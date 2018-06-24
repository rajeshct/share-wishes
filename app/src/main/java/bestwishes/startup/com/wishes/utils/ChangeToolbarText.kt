package bestwishes.startup.com.wishes.utils

import android.support.v4.content.res.ResourcesCompat
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.text.TextUtils
import android.util.TypedValue
import android.view.View
import android.widget.TextView
import bestwishes.startup.com.wishes.R


/**
 * Created by rajesh on 10/12/17.
 */
class ChangeToolbarText {
    fun getToolbarTitleView(activity: AppCompatActivity, toolbar: Toolbar) {
        val actionBar = activity.supportActionBar
        var actionbarTitle: CharSequence? = null
        if (actionBar != null)
            actionbarTitle = actionBar.title
        actionbarTitle = if (TextUtils.isEmpty(actionbarTitle)) toolbar.title else actionbarTitle
        if (TextUtils.isEmpty(actionbarTitle)) {
            return
        }
        // can't find if title not set
        for (i in 0 until toolbar.childCount) {
            val v = toolbar.getChildAt(i)
            if (v != null && v is TextView) {
                val title = v.text
                if (!TextUtils.isEmpty(title) && actionbarTitle == title && v.id == View.NO_ID) {
                    val myTypeface = ResourcesCompat.getFont(activity, R.font.toolbar_font)
                    v.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16f)
                    v.typeface = myTypeface
                    return
                }
            }
        }
    }
}