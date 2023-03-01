package com.example.projetodesafiobtg.Activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetodesafiobtg.Adapter.AdapterCurrency
import com.example.projetodesafiobtg.Model.Currencies
import com.example.projetodesafiobtg.R

class MainActivity : AppCompatActivity() {

    lateinit var recyclerCurrency: RecyclerView
    lateinit var currenciesList: MutableList<Currencies>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //listagem de moedas
        currenciesList = ArrayList()
        generateCurrency()

        recyclerCurrency = findViewById(R.id.recyclerCurrency)
        // Adapter
        val adapter: AdapterCurrency = AdapterCurrency(currenciesList)

        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        recyclerCurrency.layoutManager = layoutManager
        recyclerCurrency.adapter
        recyclerCurrency.setHasFixedSize(true)
        recyclerCurrency.adapter = adapter


    }

    fun generateCurrency(){
        val currency: Currencies = Currencies(currencyName = "DOLAR", currencySign = "USD", currencyValue = "5.0")
        currenciesList.add(currency)
        val currency2: Currencies = Currencies(currencyName = "EURO", currencySign = "EUR", currencyValue = "6.3")
        currenciesList.add(currency2)
        val currency3: Currencies = Currencies(currencyName = "REAL", currencySign = "BRL", currencyValue = "1.0")
        currenciesList.add(currency3)

    }

    fun mudarGuideLine(view: View){
        val guideLine: Guideline = findViewById(R.id.guideline)
        val layoutParams = guideLine.layoutParams as ConstraintLayout.LayoutParams
        if (layoutParams.guidePercent >= 1.0f){
            layoutParams.guidePercent = 0.15f

        } else {layoutParams.guidePercent = 1.0f}

        guideLine.layoutParams = layoutParams
    }

}