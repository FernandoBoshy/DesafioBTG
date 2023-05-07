package com.example.projetodesafiobtg.views

//import android.widget.SearchView
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.projetodesafiobtg.fragments.FragmentMain
import com.example.projetodesafiobtg.R
import com.example.projetodesafiobtg.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}