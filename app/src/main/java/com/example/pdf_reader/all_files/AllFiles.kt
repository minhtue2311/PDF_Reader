package com.example.pdf_reader.all_files

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.Bundle
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.pdf_reader.adapter.AdapterListPDF
import com.example.pdf_reader.databinding.LayoutAllFilesFragmentBinding
import com.example.pdf_reader.feature.FileManager
import com.example.pdf_reader.model.PDF
import java.io.File



class AllFiles : Fragment() {
    private lateinit var viewBinding: LayoutAllFilesFragmentBinding
    private val permissionRequestCode = 100
    private lateinit var adapter: AdapterListPDF
    private var listPdf: ArrayList<PDF> = ArrayList()
    private lateinit var fileManager: FileManager
    private lateinit var importLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        importLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    result.data?.data?.let { uri ->
                        fileManager.handleFilePdfImported(uri) { pdf ->
                            updateListPdf(pdf)
                        }
                    }
                }
            }
        fileManager = FileManager(requireActivity())
        fileManager.setImportLauncher(importLauncher)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = LayoutAllFilesFragmentBinding.inflate(inflater, container, false)
        setUpRecyclerView()
        if (allPermissionsGranted()) {
            val externalStorageDir = Environment.getExternalStorageDirectory()
            Log.d("External Storage Directory", externalStorageDir.absolutePath)
            if (externalStorageDir.exists() && externalStorageDir.isDirectory) {
                searchDir(externalStorageDir)
            } else {
                Log.e("Directory Error", "Invalid directory: ${externalStorageDir.absolutePath}")
            }
        } else {
            requestPermissions()
        }
        viewBinding.btnImport.setOnClickListener {
            fileManager.openDocumentPicker()
        }
        return viewBinding.root
    }

    private fun setUpRecyclerView() {
        viewBinding.recyclerViewPdf.setHasFixedSize(false)
        val linearLayout = LinearLayoutManager(requireContext())
        viewBinding.recyclerViewPdf.layoutManager = linearLayout
        adapter = AdapterListPDF(listPdf) { pdfFile ->
            changeToDetailPdf(pdfFile)
        }
        viewBinding.recyclerViewPdf.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun updateListPdf(pdf: PDF) {
        listPdf.add(pdf)
        adapter.notifyDataSetChanged()
    }

    private fun changeToDetailPdf(pdf: PDF) {

    }
    @SuppressLint("NotifyDataSetChanged")
    fun searchDir(dir: File) {
        if (!dir.exists() || !dir.isDirectory) {
            Log.e("Directory Error", "Invalid directory: ${dir.absolutePath}")
            return
        }
        val pdfPattern = ".pdf"
        val fileList = dir.listFiles()

        if (fileList != null) {
            if (fileList.isEmpty()) {
                Log.d("Directory Empty", "Directory is empty: ${dir.absolutePath}")
            }
            for (file in fileList) {
                if (file.isDirectory) {
                    searchDir(file)
                } else if (file.name.endsWith(pdfPattern, ignoreCase = true)) {
                    val fileName = file.name
                    val fileSize = file.length()
                    val filePath = file.absolutePath
                    val lastModified = file.lastModified()
                    val preview = createPDFPreview(filePath)
                    Log.d("PDF File", "Name: $fileName")
                    Log.d("PDF File", "Size: $fileSize bytes")
                    Log.d("PDF File", "Path: $filePath")
                    Log.d("PDF File", "Date: $lastModified")
                    val pdfFile = PDF(title = fileName, date = lastModified, data = filePath, size = fileSize, preview = preview)
                    listPdf.add(pdfFile)
                    adapter.notifyDataSetChanged()
                }
            }
        } else {
            Log.d("File List Is Null", "Directory listFiles() returned null")
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun loadPDFFiles() {
//        val contentResolver = requireActivity().contentResolver
//        val mimeType = "application/pdf"
//        val selection = MediaStore.Files.FileColumns.MIME_TYPE + "=?"
//        val selectionArgs = arrayOf(mimeType)
//        val projection = arrayOf(
//            MediaStore.Files.FileColumns._ID,
//            MediaStore.Files.FileColumns.DISPLAY_NAME,
//            MediaStore.Files.FileColumns.SIZE,
//            MediaStore.Files.FileColumns.DATE_MODIFIED,
//            MediaStore.Files.FileColumns.DATA,
//            MediaStore.Files.FileColumns.MIME_TYPE
//        )
//        val sortingOrder = "${MediaStore.Files.FileColumns.DATE_ADDED} DESC"
//
//        // Các URI cho các thư mục công cộng
//        val uris = listOf(
//            MediaStore.Files.getContentUri("external"),
//            MediaStore.Files.getContentUri("internal"),
//            MediaStore.Files.getContentUri("external_primary"),
//            MediaStore.Files.getContentUri("internal_primary")
//        )
//
//        for (uri in uris) {
//            Log.d("loadPDFFiles", "Querying URI: $uri")
//            val cursor = contentResolver.query(
//                uri,
//                projection,
//                selection,
//                selectionArgs,
//                sortingOrder
//            )
//
//            if (cursor != null) {
//                Log.d("loadPDFFiles", "Cursor is not null, found ${cursor.count} items.")
//                while (cursor.moveToNext()) {
//                    val dataColumnIndex =
//                        cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA)
//                    val path = cursor.getString(dataColumnIndex)
//                    Log.d("Data", path)
//
//                    val pdfFile = PDF(
//                        title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)),
//                        date = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATE_MODIFIED)),
//                        size = cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.SIZE)),
//                        data = path,
//                        preview = createPDFPreview(path)
//                    )
//                    listPdf.add(pdfFile)
//                }
//                cursor.close()
//            } else {
//                Log.d("loadPDFFiles", "Cursor is null, failed to query MediaStore")
//            }
//        }
//
//        Log.d("loadPDFFiles", "Total PDFs loaded: ${listPdf.size}")
//        adapter.notifyDataSetChanged()
    }

    fun createPDFPreview(filePath: String): Bitmap? {
        val file = File(filePath)
        if (!file.exists()) return null

        val fileDescriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
        val pdfRenderer = PdfRenderer(fileDescriptor)
        val page = pdfRenderer.openPage(0) // Mở trang đầu tiên

        val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
        page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
        page.close()
        pdfRenderer.close()
        fileDescriptor.close()
        return bitmap
    }


    private fun allPermissionsGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
            permissionRequestCode
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == permissionRequestCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permissions", "Permission granted")
                searchDir(Environment.getExternalStorageDirectory())
            } else {
                Log.d("Permissions", "Permission denied")
            }
        }
    }
}
