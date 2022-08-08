package com.stripe.android.ui.core.elements

import android.util.Log
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stripe.android.ui.core.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
sealed class JsRequest {

    @Serializable
    data class Event(
        val target: Target
    ) : JsRequest()

    @Serializable
    data class Target(
        val id: String,
        val value: String? = null,
        var parent: Target? = null,
        var children: List<Target> = emptyList()
    )
}

@Serializable
data class JsResponse(
    val id: String,
    val field: String,
    val label: String,
    val rawFieldValue: String,
    val state: JsTextFieldState
)

@Serializable
data class Function(
    val __f: String
)

@Serializable
data class SerializedData(
    val id: String,
    val kind: String,
    val type: String = "",
    val props: Map<String, String>? = null, // For the object deserialize not mount needs to be Function for onTextChange
    val children: ArrayList<SerializedData> = arrayListOf()
)

class JsEngine(
    val _responseFlow: MutableSharedFlow<JsResponse> = MutableSharedFlow(),
    val responseFlow: SharedFlow<JsResponse> = _responseFlow,
    private val _requestFlow: MutableSharedFlow<String?> = MutableSharedFlow(),
    val requestFlow: SharedFlow<String?> = _requestFlow,
    private val _rawJsFlow: MutableSharedFlow<String?> = MutableSharedFlow(),
    val rawJsFlow: SharedFlow<String?> = _rawJsFlow,
) : ViewModel() {
    val format = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    // Initial value is null while loading in the background
    val _elements = MutableStateFlow<List<FormElement>?>(null)
    val elements: StateFlow<List<FormElement>?> = _elements

    val idToControllerMap = HashMap<String, TextFieldController>()

    fun updateProps(id: String, propsString: String) {
        val deserializedPropMap = format.decodeFromString<Map<String, String>>(propsString)
        //get the controller for the element that matches and call
        // set value
        viewModelScope.launch {
            _responseFlow.emit(
                JsResponse(
                    id,
                    deserializedPropMap["myValue"] ?: "",
                    "label",
                    deserializedPropMap["myValue"] ?: "",
                    JsTextFieldState(
                        shouldShowErrorHasFocus = deserializedPropMap["shouldShowErrorHasFocus"].toBoolean(),
                        shouldShowErrorHasNoFocus = deserializedPropMap["shouldShowErrorHasNoFocus"].toBoolean(),
                        full = deserializedPropMap["full"].toBoolean(),
                        blank = deserializedPropMap["blank"].toBoolean(),
                        valid = deserializedPropMap["valid"].toBoolean(),
                        errorMsg = deserializedPropMap["errorMsg"]
                    )
                )
            )
        }
    }

    fun decode(serializedData: String) {
        Log.e("MLB", "decoding string: $serializedData")
        var decodedDataMaybe: SerializedData? = null
        kotlin.runCatching {
            decodedDataMaybe =
                format.decodeFromString<SerializedData>(serializedData.replace("@", "_"))
        }.onFailure { }
            .onSuccess {
                when (decodedDataMaybe) {
                    null -> {
                    }
                    else -> {
                        val decodedData = decodedDataMaybe!!
                        Log.e("MLB", "decoding string complete: $serializedData")
                        if (decodedData.type == "TextField") {
                            _elements.value = listOf(
                                when (decodedData.type) {
                                    "TextField" -> {
                                        val fieldElement = JsSimpleTextElement(
                                            IdentifierSpec.Generic(decodedData.id),
                                            JsTextFieldController(
                                                this,
                                                responseFlow.filter { it.id == decodedData.id },
                                                SimpleTextFieldConfig(
                                                    label = R.string.label_placeholder,
                                                    capitalization = KeyboardCapitalization.None,
                                                    keyboard = KeyboardType.Ascii
                                                ),
                                                showOptionalLabel = false,
                                                identifierSpec = IdentifierSpec.Generic("Idontknow"),
//                                                textChangeFnId = decodedData.props?.get("onTextChange")?.__f
                                            )
                                        )
                                        idToControllerMap[decodedData.id] = fieldElement.controller
                                        SectionElement(
                                            IdentifierSpec.Generic(decodedData.id + "section"),
                                            fieldElement,
                                            SectionController(
                                                null,
                                                listOf(fieldElement.sectionFieldErrorController())
                                            )
                                        )
                                    }
                                    else -> {
                                        null
                                    }
                                }

                            ).filterNotNull()

                            // dom receiver is using type to convert to an element of sorts
                            Log.e("MLB", "Decoded data: $decodedData")

                        }
                        //        viewModelScope.launch {
                        //            val fullJavascript =
                        //                "(function() { console.log('Is ui-text-field undefined: ' + document.querySelector('ui-text-field') == undefined); })();"
                        //            Log.e("MLB", "full javascript to execute: $fullJavascript")
                        //            _requestFlow.emit(fullJavascript)
                        //        }
                    }
                }
            }

    }

    fun insertChild(serializedData: String) {
        Log.e("MLB", "decoding string: $serializedData")
        var decodedDataMaybe: SerializedData? = null
        kotlin.runCatching {
            decodedDataMaybe = format.decodeFromString<SerializedData>(serializedData)
        }.onFailure { }
            .onSuccess {
                when (decodedDataMaybe) {
                    null -> {
                    }
                    else -> {
                        val decodedData = decodedDataMaybe!!
                        Log.e("MLB", "decoding string complete: $serializedData")
                        _elements.value = listOf(
                            when (decodedData.type) {
                                else -> {
                                    val fieldElement = JsSimpleTextElement(
                                        IdentifierSpec.Generic(decodedData.id),
                                        JsTextFieldController(
                                            this,
                                            responseFlow.filter { it.id == decodedData.id },
                                            SimpleTextFieldConfig(
                                                label = if (decodedData.props?.get("label") == "Name") {
                                                    R.string.name
                                                } else if (decodedData.props?.get("label") == "Email") {
                                                    R.string.email
                                                } else {
                                                    R.string.label_placeholder
                                                },
                                                capitalization = KeyboardCapitalization.None,
                                                keyboard = KeyboardType.Ascii
                                            ),
                                            showOptionalLabel = false,
                                            identifierSpec = IdentifierSpec.Generic("Idontknow"),
                                            jsId = decodedData.id
                                        )
                                    )
                                    SectionElement(
                                        IdentifierSpec.Generic(decodedData.id + "section"),
                                        fieldElement,
                                        SectionController(
                                            null,
                                            listOf(fieldElement.sectionFieldErrorController())
                                        )
                                    )
                                }
                            }

                        )

                        // dom receiver is using type to convert to an element of sorts
                        Log.e("MLB", "Decoded data: $decodedData")


                        //        viewModelScope.launch {
                        //            val fullJavascript =
                        //                "(function() { console.log('Is ui-text-field undefined: ' + document.querySelector('ui-text-field') == undefined); })();"
                        //            Log.e("MLB", "full javascript to execute: $fullJavascript")
                        //            _requestFlow.emit(fullJavascript)
                        //        }
                    }
                }
            }

    }

    fun removeChild(serializedData: String) {
        val decodedData = format.decodeFromString<ArrayList<SerializedData>>(serializedData)


    }

    fun mount(serializedJsonStr: String) {
        val decodedData = format.decodeFromString<ArrayList<SerializedData>>(serializedJsonStr)

        _elements.value = decodedData.map { serializedData ->
            when (serializedData.type) {
                "TextField" -> {
                    val fieldElement = JsSimpleTextElement(
                        IdentifierSpec.Generic(serializedData.id),
                        JsTextFieldController(
                            this,
                            responseFlow.filter { it.id == serializedData.id },
                            SimpleTextFieldConfig(
                                label = R.string.label_placeholder,
                                capitalization = KeyboardCapitalization.None,
                                keyboard = KeyboardType.Ascii
                            ),
                            showOptionalLabel = false,
                            identifierSpec = IdentifierSpec.Generic("Idontknow"),
                            jsId = serializedData.id
                        )
                    )
                    SectionElement(
                        IdentifierSpec.Generic(serializedData.id + "section"),
                        fieldElement,
                        SectionController(
                            null,
                            listOf(fieldElement.sectionFieldErrorController())
                        )
                    )
                }
                else -> null
            }
        }.filterNotNull() as List<FormElement>

        // dom receiver is using type to convert to an element of sorts
        Log.e("MLB", "Decoded data: $decodedData")
    }

    fun runJavascript(javascript: String) {
//                "(function(e) ${javascript})(${format.encodeToString(event)});"
//        _requestFlow.emit(javascript)

        viewModelScope.launch {
            _rawJsFlow.emit(javascript)
        }
    }

    fun requestCustomServer(javascript: String, event: JsRequest.Event) {
        viewModelScope.launch {
            Log.e("MLB", "jsEngine request: ${format.encodeToString(event)}")
            val fullJavascript = "javascript:" +
                "(function(e) ${javascript})(${format.encodeToString(event)});"

//                    "function evaluate() {" +
//                    "      Android.response(Android.post(JSON.stringify(" +
//                    "    ${format.encodeToString(event)}" +
//                    ")));" +
//                "javascript:" +
//                    "function evaluate(msg) {" +
////                    "   Android.response(JSON.stringify(" +
////                    "{" +
////                    "       id: \"tst\"," +
////                    "       field: msg[\"userInputValue\"]," +
////                    "       rawFieldValue: \"false\"," +
////                    "       state: {" +
////                    "           shouldShowErrorHasFocus: true," +
////                    "           shouldShowErrorHasNoFocus: false," +
////                    "           full: false," +
////                    "           blank: false," +
////                    "           valid: false," +
////                    "           errorMsg: \"This is the js error message\"," +
////                    "       }" +
////                    "   }))" +
//                "};" +
//                "evaluate(${format.encodeToString(event)});\n";
            Log.e("MLB", "full javascript to execute: $fullJavascript")
            _requestFlow.emit(fullJavascript)
        }
    }

    fun observe(id: IdentifierSpec) = responseFlow.filter { it.id == id.value }

    fun addResponse(string: String) {
        viewModelScope.launch {
            Log.e(
                "MLB",
                "jsEngine return: ${string}"
            )

            try {
                _responseFlow.emit(format.decodeFromString(string))
            } catch (e: Exception) {
                Log.e("MLB", "error decoding: $string: " + e.message)
            }
        }
    }
}
