package com.example.pdf_reader

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.pdf_reader.main.MainFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        replaceMainFragment()
    }
    private fun replaceMainFragment(){
        val mainFragment = MainFragment()
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.main, mainFragment)
        fragmentTrans.commit()
    }
}