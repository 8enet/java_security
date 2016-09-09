package com.kotlindemo

/**
 * Created by zl on 15/11/7.
 */
open class AdBean public  constructor(val n: String,nid: Int = 10){
    val name:String ?= n
    val id:Int = nid
    var url:String?=null



    init {

    }

    constructor(n:String,url:String) : this(n,100){
        this.url=url
    }

    override fun toString(): String {
        return "name -> ${name},id -> ${id}"
    }

    open fun test(){

    }
}

class Child :AdBean("child"){

    override fun test() {
        super.test()
    }


}

object Resource{
    val name = "Name"
}