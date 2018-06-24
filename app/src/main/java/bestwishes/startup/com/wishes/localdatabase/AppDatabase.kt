package bestwishes.startup.com.wishes.localdatabase

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import bestwishes.startup.com.wishes.ApplicationInitilizer
import bestwishes.startup.com.wishes.interfaces.dao.IHomeDao
import bestwishes.startup.com.wishes.model.viewmodel.HomeViewModel


/**
 * Created by rajesh on 10/12/17.
 */
@Database(entities = [(HomeViewModel::class)], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun favouriteDat(): IHomeDao

    private object Holder {
        val INSTANCE = synchronized(AppDatabase::class.java) {
            Room.databaseBuilder(ApplicationInitilizer.instance.applicationContext,
                    AppDatabase::class.java, "wishes")
                    .fallbackToDestructiveMigration()
                    .build()
        }
    }

    companion object {
        val instance: AppDatabase by lazy { Holder.INSTANCE }
    }

}