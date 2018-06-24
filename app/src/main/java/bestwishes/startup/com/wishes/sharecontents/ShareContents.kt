package bestwishes.startup.com.wishes.sharecontents

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.v4.app.ShareCompat
import android.widget.Toast
import bestwishes.startup.com.wishes.R
import bestwishes.startup.com.wishes.constants.AppConstants
import bestwishes.startup.com.wishes.utils.CacheStore
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.share.Sharer
import com.facebook.share.model.ShareLinkContent
import java.io.FileNotFoundException


/**
 * Created by rajesh on 9/12/17.
 */
class ShareContents(tag: String, val context: Activity, shareContents: Int,
                    bitmapImageForShare: Bitmap? = null, val content: String = "") {

    init {
        var tempPath: String?
        tempPath = CacheStore.cachedImages[tag]
        if (tempPath == null && bitmapImageForShare != null) {
            tempPath = CacheStore.instance.saveCacheFile(bitmapImageForShare)
        }
        when (shareContents) {
            AppConstants.OTHER_SHARE -> {
                otherShare()
            }
            AppConstants.WHATSAPP_SHARE
            -> {
                if (tempPath != null) {
                    whatsappShare(tempPath)
                } else {
                    whatsappShare("")
                }
            }
            AppConstants.FACEBOOK_SHARE
            -> facebookShare()

            AppConstants.INSTAGRAM_SHARE
            -> {
                if (tempPath != null) {
                    instagramShare(tempPath)
                }
            }
        }
    }

    private fun facebookShare() {

        val callbackManager = CallbackManager.Factory.create()
        val shareDialog = com.facebook.share.widget.ShareDialog(context)

        // this part is optional
        shareDialog.registerCallback(callbackManager, object : FacebookCallback<Sharer.Result> {
            override fun onSuccess(result: Sharer.Result) {

            }

            override fun onCancel() {}

            override fun onError(error: FacebookException) {}
        })
        if (com.facebook.share.widget.ShareDialog.canShow(ShareLinkContent::class.java)) {
            val linkContent: ShareLinkContent = ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse(AppConstants.APP_URL))
                    .setQuote(if (content.isBlank()) AppConstants.SHARE_MESSAGE else content)
                    .build()
            shareDialog.show(linkContent)
        }
    }
/*    private fun facebookShare(image: Bitmap) {
        if (isFacebookInstalled(context)) {
            val shareDialog = ShareDialog(context)
            val callbackManager = CallbackManager.Factory.create()
            shareDialog.registerCallback(callbackManager, object : FacebookCallback<Sharer.Result> {
                override fun onSuccess(result: Sharer.Result) {

                }

                override fun onCancel() {}

                override fun onError(error: FacebookException) {}
            })
            if (com.facebook.share.widget.ShareDialog.canShow(ShareLinkContent::class.java)) {
                val photo = SharePhoto.Builder()
                        .setBitmap(image)
                        .build()
                val content = SharePhotoContent.Builder()
                        .addPhoto(photo)
                        .setShareHashtag(ShareHashtag.Builder().setHashtag(AppConstants.HASH_TAG).build())
                        .build()
                shareDialog.show(content, ShareDialog.Mode.AUTOMATIC)
            }
        } else {
            val callbackManager = CallbackManager.Factory.create()
            val permissionNeeds = listOf("publish_actions")
            //this loginManager helps you eliminate adding a LoginButton to your UI
            val manager = LoginManager.getInstance()
            manager.logInWithPublishPermissions(context, permissionNeeds)
            manager.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
                override fun onSuccess(loginResult: LoginResult) {
                    val sharePhoto = SharePhoto.Builder().setBitmap(image).build()
                    val sharePhotoContent = SharePhotoContent.Builder().setPhotos(listOf(sharePhoto)).build()
                    ShareApi.share(sharePhotoContent, object : FacebookCallback<Sharer.Result> {
                        override fun onSuccess(result: Sharer.Result) {

                        }

                        override fun onCancel() {

                        }

                        override fun onError(error: FacebookException) {

                        }
                    })
                }

                override fun onCancel() {
                    Log.e("onCancel", "onCancel")
                }

                override fun onError(error: FacebookException) {
                    Log.e("onError", "OnError");
                }
            })

        }

    }

    private fun isFacebookInstalled(context: Context): Boolean {
        val pm = context.packageManager
        try {
            pm.getPackageInfo(AppConstants.FACEBOOK_PACKAGE, PackageManager.GET_META_DATA)
        } catch (e: PackageManager.NameNotFoundException) {
            return false
        }
        return true
    }*/

    private fun instagramShare(imagePath: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(AppConstants.INSTAGRAM_PACKAGE)
        if (intent != null) {
            val shareIntent = Intent()
            shareIntent.action = Intent.ACTION_SEND
            shareIntent.`package` = AppConstants.INSTAGRAM_PACKAGE
            try {
                shareIntent.putExtra(Intent.EXTRA_STREAM,
                        Uri.parse(MediaStore.Images.Media.insertImage(context.contentResolver,
                                imagePath, context.getString(R.string.app_name), AppConstants.HASH_TAG)))
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }

            shareIntent.type = "image/jpeg"
            context.startActivity(shareIntent)
        } else {
            Toast.makeText(context, "Instagram not installed", Toast.LENGTH_SHORT).show()
        }
    }


    private fun whatsappShare(imagePath: String) {
        try {
            val share = Intent(Intent.ACTION_SEND)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                share.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT)
            else
                share.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            if (imagePath.isEmpty()) {
                share.type = "text/plain"
            } else {
                share.type = "image/*"
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse(imagePath))
            }
            share.putExtra(Intent.EXTRA_TEXT, if (content.isEmpty()) AppConstants.SHARE_MESSAGE + AppConstants.APP_SHARE_LINK else
                content + AppConstants.APP_SHARE_LINK)
            share.`package` = "com.whatsapp"
            context.startActivity(share)
        } catch (e: Exception) {
            Toast.makeText(context, "WhatsApp not installed", Toast.LENGTH_SHORT).show()
        }
    }


    private fun otherShare() {
        val shareIntent = ShareCompat.IntentBuilder.from(context)
                .setType("text/plain")
                .setText("${AppConstants.SHARE_TITLE}\n {${if (content.isBlank()) AppConstants.SHARE_MESSAGE else content}}\n${AppConstants.APP_SHARE_LINK}")
                .intent
        context.startActivity(shareIntent)
    }
}