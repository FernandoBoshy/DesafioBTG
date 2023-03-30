package com.example.projetodesafiobtg.Model

class Currencies{
    var currencyName: String? = ""
    var currencySign: String? = ""


    constructor(){}
    constructor(currencyName: String?, currencySign: String?){
        this.currencyName = currencyName
        this.currencySign = currencySign

    }

}