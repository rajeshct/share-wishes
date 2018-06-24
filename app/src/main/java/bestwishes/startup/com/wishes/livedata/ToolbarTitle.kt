package bestwishes.startup.com.wishes.livedata

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

/**
 * Created by rajesh on 10/12/17.
 */
class ToolbarTitle : ViewModel() {

    // Create a LiveData with a String
    var mCurrentName: MutableLiveData<String>? = null

    fun getCurrentName(): MutableLiveData<String>? {
        if (mCurrentName == null) {
            mCurrentName = MutableLiveData()
        }
        return mCurrentName
    }

}
