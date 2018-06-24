package bestwishes.startup.com.wishes.interfaces.viewcallbacks

import bestwishes.startup.com.wishes.model.viewmodel.HomeViewModel

/**
 * Created by rajesh on 9/12/17.
 */
interface IHomeDataCallback : IBaseView {
    fun homeData(data: MutableList<HomeViewModel>)
    fun noData()
}