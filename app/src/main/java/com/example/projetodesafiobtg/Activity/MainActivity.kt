package com.example.projetodesafiobtg.Activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.SearchView
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
    private lateinit var searchRecyclerView: SearchView


    lateinit var recyclerCurrency: RecyclerView
    lateinit var currenciesList: MutableList<Currencies>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editValue = findViewById(R.id.editValue)
        spinnerFrom = findViewById(R.id.spinnerFrom)
        spinnerTo = findViewById(R.id.spinnerTo)
        textResult = findViewById(R.id.textResult)
        searchRecyclerView = findViewById(R.id.recyclesearch)

        currenciesList = ArrayList()
        getCurrency()

        //  Listener do editValue para alteração de valor do TextView
        editValue.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                println("before change")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                println("on change")
            }

            override fun afterTextChanged(p0: Editable?) {
                convertMoney()
            }

        })

        recyclerCurrency = findViewById(R.id.recyclerCurrency)

        // Adapter do recyclerView

        val adapterRecycler: AdapterCurrency = AdapterCurrency(currenciesList)
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(applicationContext)
        recyclerCurrency.layoutManager = layoutManager
        recyclerCurrency.adapter
        recyclerCurrency.setHasFixedSize(true)
        recyclerCurrency.adapter = adapterRecycler


    }

    fun toggleGuideLine(view: View){
        val guideLine: Guideline = findViewById(R.id.guideline)
        val layoutParams = guideLine.layoutParams as ConstraintLayout.LayoutParams
        if (layoutParams.guidePercent >= 1.0f){
            layoutParams.guidePercent = 0.15f


        } else {layoutParams.guidePercent = 1.0f}

        guideLine.layoutParams = layoutParams
    }
    fun convertMoney(){
        val retrofitClient = RetrofitNetwork.getRetrofit("https://cdn.jsdelivr.net/")
        val endpoint = retrofitClient.create(EndPoint::class.java)

        endpoint.getCurrencyRate(spinnerFrom.selectedItem.toString(), spinnerTo.selectedItem.toString()).enqueue(object :
            Callback<JsonObject>{
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                var data = response.body()?.entrySet()?.find { it.key == spinnerTo.selectedItem.toString() }
                val rate: Double = data?.value.toString().toDouble()
                var conversion = editValue.text.toString().toDouble() * rate
                textResult.setText(conversion.toString())
                println(data)

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("não deu")
            }

        })

    }
    fun getCurrency(){
        val retrofitClient = RetrofitNetwork.getRetrofit("https://cdn.jsdelivr.net/")
        val endpoint = retrofitClient.create(EndPoint::class.java)

        endpoint.getCurrencies().enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                val data = mutableListOf<String>()

                response.body()?.keySet()?.iterator()?.forEach {
                    data.add(it)
                }

                response.body()?.entrySet()?.iterator()?.forEach {
                    var key = it.key
                    var value = it.value
                    currenciesList.add(Currencies(value.toString(), key.toString()))


                }


                //  adapter do Spinner
                val posBRL = data.indexOf("brl")
                val posUSD = data.indexOf("usd")
                val adapterSp = ArrayAdapter(baseContext, android.R.layout.simple_spinner_dropdown_item, data)
                spinnerFrom.adapter = adapterSp
                spinnerTo.adapter = adapterSp
                spinnerFrom.setSelection(posBRL)
                spinnerTo.setSelection(posUSD)

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("falhou")
            }

        })
    }

}