package todo.quang.mvvm.model.database

import android.content.pm.PackageInfo
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.Serializable
import java.lang.reflect.Type

class DataConverter : Serializable {
    @TypeConverter
    fun fromPackageInfo(contents: PackageInfo): String {
        val gson = Gson()
        val type: Type = object : TypeToken<PackageInfo>() {}.type
        return gson.toJson(contents, type)
    }
}