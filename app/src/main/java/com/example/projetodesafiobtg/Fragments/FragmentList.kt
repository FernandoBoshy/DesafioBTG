package com.example.projetodesafiobtg.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.projetodesafiobtg.Adapter.AdapterCurrency
import com.example.projetodesafiobtg.model.Currency
import com.example.projetodesafiobtg.network.EndPoint
import com.example.projetodesafiobtg.network.RetrofitNetwork
import com.example.projetodesafiobtg.R
import com.example.projetodesafiobtg.databinding.FragmentListBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

class FragmentList : Fragment() {

    private lateinit var binding: FragmentListBinding
    lateinit var currencies: MutableList<Currency>
    lateinit var adapter: AdapterCurrency

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListBinding.inflate(inflater, container, false)
        binding.button3.setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        getCurrency()

        currencies = ArrayList()
        adapter = AdapterCurrency(currencies)

        binding.recyclerCurrency.apply {
            layoutManager = LinearLayoutManager(this@FragmentList.context)
            setHasFixedSize(true)
            adapter = this@FragmentList.adapter
        }

        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenerSearchUpdateCurrencyList()
    }

    private fun listenerSearchUpdateCurrencyList() {
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
        val tempFilterListVar = currencies
        if (query != null) {
            val filterListVar = ArrayList<Currency>()

            for (item in tempFilterListVar) {
                if (item.sign?.lowercase(Locale.ROOT)?.contains(query.lowercase())!!) {
                    filterListVar.add(item)
                }
                if (item.name?.lowercase(Locale.ROOT)?.contains(query.lowercase())!!) {
                    filterListVar.add(item)
                }
            }
            adapter.setFilterList(filterListVar.distinct().toMutableList())
        }
    }

    private fun getCurrency() {

        val retrofitClient = RetrofitNetwork.getRetrofit("https://cdn.jsdelivr.net/")
        val endpoint = retrofitClient.create(EndPoint::class.java)

        endpoint.getCurrencies().enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {

                val currenciesList = response.body()?.entrySet()?.iterator()?.asSequence()?.toList()?.map {
                    Currency(name = it.key.toString(), sign = it.value.toString())
                } ?: arrayListOf()

                currencies.addAll(currenciesList.toMutableList())
                adapter.setFilterList(currenciesList.toMutableList())
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("getCurrency falhou")
            }
        })
    }
}