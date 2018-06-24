package bestwishes.startup.com.wishes.firebase

import bestwishes.startup.com.wishes.adapter.HomeAdapter
import bestwishes.startup.com.wishes.constants.AppConstants
import bestwishes.startup.com.wishes.interfaces.viewcallbacks.IHomeDataCallback
import bestwishes.startup.com.wishes.livedata.ToolbarTitle
import bestwishes.startup.com.wishes.localdatabase.AppDatabase
import bestwishes.startup.com.wishes.model.viewmodel.HomeViewModel
import com.google.firebase.firestore.FirebaseFirestore
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


/**
 * Created by rajesh on 9/12/17.
 */
class FireBaseGetData private constructor() {
    private val db = FirebaseFirestore.getInstance()

    private object Holder {
        val INSTANCE = synchronized(FireBaseGetData::class.java) {
            FireBaseGetData()
        }
    }

    companion object {
        val instance: FireBaseGetData by lazy { Holder.INSTANCE }
    }

    @Suppress("UNCHECKED_CAST")
    fun getDataFromFireBase(homeDataCallback: IHomeDataCallback?) {
        homeDataCallback?.showProgress()
        db.collection(FirebaseCollection.QUOTES)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            if (document.id == FireBaseDocumentId.CONTENTS) {
                                val dataTemp: MutableList<HashMap<String, Any>> = document.data[FireBaseFieldId.DATA]
                                        as MutableList<HashMap<String, Any>>
                                parseDataAsync(dataTemp, homeDataCallback)
                                break
                            }
                        }
                    } else {
                        homeDataCallback?.noData()
                    }
                }
    }

    fun getTitleFromFireBase(toolbarTitle: ToolbarTitle) {
        db.collection(FirebaseCollection.TITLE)
                .get()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        for (document in task.result) {
                            if (document.id == FireBaseDocumentId.APP_TITLE) {
                                AppConstants.APP_TITLE = document.data[FireBaseFieldId.TITLE] as String
                                try {
                                    AppConstants.ADV_POSITION = Integer.parseInt(document.data[FireBaseFieldId.ADV_POSITION_KEY] as String)
                                } catch (exp: Exception) {

                                }
                                toolbarTitle.getCurrentName()?.value = AppConstants.APP_TITLE
                                break
                            }
                        }
                    }
                }
    }

    private fun parseDataAsync(dataTemp: MutableList<HashMap<String, Any>>, homeDataCallback: IHomeDataCallback?) {

        Single.create<MutableList<HomeViewModel>> {
            val data = mutableListOf<HomeViewModel>()
            val iHomeDao = AppDatabase.instance.favouriteDat()
            dataTemp.reverse()
            dataTemp.mapTo(data) {
                val contentType = (it[FireBaseFieldId.CONTENT_TYPE] as String).toInt()
                var contentBackground = ""
                var contentColor = ""
                if (contentType == HomeAdapter.ViewTypes.TEXT_VIEW_TYPE) {
                    contentBackground = it[FireBaseFieldId.CONTENT_BACKGROUND] as String
                    contentColor = it[FireBaseFieldId.CONTENT_COLOR] as String
                }
                val content = it[FireBaseFieldId.CONTENT] as String
                HomeViewModel(contentType = contentType, content = content,
                        contentSource = it[FireBaseFieldId.CONTENT_SOURCE] as String,
                        contentBackground = contentBackground,
                        isFavourite = (iHomeDao.fetchOldContent(content) == content),
                        contentColor = contentColor)
            }
            it.onSuccess(data)
        }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    homeDataCallback?.hideProgress()
                    homeDataCallback?.homeData(it)
                }) {
                    homeDataCallback?.hideProgress()
                    homeDataCallback?.showMessage(AppConstants.ERROR)
                }

    }

}