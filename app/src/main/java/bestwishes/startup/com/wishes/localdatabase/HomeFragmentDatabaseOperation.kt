package bestwishes.startup.com.wishes.localdatabase

import bestwishes.startup.com.wishes.interfaces.viewcallbacks.IDatabseSuccessFailureCallback
import bestwishes.startup.com.wishes.interfaces.viewcallbacks.IHomeDatabaseCallback
import bestwishes.startup.com.wishes.model.viewmodel.HomeViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.internal.operators.completable.CompletableFromAction
import io.reactivex.schedulers.Schedulers

/**
 * Created by rajesh on 10/12/17.
 */
class HomeFragmentDatabaseOperation {
    object Data {
        val HOLDER = mutableListOf<HomeViewModel>()
    }

    companion object {
        val data: MutableList<HomeViewModel> by lazy { HomeFragmentDatabaseOperation.Data.HOLDER }
    }

    fun updateFavourite(homeViewModel: HomeViewModel, isSave: Boolean,
                        iDatabseSuccessFailureCallback: IDatabseSuccessFailureCallback) {
        data.add(homeViewModel)
        CompletableFromAction(Action {
            if (isSave) {
                AppDatabase.instance.favouriteDat().insert(homeViewModel)
            } else {
                AppDatabase.instance.favouriteDat().delete(homeViewModel.content)
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ iDatabseSuccessFailureCallback.onSuccess(true) },
                        { iDatabseSuccessFailureCallback.onError("") })
    }

    fun getFavouriteData(iHomeDatabaseCallback: IHomeDatabaseCallback) {
        iHomeDatabaseCallback.showProgress()
        AppDatabase.instance.favouriteDat().getAll(true)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ it ->
                    iHomeDatabaseCallback.hideProgress()
                    iHomeDatabaseCallback.favouriteData(it)
                },
                        { iHomeDatabaseCallback.showMessage("") })
    }
}