package bestwishes.startup.com.wishes.fragments

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import bestwishes.startup.com.wishes.BuildConfig
import bestwishes.startup.com.wishes.R
import bestwishes.startup.com.wishes.constants.AppConstants
import bestwishes.startup.com.wishes.dialog.FullscreenImageDialog
import bestwishes.startup.com.wishes.interfaces.viewcallbacks.ActivityOpeartion
import bestwishes.startup.com.wishes.interfaces.viewcallbacks.IDatabseSuccessFailureCallback
import bestwishes.startup.com.wishes.localdatabase.HomeFragmentDatabaseOperation
import bestwishes.startup.com.wishes.model.viewmodel.HomeViewModel
import bestwishes.startup.com.wishes.sharecontents.ShareContents
import com.google.android.youtube.player.YouTubeApiServiceUtil
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubeStandalonePlayer

/**
 * Created by rajesh on 10/12/17.
 */
open class BaseFragment : Fragment() {
    private var activityOpeartion: ActivityOpeartion? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        try {
            activityOpeartion = context as ActivityOpeartion
        } catch (exp: Exception) {

        }
    }

    override fun onDetach() {
        super.onDetach()
        activityOpeartion = null
    }


    fun showInterstitialAD() {
        activityOpeartion?.showInterstitialAd()
    }

    fun imageShare(image: Bitmap, shareType: Int, tag: String) {
        activityOpeartion?.imageShare(tag = tag, image = image, shareType = shareType)
    }


    fun textShare(content: String, shareType: Int, tag: String) {
        if (context != null) {
            ShareContents(context = (context as AppCompatActivity), content = content, shareContents = shareType
                    , tag = tag)
        }
    }

    fun saveToLocalDatabase(homeViewModel: HomeViewModel, isSave: Boolean, position: Int, isFavourite: Boolean) {
        if (!isFavourite) activityOpeartion?.updateFavourite(homeViewModel, isSave)
        else activityOpeartion?.updateHome(homeViewModel)
        HomeFragmentDatabaseOperation().updateFavourite(homeViewModel = homeViewModel, isSave = isSave,
                iDatabseSuccessFailureCallback = object : IDatabseSuccessFailureCallback {
                    override fun onSuccess(data: Any) {
                        if (!isSave) {
                            updateList(position)
                        }
                    }

                    override fun onError(message: String) {
                    }

                })

    }

    open fun updateList(position: Int) {

    }

    fun openFullImage(content: String) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        val ft = fragmentManager!!.beginTransaction()
        val prev = fragmentManager!!.findFragmentByTag(AppConstants.DIALOG_TAG)
        if (prev != null) {
            ft.remove(prev)
        }
        ft.addToBackStack(null)

        // Create and show the dialog.
        val newFragment = FullscreenImageDialog()
        val bundle = Bundle()
        bundle.putString(FullscreenImageDialog.ImageUrl.IMAGE_URL, content)
        newFragment.arguments = bundle
        newFragment.show(ft, AppConstants.DIALOG_TAG)
    }

    fun playVideo(content: String) {
        //Check for any issues
        val result = YouTubeApiServiceUtil.isYouTubeApiServiceAvailable(context)
        if (result != YouTubeInitializationResult.SUCCESS) {
            //If there are any issues we can show an error dialog.
            Toast.makeText(context, getString(R.string.error_youtube), Toast.LENGTH_SHORT).show()
        } else {
            if (activity != null) {
                startActivity(YouTubeStandalonePlayer.createVideoIntent(activity,
                        BuildConfig.DEVELOPER_KEY, content, 0, true, true))
            }
        }
    }
}