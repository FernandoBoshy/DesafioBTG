package com.example.projetodesafiobtg.Model

class Currencies{
    var currencyName: String = ""
    var currencySign: String = ""
    var currencyValue: String = ""

    constructor(){

    }
    constructor(currencyName: String, currencySign: String,  currencyValue: String){
        this.currencyName = currencyName
        this.currencySign = currencySign
        this.currencyValue = currencyValue
    }

}