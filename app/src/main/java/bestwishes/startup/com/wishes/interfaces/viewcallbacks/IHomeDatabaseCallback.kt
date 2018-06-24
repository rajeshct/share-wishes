package bestwishes.startup.com.wishes.interfaces.viewcallbacks

import bestwishes.startup.com.wishes.model.viewmodel.HomeViewModel

/**
 * Created by rajesh on 10/12/17.
 */
interface IHomeDatabaseCallback:IBaseView {
    fun favouriteData(data: List<HomeViewModel>)
}