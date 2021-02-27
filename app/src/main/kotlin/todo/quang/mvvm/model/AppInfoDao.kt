package todo.quang.mvvm.model

import androidx.room.Dao
import androidx.room.Insert

@Dao
interface AppInfoDao {
    @Insert
    fun insertAll(vararg users: AppInfo)
}