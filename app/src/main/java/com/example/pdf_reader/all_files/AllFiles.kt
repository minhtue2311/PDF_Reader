package com.example.pdf_reader.all_files

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pdf_reader.databinding.LayoutAllFilesFragmentBinding

class AllFiles : Fragment() {
    private lateinit var viewBinding : LayoutAllFilesFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = LayoutAllFilesFragmentBinding.inflate(inflater, container, false)
        return viewBinding.root
    }
}