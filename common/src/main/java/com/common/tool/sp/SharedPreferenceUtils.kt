package com.common.tool.sp

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import com.common.tool.base.BaseApp
import com.google.gson.*
import java.lang.reflect.Type
import java.util.*

/**
 * @author 李雄厚
 *
 * @features 公共SP文件存储，配合Gson存储集合
 */
object SharedPreferenceUtils {

    private val prefs: SharedPreferences by lazy {
        BaseApp.instance.applicationContext.getSharedPreferences("Common", Context.MODE_PRIVATE)
    }

    fun <T> putListData(key: String, list: List<T>): Boolean {
        var result: Boolean
        val array = JsonArray()
        with(prefs.edit()) {
            if (list.isEmpty()) {
                putString(key, array.toString())
                apply()
                return false
            }
            val type = list::class.java.simpleName
            try {
                when (type) {
                    "Boolean" -> {
                        for (i in list) {
                            array.add(i as Boolean)
                        }
                    }
                    "Long" -> {
                        for (i in list) {
                            array.add(i as Long)
                        }
                    }
                    "Float" -> {
                        for (i in list) {
                            array.add(i as Float)
                        }
                    }
                    "String" -> {
                        for (i in list) {
                            array.add(i as String)
                        }
                    }
                    "Int" -> {
                        for (i in list) {
                            array.add(i as Int)
                        }
                    }
                    else -> {
                        val gson = com.google.gson.GsonBuilder()
                            .registerTypeAdapter(android.net.Uri::class.java, UriSerializer())
                            .create()
                        for (i in list) {
                            val obj = gson.toJsonTree(i)
                            array.add(obj)
                        }
                    }
                }
                putString(key, array.toString())
                result = true
            } catch (e: Exception) {
                result = false
                e.printStackTrace()
            }
            apply()
        }
        return result
    }

    fun <T> getListData(key: String, cls: Class<T>): MutableList<T> {
        val list = mutableListOf<T>()
        val json = prefs.getString(key, "")
        if (!json.isNullOrEmpty()) {
            val gson = GsonBuilder()
                .registerTypeAdapter(Uri::class.java, UriSerializer())
                .create()
            val array = JsonParser.parseString(json).asJsonArray
            for (elem in array) {
                list.add(gson.fromJson(elem, cls))
            }
        }
        return list
    }

    /**
     * 根据key删除存储数据
     */
    fun remove(key: String) {
        prefs.edit().remove(key).apply()
    }
}


internal class UriSerializer : JsonSerializer<Uri>,
    JsonDeserializer<Uri> {
    override fun serialize(
        src: Uri,
        typeOfSrc: Type,
        context: JsonSerializationContext
    ): JsonElement {
        return JsonPrimitive(src.toString())
    }

    @Throws(JsonParseException::class)
    override fun deserialize(
        src: JsonElement, srcType: Type,
        context: JsonDeserializationContext
    ): Uri {
        return Uri.parse(src.asString)
    }
}