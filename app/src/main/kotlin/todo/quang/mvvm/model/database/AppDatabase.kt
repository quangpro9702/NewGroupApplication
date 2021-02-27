package todo.quang.mvvm.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import todo.quang.mvvm.model.AppInfo
import todo.quang.mvvm.model.AppInfoDao

@Database(entities = [AppInfo::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun appInfoDao(): AppInfoDao

    companion object {
        const val DATABASE_NAME = "app_db"
    }
}