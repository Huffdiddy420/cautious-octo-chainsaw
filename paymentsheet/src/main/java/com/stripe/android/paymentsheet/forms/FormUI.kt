package com.stripe.android.paymentsheet.forms

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.viewinterop.AndroidView
import com.stripe.android.paymentsheet.R
import com.stripe.android.ui.core.elements.AfterpayClearpayElementUI
import com.stripe.android.ui.core.elements.AfterpayClearpayHeaderElement
import com.stripe.android.ui.core.elements.FormElement
import com.stripe.android.ui.core.elements.IdentifierSpec
import com.stripe.android.ui.core.elements.JsEngine
import com.stripe.android.ui.core.elements.SaveForFutureUseElement
import com.stripe.android.ui.core.elements.SaveForFutureUseElementUI
import com.stripe.android.ui.core.elements.SectionElement
import com.stripe.android.ui.core.elements.SectionElementUI
import com.stripe.android.ui.core.elements.StaticElementUI
import com.stripe.android.ui.core.elements.StaticTextElement
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import kotlinx.serialization.decodeFromString

@FlowPreview
@Composable
internal fun Form(formViewModel: FormViewModel) {
    val completedValues by formViewModel.completeFormValues.collectAsState(initial = null)

    FormInternal(
        formViewModel.hiddenIdentifiers,
        formViewModel.enabled,
        formViewModel.jsEngine.elements
    )
    WebPageScreen(formViewModel.jsEngine, completedValues)
//    FormInternal(
//        formViewModel.hiddenIdentifiers,
//        formViewModel.enabled,
//        formViewModel.elements
//    )
}

