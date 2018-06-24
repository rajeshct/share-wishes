package bestwishes.startup.com.wishes.interfaces.viewcallbacks

import android.graphics.Bitmap
import bestwishes.startup.com.wishes.model.viewmodel.HomeViewModel

/**
 * Created by rajesh on 10/12/17.
 */
interface ActivityOpeartion {
    fun showInterstitialAd()
    fun imageShare(tag: String, image: Bitmap, shareType: Int)
    fun requestPermissionForApp()
    fun updateFavourite(homeViewModel: HomeViewModel, save: Boolean)
    fun updateHome(homeViewModel: HomeViewModel)
}