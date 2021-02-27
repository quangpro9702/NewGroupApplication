package todo.quang.mvvm.model.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import todo.quang.mvvm.model.Post
import todo.quang.mvvm.model.PostDao

@Database(entities = [Post::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDao(): PostDao
}