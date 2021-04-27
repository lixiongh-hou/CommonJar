package com.common.jar

/**
 * @author 李雄厚
 *
 * @features ***
 */
fun main() {
    val list = listOf("apple", "zoo", "pear", "banana", "watermelon")
    val result = list.findMax { it.length }
    println(result)

    //如果我们已经有一个数组并希望将其内容传给该函数，我们使用伸展（spread）操作符（在数组前面加 *）：
    val a = arrayOf("2", "3", "4")
    val list1 = asList("1", *a, "5", "6")
    println(list1)

    TestActivity.build()

    println("infix拓展函数${1 add 1}")


    val test: (Int, Int) -> String = { index, index1 ->
        (index + index1).toString()
    }

    println(test(1, 2))

    val repeatFun: String.(Int) -> String = {
        this.repeat(it)
    }
    val twoParameters: (String, Int) -> String = repeatFun
    println("result = ${twoParameters("hello", 3)}")

    fun runTransformation(f: (String, Int) -> String): String{
        return f("hello", 3)
    }
    val result1 = runTransformation(repeatFun)
    println("result = $result1")

    /*
    延迟属性 Lazy
    lazy() 是接受一个 lambda 并返回一个 Lazy <T> 实例的函数，返回的实例可以作为实现延迟属性的委托： 第一次调用 get()
    会执行已传递给 lazy() 的 lambda 表达式并记录结果， 后续调用 get() 只是返回记录的结果。
     */
    val lazyValue: String by lazy {
        println("computed!")
        "Hello"
    }
    println(lazyValue)
    println(lazyValue)

}



/**
 * inline 内联函数
 */
inline fun inlined(block: () -> Unit) {
    println("hi!")
}

/**
 * 具体化的类型参数
 * 有时候我们需要访问一个作为参数传给我们的一个类型：
 */
inline fun <reified T> findParentOfType(): T {
    T::class.java.name
    return "s" as T
}

fun <T, R : Comparable<R>> List<T>.findMax(block: (T) -> R): T? {
    if (isEmpty()) return null
    var maxElement = get(0)
    var maxValue = block(maxElement)
    for (element in this) {
        val value = block(element)
        if (value > maxValue) {
            maxElement = element
            maxValue = value
        }
    }
    return maxElement

}

/**
 * 关键字 Varargs
 * 可变数量的参数（Varargs）
 * 函数的参数（通常是最后一个）可以用 vararg 修饰符标记：
 */
fun <T> asList(vararg ts: T): List<T> {
    val result = ArrayList<T>()
    for (t in ts) {
        result.add(t)
    }
    return result
}

/**
 * #拓展函数
 */
infix fun Int.add(s: Int): Int {
    return this + s
}

object TestActivity {
    /**
     * 关键字 infix
     * 中缀表示法
     * 标有 infix 关键字的函数也可以使用中缀表示法（忽略该调用的点与圆括号）调用。中缀函数必须满足以下要求：
     * 1，它们必须是成员函数或扩展函数；
     * 2，它们必须只有一个参数；
     * 3，其参数不得接受可变数量的参数且不能有默认值。
     *
     * #成员函数
     */
    private infix fun add(s: String) {
        println(s)
    }

    /**
     * 请注意，中缀函数总是要求指定接收者与参数。当使用中缀表示法在当前接收者上调用方法时，需要显式使用 this；不能像常规方法调用那样省略。
     * 这是确保非模糊解析所必需的。
     */
    fun build() {
        this add "infix成员函数" // 正确
//        add("成员函数")     // 正确
//        add "成员函数"      // 错误：必须指定接收者

    }


}









