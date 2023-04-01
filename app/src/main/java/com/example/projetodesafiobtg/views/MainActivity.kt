package com.example.projetodesafiobtg.views

//import android.widget.SearchView
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
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
    lateinit var adapter: AdapterCurrency

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val data2 = mutableListOf<String>()
        getCurrency(data2)
        currenciesList = ArrayList()
        adapter = AdapterCurrency(currenciesList)

        val adapterSV: ArrayAdapter<String> = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            data2
        )

        binding.listViewMid.adapter = adapterSV

        //  Listener do editValue para alteração de valor do TextView
        binding.editValue.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                println("before change")
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                println("on change")
            }

            override fun afterTextChanged(p0: Editable?) {
                val editTextValue = p0.toString()
                if (editTextValue == ".") {
                    binding.editValue.setText("0.")
                    binding.editValue.setSelection(2)
                } else {
                    if(!editTextValue.isEmpty()){
                        convertMoney()
                        println("")
                    }
                }
                println("after change")
            }
        })

//      Adapter recyclerview da lista de moedas
        binding.recyclerCurrency.apply {
            layoutManager = LinearLayoutManager(applicationContext)
            setHasFixedSize(true)
            adapter = this@MainActivity.adapter
        }

//      Listener do searchView que fica junto da lista de moedas
        binding.searchinside.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                filterList(newText)
                return true
            }

        })

//      Listener do searchView que procura as moedas que vão entrar nos spinners
        binding.searchFrom.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(newText: String?): Boolean {
                binding.searchFrom.clearFocus()
                if(data2.contains(newText)){
                    adapterSV.filter.filter(newText)

                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if(newText.isNullOrEmpty() || !binding.searchFrom.hasFocus()){
                    binding.frameLayoutMid.visibility = View.GONE
                } else {
                    binding.frameLayoutMid.visibility = View.VISIBLE
                    adapterSV.filter.filter(newText)

                }
                return false
            }

        })

//      Listener para alterar a lista de moedas da caixa-lista de moedas dos spinners (clique CURTO)
        binding.listViewMid.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = binding.listViewMid.adapter.getItem(position).toString()
            val spinnerAdapter = binding.spinnerFrom.adapter
            for (x in 0 until spinnerAdapter.count) {
                if (spinnerAdapter.getItem(x) == selectedItem) {
                    binding.spinnerFrom.setSelection(x)
                    break
                }
            }
        }

//      Listener para alterar a lista de moedas da caixa-lista de moedas dos spinners (clique LONGO)

        binding.listViewMid.setOnItemLongClickListener { parent, view, position, id ->
            val selectedItem = binding.listViewMid.adapter.getItem(position).toString()
            val spinnerAdapter = binding.spinnerTo.adapter
            for (x in 0 until spinnerAdapter.count) {
                if (spinnerAdapter.getItem(x) == selectedItem) {
                    binding.spinnerTo.setSelection(x)
                    break
                }
            }
            true
        }
    }

//  funções //

    fun filterList(query: String?) {
        var tempFilterListVar = currenciesList
        if (query != null) {
            var filterListVar = ArrayList<Currencies>()

            for (item in tempFilterListVar) {
                if (item.currencySign?.lowercase(Locale.ROOT)?.contains(query.lowercase())!!) {
                    filterListVar.add(item)
                }
                if (item.currencyName?.lowercase(Locale.ROOT)?.contains(query.lowercase())!!) {
                    filterListVar.add(item)
                }
            }
            adapter.setFilterList(filterListVar.distinct().toMutableList())
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

                val data = response.body()?.entrySet()?.find { it.key == binding.spinnerTo.selectedItem.toString() }
                val rate: Double = data?.value.toString().toDouble()

//                val conversion = binding.editValue.text.toString().toDouble()  * rate

//                val conversion = (binding.editValue.text?.toString()?: 0.0.toDouble()?: 0.0)  * rate
                
                val conversion = (binding.editValue.text?.toString() ?: "0.0")
                val conversion2 = if(conversion.isEmpty()) 0.0 * rate else conversion.toDouble() * rate

//                val conversion = if (!binding.editValue.text.isEmpty()) binding.editValue.text.toString().toDouble() * rate else 0.0

                binding.textResult.text = String.format("%.2f", conversion2)

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("convertMoney falhou")
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
                println("getCurrency falhou")
            }
        })
    }
}