package com.demo.kotlindemo

import com.kotlindemo.AdBean

/**
 * Created by zl on 15/11/6.
 */

fun main(args:Array<String>){
    println("hello kotlin")
    aa()
    val n = sum(4, 6)
    println("n is ${n}")
    println("who max ${max(19, 7)}")

    typeCheck("aaa")
    cases("test")
    checkRange()
    nullCase(null)
    dataClass()

    val list =arrayListOf("a","b","c")
    for (v in list) {
        println(" $v")
    }


    val map=hashMapOf(Pair("a",1), Pair("b","cd"))
    for ((k, v) in map) {
        println("$k -> $v")
    }



}



fun aa(): Unit {
    println("aa")
}


fun sum(x:Int,y:Int)=x+y


fun max(x: Int,y: Int)= if (x > y) x else y

fun parseInt(s:String):Int ?{
    return s.toInt()
}

fun typeCheck(any: Any){
    println("typeCheck ${any is String}")
}

fun cases(any: Any){
    when(any){
        1 -> println("is one")
        2 -> println("is two ")
        is Int -> println("is int")
        "test" -> println("is test str")
        is String -> println("is a str")
        else -> println("unkonw")
    }
}

fun checkRange(){
    if (1 in 1..6)
        print("OK")
    else
        print("unkonw")
}

fun String.print(){
    println(toString())
}

fun nullCase(any: Any?){

    any.let {
        println("any is null , -->  ${any}")
    }
    println(any?.hashCode() ?: "is null !!")

}

fun dataClass(): Unit {
    val  b:AdBean = AdBean("kotlin", 100)

    println(b)
}