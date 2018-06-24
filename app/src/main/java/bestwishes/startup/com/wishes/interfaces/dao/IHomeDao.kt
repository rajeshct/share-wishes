package bestwishes.startup.com.wishes.interfaces.dao

import android.arch.persistence.room.*
import bestwishes.startup.com.wishes.model.viewmodel.HomeViewModel
import io.reactivex.Single


/**
 * Created by rajesh on 10/12/17.
 */
@Dao
interface IHomeDao {

    @Query("SELECT * FROM homeviewmodel where isFavourite LIKE :favourite")
    fun getAll(favourite: Boolean): Single<List<HomeViewModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg users: HomeViewModel)

    @Query("delete from homeviewmodel where content LIKE :content")
    fun delete(content: String)

    @Query("SELECT content FROM homeviewmodel WHERE content LIKE :content LIMIT 1")
    fun fetchOldContent(vararg content: String): String
}