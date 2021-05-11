package com.common.jar

import android.os.Build
import android.os.Bundle
import android.util.Log
import com.google.gson.Gson
import java.io.File

/**
 * @author 李雄厚
 *
 * @features ***
 */
fun main() {
//    computeRunTime {
//        (0..10000000)
//            .asSequence()
//            .map { it + 1 }
//            .filter { it % 2 == 0 }
//            .count { it < 20 }
//            .run {
//                println("by using list way, result is : $this")
//            }
//    }
//    (0..6)
//        .asSequence()
//        //map返回是Sequence<T>，故它属于中间操作
//        .map {
//            println("map: $it")
//            return@map it + 1
//        }
//        //filter返回是Sequence<T>，故它属于中间操作
//        .filter {
//            println("filter: $it")
//            return@filter it % 2 == 0
//        }
//        .count {
//            it < 6
//        }
//        .run {
//            println("result is $this");
//        }

    val list = listOf(0, 1, 2, 3, 4, 5, 6).map {
        if (it == 4) {
            10
        } else {
            it
        }
    }
    list.forEach(::println)

    val index = 19

    val blessingFunction: (Int) -> String = {
        "1"
    }
    println(blessingFunction.invoke(1))

    val info = { name: String, sex: String ->
        "我的个人信息是,姓名:$name,年龄:$sex"
    }
    userInfo("李雄厚", ::info)

    //对象的解构语法
    val (name1, sex1) = User("李雄厚", "5")
    println("$name1$sex1")

    //集合的解构语法
    val data = mutableListOf(User("李雄厚", "5"), User("马国瑞", "6"))
    val (_, data2) = data
    val (str1, str2) = data2
//    println("${data1.name}${data1.sex}")
    println("${data2.name}${data2.sex}")


    val str3 = "The people's Republic of China."
    val str4 = str3.replace(Regex("[aeiou]")) {
        when (it.value) {
            "a" -> "3"
            "e" -> "5"
            "i" -> "0"
            "o" -> "9"
            "u" -> "2"
            else -> it.value
        }
    }
    println(str3)
    println(str4)

    val number = "%.2f".format(1.225)
    println(number)

    val file = File("E://test.txt").takeIf {
        it.exists() && it.canRead()
    }?.readText()
    println(file)

    var arr = intArrayOf(1,2,3,4,5,6,7)
    arr += 9
    arr.forEach {
        println(it)
    }

    val bundle = arrayListOf<String>().apply {
        add("李奕迅")
    }
    println(bundle)
}

fun info(name: String, sex: String): String {
    return "我的个人信息是,姓名:$name,年龄:$sex"
}


fun computeRunTime(action: (() -> Unit)?) {
    val startTime = System.currentTimeMillis()
    action?.invoke()
    println("the code run time is ${System.currentTimeMillis() - startTime}")
}

fun userInfo(name: String, info: (String, String) -> String) {
    val sex = (1..24).shuffled().last()
    println(info(name, sex.toString()))
}

data class User(val name: String, val sex: String)