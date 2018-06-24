package bestwishes.startup.com.wishes.dialog

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AppCompatDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bestwishes.startup.com.wishes.R
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.dialog_fullscreen_image.*


/**
 * Created by rajesh on 22/12/17.
 */
class FullscreenImageDialog : AppCompatDialogFragment() {
    object ImageUrl {
        val IMAGE_URL = "image"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.fullScreenDialog)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_fullscreen_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            iv_close.setOnClickListener { dismiss() }
            Glide.with(this).load(arguments?.getString(ImageUrl.IMAGE_URL)).into(iv_full_image)
        }


    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            if (dialog.window != null) dialog.window.setLayout(width, height)
        }
    }

}