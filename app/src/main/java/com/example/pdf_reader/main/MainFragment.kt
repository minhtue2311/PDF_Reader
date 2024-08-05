package com.example.pdf_reader.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.pdf_reader.R
import com.example.pdf_reader.all_files.AllFiles
import com.example.pdf_reader.databinding.LayoutMainFragmentBinding

class MainFragment : Fragment() {
    private lateinit var viewBinding: LayoutMainFragmentBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewBinding = LayoutMainFragmentBinding.inflate(inflater, container, false)
        changeToAllFiles()
        onSelectedItemMenuBar()
        return viewBinding.root
    }

    private fun onSelectedItemMenuBar() {
        viewBinding.bottomBar.setOnItemSelectedListener {
            when(it.itemId){
                R.id.all_files -> {
                    changeToAllFiles()
                    viewBinding.bottomBar.menu.findItem(R.id.all_files).isChecked = true
                }
            }
            false
        }
    }

    private fun changeToAllFiles() {
        val allFilesFragment = AllFiles()
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainLayout, allFilesFragment)
        fragmentTransaction.commit()
    }

}