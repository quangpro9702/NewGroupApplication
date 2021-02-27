package todo.quang.mvvm.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Class which provides a model for post
 * @constructor Sets all properties of the post
 * @property userId the unique identifier of the author of the post
 * @property id the unique identifier of the post
 * @property title the title of the post
 * @property body the content of the post
 */
@Entity
data class AppInfo(
        @field:PrimaryKey
        val id: Int,
        val name: String,
        val genre : String,
        val type : Int
)