internal class MyWebViewClient : WebViewClient() {
    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
        if (request?.url?.schemeSpecificPart?.contains("lake-universal-skate.glitch.me/") == true) {
            view?.loadUrl(request.url.toString(), mapOf("Access-Control-Allow-Origin" to "*"))
            return false
        } else {
            return true
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
internal fun WebPageScreen(jsEngine: JsEngine, formFieldValues: FormFieldValues?) {

    val jsRequest by jsEngine.requestFlow.collectAsState(initial = null)
    val jsRawRequest by jsEngine.rawJsFlow.collectAsState(initial = null)

    var webView by remember { mutableStateOf<WebView?>(null) }
    AndroidView(
        factory = { context ->
            WebView(context).also { wv ->
                wv.settings.javaScriptEnabled = true
                wv.settings.blockNetworkLoads = false
                wv.settings.allowFileAccessFromFileURLs = true
                wv.settings.allowContentAccess = true
                wv.settings.allowUniversalAccessFromFileURLs = true
                wv.settings.mixedContentMode = MIXED_CONTENT_ALWAYS_ALLOW
                wv.addJavascriptInterface(WebAppInterface(jsEngine), "Android")
                wv.webViewClient = MyWebViewClient()
                wv.webChromeClient = object : WebChromeClient() {

                    override fun onConsoleMessage(message: ConsoleMessage): Boolean {
                        Log.d(
                            "MLB", "${message.message()} -- From line " +
                                "${message.lineNumber()}"
                        )// of ${message.sourceID()}")
                        return true
                    }
                }
                wv.loadUrl("http://10.0.2.2:8080")
                webView = wv
            }
        },
        modifier = Modifier.fillMaxSize()
    )

    LaunchedEffect(jsRequest) {
        webView?.let { wv ->
            jsRequest?.let { jsString ->
                wv.evaluateJavascript(jsString) {
                    if (it != "null") {
                        jsEngine.addResponse(it)
                    }
                }
            }
        }
    }
    LaunchedEffect(jsRawRequest) {
        Log.e("MLB", "raw js request: $jsRawRequest")
        webView?.let { wv ->
            jsRawRequest?.let { jsString ->
                wv.evaluateJavascript(jsString) {
                    //handle response
                }
            }
        }
    }
}

/** Instantiate the interface and set the context  */
class WebAppInterface(val jsEngine: JsEngine) {
    val format = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @JavascriptInterface
    fun mount(serializedData: String) {
        jsEngine.mount(serializedData)
    }
    @JavascriptInterface
    fun decode(serializedData: String) {
        jsEngine.decode(serializedData)
    }
    @JavascriptInterface
    fun updateProps(id: String, props: String) {
        Log.e("MLB", "Update props ${id}, ${props}")
        jsEngine.updateProps(id, props)
    }

    @JavascriptInterface
    fun insertChild(serializedData: String) {
        jsEngine.insertChild(serializedData)
    }

    @JavascriptInterface
    fun removeChild(serializedData: String) {
        jsEngine.removeChild(serializedData)
    }

    @JavascriptInterface
    fun apply(type: String, property: String, value: String) {
        Log.e("MLB", "HERE APPLY- type: $type, property: $property, value: $value");
    }

    /** Show a toast from the web page  */
    @JavascriptInterface
    fun response(response: String) {
        Log.e("MLB", "ui response: $response")
//        jsEngine.addResponse(
//            response.replace("\\", "")
//                .replace(Regex("^\""), "")
//                .replace(Regex("\"$"), "")
//        )
    }
    @JavascriptInterface
    fun post(endpoint: String, request: String) : String{
        val url = URL("https://lake-universal-skate.glitch.me$endpoint")
        val httpURLConnection = url.openConnection() as HttpURLConnection
        httpURLConnection.requestMethod = "POST"
        httpURLConnection.setRequestProperty("Content-Type", "application/json") // The format of the content we're sending to the server
        httpURLConnection.setRequestProperty("Accept", "application/json") // The format of response we want to get from the server
        httpURLConnection.doInput = true
        httpURLConnection.doOutput = true

        Log.e("MLB", "Request: $request")
        // Send the JSON we created
        val outputStreamWriter = OutputStreamWriter(httpURLConnection.outputStream)
        outputStreamWriter.write(request)
        outputStreamWriter.flush()

        // Check if the connection is successful
        val responseCode = httpURLConnection.responseCode
        return if (responseCode == HttpURLConnection.HTTP_OK) {
            val response = httpURLConnection.inputStream.bufferedReader()
                .use { it.readText() }  // defaults to UTF-8
            response
        } else {
            "HTTPURLCONNECTION_ERROR: $responseCode"
        }
    }
}

@Composable
internal fun FormInternal(
    hiddenIdentifiersFlow: Flow<List<IdentifierSpec>>,
    enabledFlow: Flow<Boolean>,
    elementsFlow: Flow<List<FormElement>?>
) {
    val hiddenIdentifiers by hiddenIdentifiersFlow.collectAsState(emptyList())
    val enabled by enabledFlow.collectAsState(true)
    val elements by elementsFlow.collectAsState(null)

    Column(
        modifier = Modifier.fillMaxWidth(1f)

    ) {
        elements?.let {
            it.forEach { element ->
                if (!hiddenIdentifiers.contains(element.identifier)) {
                    when (element) {
                        is SectionElement -> SectionElementUI(enabled, element, hiddenIdentifiers)
                        is StaticTextElement -> StaticElementUI(element)
                        is SaveForFutureUseElement -> SaveForFutureUseElementUI(
                            enabled,
                            element
                        )
                        is AfterpayClearpayHeaderElement -> AfterpayClearpayElementUI(
                            enabled,
                            element
                        )
                    }
                }
            }
        } ?: Row(
            modifier = Modifier
                .height(
                    dimensionResource(R.dimen.stripe_paymentsheet_loading_container_height)
                )
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(
                    dimensionResource(R.dimen.stripe_paymentsheet_loading_indicator_size)
                ),
                color = if (isSystemInDarkTheme()) {
                    Color.LightGray
                } else {
                    Color.Black
                },
                strokeWidth = dimensionResource(
                    R.dimen.stripe_paymentsheet_loading_indicator_stroke_width
                )
            )
        }
    }
}
