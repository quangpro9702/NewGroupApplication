package todo.quang.mvvm.model

import androidx.annotation.Keep
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Keep
@Dao
interface AppInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg apps: AppInfoEntity)

    @Query("SELECT * FROM AppInfoEntity  where packageName=:packageName")
    fun findAppByPackageNameData(packageName: String): AppInfoEntity?

    @Query("SELECT * FROM AppInfoEntity  where packageName=:packageName")
    fun findAppByPackageNameDataa(packageName: String): Flow<AppInfoEntity?>

    @Query("SELECT * FROM AppInfoEntity where genreName=:groupName")
    fun findListAppByGroupName(groupName: String): List<AppInfoEntity>
}