package bestwishes.startup.com.wishes

import android.app.Application
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

/**
 * Created by rajesh on 10/12/17.
 */

class ApplicationInitilizer : Application() {
    companion object {
        @get:Synchronized
        lateinit var instance: ApplicationInitilizer
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Fabric.with(this, Crashlytics())
    }


}
