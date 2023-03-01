package com.example.projetodesafiobtg.Activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetodesafiobtg.Adapter.AdapterCurrency
import com.example.projetodesafiobtg.Model.Currencies
import com.example.projetodesafiobtg.Network.EndPoint
import com.example.projetodesafiobtg.Network.RetrofitNetwork
import com.example.projetodesafiobtg.R
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var spinnerFrom: Spinner
    private lateinit var spinnerTo: Spinner
    private lateinit var editValue: EditText
    private lateinit var textResult: TextView


    lateinit var recyclerCurrency: RecyclerView
    lateinit var currenciesList: MutableList<Currencies>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //  Listener do editValue para alteração de valor do TextView

        editValue = findViewById(R.id.editValue)
        editValue.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                println("before change")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                println("on change")
            }

            override fun afterTextChanged(p0: Editable?) {
                textResult = findViewById(R.id.textResult)
                textResult.setText("mudou o texto = $p0")

                TODO("colocar o resultado da conversão da moeda 'from' e 'to' no TextResultado")
            }

        })


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

    fun toggleGuideLine(view: View){
        val guideLine: Guideline = findViewById(R.id.guideline)
        val layoutParams = guideLine.layoutParams as ConstraintLayout.LayoutParams
        if (layoutParams.guidePercent >= 1.0f){
            layoutParams.guidePercent = 0.15f


        } else {layoutParams.guidePercent = 1.0f}

        guideLine.layoutParams = layoutParams
    }
    fun getCurrency(){
        val retrofitClient = RetrofitNetwork.getRetrofit("http://cdn.jsdelivr.net/")
        val endpoint = retrofitClient.create(EndPoint::class.java)

        endpoint.getCurrencies().enqueue(object: Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val data = mutableListOf<String>()

                response.body()?.keySet()?.iterator()?.forEach {
                    data.add(it)
                }

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("falhou")
            }

        })
    }
    fun imputFrom(view: View){
        toggleGuideLine(view)
    }
    fun imputTo(view: View){
        toggleGuideLine(view)

    }

}