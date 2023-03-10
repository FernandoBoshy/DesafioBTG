package com.example.projetodesafiobtg.views

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
//import android.widget.SearchView
import androidx.appcompat.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.Guideline
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetodesafiobtg.Adapter.AdapterCurrency
import com.example.projetodesafiobtg.Model.Currencies
import com.example.projetodesafiobtg.Network.EndPoint
import com.example.projetodesafiobtg.Network.RetrofitNetwork
import com.example.projetodesafiobtg.R
import com.example.projetodesafiobtg.databinding.ActivityMainBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var currenciesList: MutableList<Currencies>
    lateinit var CurrenciesListTemp: MutableList<Currencies>
    lateinit var adapter: AdapterCurrency

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currenciesList = ArrayList()
        adapter = AdapterCurrency(currenciesList)

        val data2 = mutableListOf<String>()


        //  Listener do editValue para alteração de valor do TextView
        binding.editValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                println("before change")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                println("on change")
            }

            override fun afterTextChanged(p0: Editable?) {
                if (!p0.isNullOrEmpty()) {
                    convertMoney()
                }

            }
        })

        binding.recyclerCurrency.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            setHasFixedSize(true)
            adapter = AdapterCurrency(currenciesList)
        }

        binding.searchinside.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                println(currenciesList.count())
                filterList(newText)
                println(currenciesList.count() +1)
                return true
            }

        })
        binding.searchFrom.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                TODO("Not yet implemented")
            }

        })

        getCurrency(data2)
    }


    fun filterList(query: String?) {
        var tempFilterListVar = currenciesList
        if (query != null) {
            var filterListVar = ArrayList<Currencies>()

            for (item in currenciesList) {
                if (item.currencySign?.lowercase(Locale.ROOT)?.contains(query.lowercase())!!) {
                    filterListVar.add(item)
                }
            }
            if (filterListVar.isNotEmpty()) {
                adapter.setFilterList(filterListVar)
                println(filterListVar.toString())
            }
        }
    }

    fun toggleGuideLine(view: View) {
        val guideLine: Guideline = findViewById(R.id.guideline)
        val layoutParams = guideLine.layoutParams as ConstraintLayout.LayoutParams
        if (layoutParams.guidePercent >= 1.0f) {
            layoutParams.guidePercent = 0.15f


        } else {
            layoutParams.guidePercent = 1.0f
        }

        guideLine.layoutParams = layoutParams
    }

    fun convertMoney() {
        val retrofitClient = RetrofitNetwork.getRetrofit("https://cdn.jsdelivr.net/")
        val endpoint = retrofitClient.create(EndPoint::class.java)

        endpoint.getCurrencyRate(
            binding.spinnerFrom.selectedItem.toString(),
            binding.spinnerTo.selectedItem.toString()
        ).enqueue(object :
            Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                var data = response.body()?.entrySet()
                    ?.find { it.key == binding.spinnerTo.selectedItem.toString() }
                val rate: Double = data?.value.toString().toDouble()
                var conversion = binding.editValue.text.toString().toDouble() * rate
                binding.textResult.text = conversion.toString()
                println(data)

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("não deu")
            }

        })

    }

    fun getCurrency(data: MutableList<String>) {
        val retrofitClient = RetrofitNetwork.getRetrofit("https://cdn.jsdelivr.net/")
        val endpoint = retrofitClient.create(EndPoint::class.java)


        endpoint.getCurrencies().enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {


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
                val adapterSp = ArrayAdapter(baseContext, android.R.layout.simple_list_item_1, data)
                binding.apply {
                    spinnerFrom.adapter = adapterSp
                    spinnerTo.adapter = adapterSp
                    spinnerFrom.setSelection(posBRL)
                    spinnerTo.setSelection(posUSD)
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("falhou")
            }
        })
    }
}