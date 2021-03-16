package todo.quang.mvvm.model

import androidx.annotation.Keep
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
@Keep
data class AppInfoEntity(
        @PrimaryKey
        var id: String,
        var name: String = "",
        var packageName: String,
        var genreName: String = "",
        var genreType: String = "app",
        var timeRecent: Long = 0,
        var sumClick: Long = 0
)