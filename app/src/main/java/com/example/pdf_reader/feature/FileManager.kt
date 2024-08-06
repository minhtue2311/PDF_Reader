package com.example.pdf_reader.feature

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.example.pdf_reader.model.PDF
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FileManager(private val activity: Activity) {
    private lateinit var importLauncher: ActivityResultLauncher<Intent>
    fun setImportLauncher(importLauncher : ActivityResultLauncher<Intent>){
        this.importLauncher = importLauncher
    }
    fun openDocumentPicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        importLauncher.launch(intent)
    }
    fun handleFilePdfImported(uri: Uri, callBack : (PDF) -> Unit) {
        try {
            activity.contentResolver.openFileDescriptor(uri, "r")
                ?.use { parcelFileDescriptor ->
                    PdfRenderer(parcelFileDescriptor).use { pdfRenderer ->
                        if (pdfRenderer.pageCount > 0) {
                            val page = pdfRenderer.openPage(0)
                            val bitmap = Bitmap.createBitmap(
                                page.width,
                                page.height,
                                Bitmap.Config.ARGB_8888
                            )
                            page.render(
                                bitmap,
                                null,
                                null,
                                PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY
                            )
                            page.close()

                            // Save the bitmap to a file or use it as needed
                            val fileName = getFileInfo(uri)
                            val pdfFile = PDF(
                                title = fileName.first,
                                date = fileName.second,  // Update this to get the actual date
                                size = parcelFileDescriptor.statSize,
                                data = uri.toString(),
                                preview = bitmap
                            )
                            callBack(pdfFile)
                        }
                    }
                }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(activity, "Error loading PDF: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun getFileInfo(uri: Uri): Pair<String, Long> {
        var name = ""
        var dateModified: Long = 0

        // Try to get file name
        activity.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) {
                name = cursor.getString(nameIndex)
            }
        }

        // Try to get last modified date
        try {
            activity.contentResolver.openInputStream(uri)?.use { inputStream ->
                val file = File(activity.cacheDir, getFileName(uri))
                FileOutputStream(file).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                dateModified = file.lastModified()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return Pair(name, dateModified)
    }
    private fun getFileName(uri: Uri): String {
        var name = ""
        activity.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex != -1) {
                name = cursor.getString(nameIndex)
            }
        }
        return name
    }
}