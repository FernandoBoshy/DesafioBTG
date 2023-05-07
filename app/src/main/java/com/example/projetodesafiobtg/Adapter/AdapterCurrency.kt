package com.example.projetodesafiobtg.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.projetodesafiobtg.model.Currency
import com.example.projetodesafiobtg.R

class AdapterCurrency(list: MutableList<Currency>): RecyclerView.Adapter<AdapterCurrency.MyViewHolder>() {

    private var listaCurrency: MutableList<Currency> = list

    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val currencyName: TextView = itemView.findViewById(R.id.idCurrencyName)
        val currencySign: TextView = itemView.findViewById(R.id.idCurrencySign)
    }

    fun setFilterList(newList: MutableList<Currency>){
        listaCurrency = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemList: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.adaptercurrencylist, parent,false)
        return MyViewHolder(itemList)
    }


    override fun getItemCount(): Int {
        return listaCurrency.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item: Currency = listaCurrency[position]
        holder.currencyName.setText(item.name)
        holder.currencySign.setText(item.sign)
    }

}