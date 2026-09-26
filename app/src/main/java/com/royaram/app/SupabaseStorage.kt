package com.royaram.app

import android.content.Context
import android.net.Uri
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

object SupabaseStorage {

    private const val BUCKET = "royaram"

    fun uploadFile(
        context: Context,
        fileUri: Uri,
        filePath: String,
        publishableKey: String,
        onResult: (Boolean, String) -> Unit
    ) {
        Thread {
            var connection: HttpURLConnection? = null

            try {
                val url = URL(
                    "${SupabaseConfig.URL}/storage/v1/object/$BUCKET/$filePath"
                )

                connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty(
                    "apikey",
                    publishableKey
                )
                connection.setRequestProperty(
                    "Authorization",
                    "Bearer $publishableKey"
                )
                connection.setRequestProperty(
                    "Content-Type",
                    context.contentResolver.getType(fileUri)
                        ?: "application/octet-stream"
                )

                val inputStream =
                    context.contentResolver.openInputStream(fileUri)
                        ?: throw IOException("Unable to open file")

                inputStream.use { input ->
                    connection.outputStream.use { output ->
                        input.copyTo(output)
                    }
                }

                val success =
                    connection.responseCode in 200..299

                val message =
                    if (success) {
                        "فایل با موفقیت آپلود شد ❤️"
                    } else {
                        "آپلود فایل انجام نشد: ${connection.responseCode}"
                    }

                onResult(success, message)

            } catch (e: Exception) {
                onResult(
                    false,
                    e.message ?: "خطای ناشناخته"
                )
            } finally {
                connection?.disconnect()
            }
        }.start()
    }
}
