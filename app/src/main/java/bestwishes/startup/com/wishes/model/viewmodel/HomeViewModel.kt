package bestwishes.startup.com.wishes.model.viewmodel

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * Created by rajesh on 9/12/17.
 */
@Entity
data class HomeViewModel(
        @ColumnInfo(name = "contentType")
        var contentType: Int,
        @ColumnInfo(name = "content")
        var content: String,
        @ColumnInfo(name = "contentSource")
        var contentSource: String,
        @ColumnInfo(name = "contentBackground")
        var contentBackground: String,
        @ColumnInfo(name = "isFavourite")
        var isFavourite: Boolean,
        @ColumnInfo(name = "contentColor")
        var contentColor: String) {

    @ColumnInfo(name = "id")
    @PrimaryKey(autoGenerate = true)
    var tid: Int = 0

    override fun toString(): String {
        return ""+tid
    }


}
