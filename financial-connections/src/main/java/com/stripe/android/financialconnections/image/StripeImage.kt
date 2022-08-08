package com.stripe.android.financialconnections.image

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.Dp.Companion.Infinity
import androidx.compose.ui.unit.IntSize.Companion.Zero
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.io.InputStream
import java.net.URL

@Composable
internal fun StripeImage(
    url: String,
    placeHolder: Painter,
    memoryCache: LruCache<String, Bitmap> = imageMemoryCache,
    contentDescription: String? = null,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier) {
        val (width, height) = calculateBoxSize()
        val painter: MutableState<Painter> =
            remember { mutableStateOf(BitmapPainter(ImageBitmap(width, height))) }
        var loadImageJob: Job?
        val scope = rememberCoroutineScope()

        DisposableEffect(url) {
            val imageLoader = ImageLoader()
            loadImageJob = scope.launch(Dispatchers.Default) {
                painter.value = memoryCache.get(url)
                    ?.let { BitmapPainter(it.asImageBitmap()) }
                    ?: imageLoader.load(url, width, height)
                        .fold(
                            onSuccess = { bitmap ->
                                debug("Image loaded")
                                memoryCache.put(url, bitmap)
                                BitmapPainter(bitmap.asImageBitmap())
                            },
                            onFailure = {
                                debug("Image failed loading")
                                it.printStackTrace()
                                placeHolder
                            }
                        )

            }
            onDispose {
                painter.value = BitmapPainter(ImageBitmap(width, height))
                imageLoader.cancel()
                loadImageJob?.cancel()
            }
        }
        Image(
            modifier = modifier,
            contentDescription = contentDescription,
            painter = painter.value
        )
    }
}

fun debug(message: String) {
    Log.d("StripeImage", message)
}

private fun BoxWithConstraintsScope.calculateBoxSize(): Pair<Int, Int> {
    var width =
        if (constraints.maxWidth > Zero.width &&
            constraints.maxWidth < Infinity.value.toInt()
        ) {
            constraints.maxWidth
        } else {
            -1
        }

    var height =
        if (constraints.maxHeight > Zero.height &&
            constraints.maxHeight < Infinity.value.toInt()
        ) {
            constraints.maxHeight
        } else {
            -1
        }

    // if height xor width not able to be determined, make image a square of the determined dimension
    if (width == -1) width = height
    if (height == -1) height = width
    return Pair(width, height)
}

// TODO@carlosmuvi scope this to SDK lifecycle and remove static reference.
private val imageMemoryCache = object : LruCache<String, Bitmap>(
    // Use 1/8th of the available memory for this memory cache.
    (Runtime.getRuntime().maxMemory() / 1024).toInt() / 8
) {
    override fun sizeOf(key: String, bitmap: Bitmap): Int {
        return bitmap.byteCount / 1024
    }
}

private class ImageLoader {
    var currentInputStream: InputStream? = null

    fun load(url: String, width: Int, height: Int): Result<Bitmap> {
        return kotlin.runCatching {
            BitmapFactory.Options().run {
                // First decode with inJustDecodeBounds=true to check dimensions
                inJustDecodeBounds = true
                BitmapFactory.decodeStream(
                    URL(url).openStream().also { stream -> currentInputStream = stream },
                    null,
                    this
                )
                // Calculate inSampleSize
                inSampleSize = calculateInSampleSize(this, width, height)
                // Decode bitmap with inSampleSize set
                inJustDecodeBounds = false
                BitmapFactory.decodeStream(
                    URL(url).openStream().also { stream -> currentInputStream = stream },
                    null,
                    this
                )!!
            }
        }
    }

    fun cancel() {
        currentInputStream?.close()
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        // Raw height and width of image
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }
}
