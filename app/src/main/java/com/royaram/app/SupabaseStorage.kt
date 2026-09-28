package com.royaram.app

import android.content.Context
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import org.json.JSONObject
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

object SupabaseStorage {

    private const val FUNCTION_URL =
        "https://yinitizbfaojyrpqzxvo.supabase.co/functions/v1/smooth-api"

    fun uploadFile(
        context: Context,
        fileUri: Uri,
        filePath: String,
        onResult: (Boolean, String) -> Unit
    ) {

        val currentUser =
            FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            onResult(
                false,
                "کاربر وارد حساب نشده است"
            )
            return
        }

        currentUser.getIdToken(true)
            .addOnSuccessListener { result ->

                val firebaseToken = result.token

                if (firebaseToken.isNullOrBlank()) {
                    onResult(
                        false,
                        "Firebase توکن خالی برگرداند"
                    )
                    return@addOnSuccessListener
                }

                Thread {

                    var connection: HttpURLConnection? = null

                    try {

                        val contentResolver =
                            context.contentResolver

                        val inputStream =
                            contentResolver.openInputStream(fileUri)
                                ?: throw Exception(
                                    "فایل قابل خواندن نیست"
                                )

                        val mimeType =
                            contentResolver.getType(fileUri)
                                ?: "application/octet-stream"

                        val fileName =
                            fileUri.lastPathSegment
                                ?: "upload_file"

                        val boundary =
                            "----RoyaramBoundary${System.currentTimeMillis()}"

                        val url =
                            URL(FUNCTION_URL)

                        connection =
                            url.openConnection() as HttpURLConnection

                        connection.requestMethod = "POST"
                        connection.doOutput = true
                        connection.doInput = true
                        connection.useCaches = false

                        connection.connectTimeout = 30000
                        connection.readTimeout = 60000

                        connection.setRequestProperty(
                            "Authorization",
                            "Bearer $firebaseToken"
                        )

                        connection.setRequestProperty(
                            "Content-Type",
                            "multipart/form-data; boundary=$boundary"
                        )

                        val output =
                            DataOutputStream(
                                connection.outputStream
                            )

                        output.writeBytes(
                            "--$boundary\r\n"
                        )

                        output.writeBytes(
                            "Content-Disposition: form-data; name=\"filePath\"\r\n\r\n"
                        )

                        output.writeBytes(filePath)
                        output.writeBytes("\r\n")

                        output.writeBytes(
                            "--$boundary\r\n"
                        )

                        output.writeBytes(
                            "Content-Disposition: form-data; name=\"contentType\"\r\n\r\n"
                        )

                        output.writeBytes(mimeType)
                        output.writeBytes("\r\n")

                        output.writeBytes(
                            "--$boundary\r\n"
                        )

                        output.writeBytes(
                            "Content-Disposition: form-data; name=\"file\"; filename=\"$fileName\"\r\n"
                        )

                        output.writeBytes(
                            "Content-Type: $mimeType\r\n\r\n"
                        )

                        inputStream.use { input ->

                            val buffer =
                                ByteArray(8192)

                            var bytesRead: Int

                            while (
                                input.read(buffer)
                                    .also {
                                        bytesRead = it
                                    } != -1
                            ) {

                                output.write(
                                    buffer,
                                    0,
                                    bytesRead
                                )
                            }
                        }

                        output.writeBytes("\r\n")

                        output.writeBytes(
                            "--$boundary--\r\n"
                        )

                        output.flush()
                        output.close()

                        val responseCode =
                            connection.responseCode

                        val responseText =
                            try {

                                val stream =
                                    if (responseCode in 200..299) {
                                        connection.inputStream
                                    } else {
                                        connection.errorStream
                                    }

                                stream
                                    ?.bufferedReader()
                                    ?.use {
                                        it.readText()
                                    }
                                    ?: ""

                            } catch (_: Exception) {
                                ""
                            }

                        if (responseCode in 200..299) {

                            onResult(
                                true,
                                "فایل با موفقیت آپلود شد ❤️"
                            )

                        } else {

                            onResult(
                                false,
                                "آپلود انجام نشد: HTTP $responseCode\n$responseText"
                            )
                        }

                    } catch (e: Exception) {

                        onResult(
                            false,
                            "خطای آپلود:\n${e.message ?: "خطای ناشناخته"}"
                        )

                    } finally {

                        connection?.disconnect()
                    }
                }.start()
            }

