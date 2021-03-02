package todo.quang.mvvm.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AppInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg apps: AppInfoEntity)

    @Query("SELECT * FROM AppInfoEntity  where packageName=:packageName")
    fun findAppByPackageNameData(packageName: String): AppInfoEntity?
}