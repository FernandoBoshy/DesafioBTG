package com.example.projetodesafiobtg.fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.projetodesafiobtg.Adapter.AdapterCurrency
import com.example.projetodesafiobtg.model.Currency
import com.example.projetodesafiobtg.network.EndPoint
import com.example.projetodesafiobtg.network.RetrofitNetwork
import com.example.projetodesafiobtg.R
import com.example.projetodesafiobtg.databinding.FragmentPrincipalBinding
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*


class FragmentMain : Fragment() {

    private lateinit var binding: FragmentPrincipalBinding
    lateinit var currenciesList: MutableList<Currency>
    lateinit var adapter: AdapterCurrency
    lateinit var data2: MutableList<String>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPrincipalBinding.inflate(inflater, container, false)

        binding.buttonToList.setOnClickListener {
           findNavController().navigate(R.id.fragment_main_to_list)
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        data2 = mutableListOf()
        binding.editValue.setText("")

        getCurrency(data2)
        currenciesList = ArrayList()
        adapter = AdapterCurrency(currenciesList)

        val adapterSV: ArrayAdapter<String> = ArrayAdapter(
            requireContext().applicationContext.getApplicationContext(), // context?.applicationContext!!
            android.R.layout.simple_list_item_1,
            data2
        )
        binding.listViewMid.adapter = adapterSV

        listenerSearchUpdateSpinner()

        listenerCurrencyFrame(data2, adapterSV)

        listenerUpdateEditValue()

    }

    fun listenerUpdateEditValue(){
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
                        if(binding.spinnerFrom.selectedItem != null || binding.spinnerTo.selectedItem != null) {
                            convertMoney()
                        }
                    }
                }
                println("after change")
            }
        })
    }

    fun listenerCurrencyFrame(data2: MutableList<String>,adapterSV: ArrayAdapter<String>){
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
                    binding.textViewTo.visibility = View.GONE
                    binding.textViewFrom.visibility = View.GONE
                } else {
                    binding.frameLayoutMid.visibility = View.VISIBLE
                    binding.textViewTo.visibility = View.VISIBLE
                    binding.textViewFrom.visibility = View.VISIBLE
                    binding.frameLayoutMid.bringToFront()
                    adapterSV.filter.filter(newText)
                }
                return false
            }

        })
    }

    fun listenerSearchUpdateSpinner(){
        //  Listener para alterar a lista de moedas da caixa-lista de moedas dos spinners (clique CURTO)
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
        //  Listener para alterar a lista de moedas da caixa-lista de moedas dos spinners (clique LONGO)
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


    fun filterList(query: String?) {
        val tempFilterListVar = currenciesList
        if (query != null) {
            val filterListVar = ArrayList<Currency>()

            for (item in tempFilterListVar) {
                if (item.sign?.lowercase(Locale.ROOT)?.contains(query.lowercase()) == true) {
                    filterListVar.add(item)
                }
                if (item.name?.lowercase(Locale.ROOT)?.contains(query.lowercase()) == true) {
                    filterListVar.add(item)
                }
            }
            adapter.setFilterList(filterListVar.distinct().toMutableList())
        }
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
                val conversion = if (!binding.editValue.text.isEmpty()) binding.editValue.text.toString().toDouble() * rate else 0.0

                binding.textResult.text = String.format("%.2f", conversion)

            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                println("convert Money falhou")
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
                    val key = it.key
                    val value = it.value
                    currenciesList.add(Currency(value.toString(), key.toString()))
                }

                //  adapter do Spinner
                val posBRL = data.indexOf("brl")
                val posUSD = data.indexOf("usd")
                val adapterSp = ArrayAdapter(context?.applicationContext!!, android.R.layout.simple_list_item_1, data)

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