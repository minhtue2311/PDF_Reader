package com.example.pdf_reader.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pdf_reader.databinding.LayoutItemPdfRecyclerViewBinding
import com.example.pdf_reader.model.PDF
import java.io.File
import java.text.SimpleDateFormat
import java.util.ArrayList
import java.util.Date
import java.util.Locale

class AdapterListPDF(private val listPDF: ArrayList<PDF>, private val onItemClick : (PDF) -> Unit) : RecyclerView.Adapter<ViewHolderListPDF>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderListPDF {
        val viewBinding = LayoutItemPdfRecyclerViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolderListPDF(viewBinding)
    }

    override fun getItemCount(): Int {
       return listPDF.size
    }

    override fun onBindViewHolder(holder: ViewHolderListPDF, position: Int) {
       val model = listPDF[position]
        holder.bindData(model)
        holder.viewBinding.layoutItem.setOnClickListener {

        }
    }
}
class ViewHolderListPDF( val viewBinding : LayoutItemPdfRecyclerViewBinding) : RecyclerView.ViewHolder(viewBinding.root){
    @SuppressLint("SetTextI18n")
    fun bindData(pdf : PDF){
        viewBinding.title.text = pdf.title
        viewBinding.date.text = formatDate(pdf.date)
        viewBinding.size.text = "${pdf.size / 1024} KB"
        Glide.with(viewBinding.previewImage)
            .load(pdf.preview)
            .into(viewBinding.previewImage)
    }
    private fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return sdf.format(Date(timestamp))
    }
}