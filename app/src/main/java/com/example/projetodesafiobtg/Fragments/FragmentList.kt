package com.example.projetodesafiobtg.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import androidx.appcompat.widget.SearchView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.projetodesafiobtg.Adapter.AdapterCurrency
import com.example.projetodesafiobtg.Model.Currencies
import com.example.projetodesafiobtg.Network.EndPoint
import com.example.projetodesafiobtg.Network.RetrofitNetwork
import com.example.projetodesafiobtg.R
import com.example.projetodesafiobtg.databinding.FragmentListBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class FragmentList : Fragment() {
    private lateinit var binding: FragmentListBinding
    lateinit var currenciesList: MutableList<Currencies>
    lateinit var adapter: AdapterCurrency

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentListBinding.inflate(inflater, container, false)
        binding.button3.setOnClickListener {
            fragmentManager?.beginTransaction()?.replace(R.id.navManagerFragment, FragmentMain())?.commit()
        }

        val data2 = mutableListOf<String>()
        getCurrency(data2)

        currenciesList = ArrayList()
        println("primeiro" + data2)
        adapter = AdapterCurrency(currenciesList)

        binding.recyclerCurrency.apply {
            layoutManager = LinearLayoutManager(this@FragmentList.context)
            setHasFixedSize(true)
            adapter = this@FragmentList.adapter
        }

        val adapterSV: ArrayAdapter<String> = ArrayAdapter(
            requireContext().applicationContext.getApplicationContext(), // context?.applicationContext!!
            android.R.layout.simple_list_item_1,
            data2
        )

        return binding.root
    }



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenerSearchUpdateCurrencyList()
    }

    fun listenerSearchUpdateCurrencyList(){
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
    }

    fun filterList(query: String?) {
        val tempFilterListVar = currenciesList
        if (query != null) {
            val filterListVar = ArrayList<Currencies>()

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

    fun getCurrency(data: MutableList<String>) {

        val retrofitClient = RetrofitNetwork.getRetrofit("https://cdn.jsdelivr.net/")
        val endpoint = retrofitClient.create(EndPoint::class.java)

        endpoint.getCurrencies().enqueue(object : retrofit2.Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {


                response.body()?.keySet()?.iterator()?.forEach {
                    data.add(it)
                }

                response.body()?.entrySet()?.iterator()?.forEach {
                    val key = it.key
                    val value = it.value
                    currenciesList.add(Currencies(value.toString(), key.toString()))
//                    println(currenciesList.toString())
                }

                //  adapter do Spinner
//                val posBRL = data.indexOf("brl")
//                val posUSD = data.indexOf("usd")
//                val adapterSp = ArrayAdapter(context?.applicationContext!!, android.R.layout.simple_list_item_1, data)
//
//                binding.apply {
//                    spinnerFrom.adapter = adapterSp
//                    spinnerTo.adapter = adapterSp
//                    spinnerFrom.setSelection(posBRL)
//                    spinnerTo.setSelection(posUSD)
//                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("getCurrency falhou")
            }
        })
    }
}