            .addOnFailureListener { error ->

                onResult(
                    false,
                    "دریافت توکن Firebase انجام نشد:\n${error.message ?: "خطای ناشناخته"}"
                )
            }
    }


    fun getSignedUrl(
        context: Context,
        filePath: String,
        onResult: (Boolean, String) -> Unit
    ) {

        val currentUser =
            FirebaseAuth.getInstance().currentUser

        if (currentUser == null) {
            onResult(
                false,
                "کاربر وارد حساب نشده است"
            )
            return
        }

        currentUser.getIdToken(true)
            .addOnSuccessListener { result ->

                val firebaseToken = result.token

                if (firebaseToken.isNullOrBlank()) {

                    onResult(
                        false,
                        "Firebase توکن خالی برگرداند"
                    )

                    return@addOnSuccessListener
                }

                Thread {

                    var connection: HttpURLConnection? = null

                    try {

                        val url =
                            URL(FUNCTION_URL)

                        connection =
                            url.openConnection() as HttpURLConnection

                        connection.requestMethod = "POST"
                        connection.doOutput = true
                        connection.doInput = true
                        connection.useCaches = false

                        connection.connectTimeout = 30000
                        connection.readTimeout = 30000

                        connection.setRequestProperty(
                            "Authorization",
                            "Bearer $firebaseToken"
                        )

                        connection.setRequestProperty(
                            "Content-Type",
                            "application/json"
                        )

                        val body =
                            JSONObject().apply {
                                put(
                                    "action",
                                    "signedUrl"
                                )
                                put(
                                    "filePath",
                                    filePath
                                )
                            }.toString()

                        connection.outputStream.use { output ->

                            output.write(
                                body.toByteArray(
                                    Charsets.UTF_8
                                )
                            )

                            output.flush()
                        }

                        val responseCode =
                            connection.responseCode

                        val responseText =
                            try {

                                val stream =
                                    if (responseCode in 200..299) {
                                        connection.inputStream
                                    } else {
                                        connection.errorStream
                                    }

                                stream
                                    ?.bufferedReader()
                                    ?.use {
                                        it.readText()
                                    }
                                    ?: ""

                            } catch (_: Exception) {
                                ""
                            }

                        if (responseCode in 200..299) {

                            try {

                                val json =
                                    JSONObject(responseText)

                                val signedUrl =
                                    json.optString(
                                        "signedUrl",
                                        ""
                                    )

                                if (signedUrl.isNotBlank()) {

                                    onResult(
                                        true,
                                        signedUrl
                                    )

                                } else {

                                    onResult(
                                        false,
                                        "لینک امن عکس دریافت نشد"
                                    )
                                }

                            } catch (e: Exception) {

                                onResult(
                                    false,
                                    "پاسخ سرور نامعتبر است"
                                )
                            }

                        } else {

                            onResult(
                                false,
                                "دریافت عکس انجام نشد: HTTP $responseCode\n$responseText"
                            )
                        }

                    } catch (e: Exception) {

                        onResult(
                            false,
                            "خطای دریافت عکس:\n${e.message ?: "خطای ناشناخته"}"
                        )

                    } finally {

                        connection?.disconnect()
                    }

                }.start()
            }

            .addOnFailureListener { error ->

                onResult(
                    false,
                    "دریافت توکن Firebase انجام نشد:\n${error.message ?: "خطای ناشناخته"}"
                )
            }
    }
}
