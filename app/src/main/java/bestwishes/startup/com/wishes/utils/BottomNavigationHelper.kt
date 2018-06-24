package bestwishes.startup.com.wishes.utils

import android.content.Context
import android.support.design.internal.BottomNavigationMenuView
import android.support.design.widget.BottomNavigationView
import android.support.v4.content.res.ResourcesCompat
import android.util.TypedValue
import android.view.View
import android.widget.TextView

import bestwishes.startup.com.wishes.R

/**
 * Created by rajesh on 10/12/17.
 */

class BottomNavigationHelper {
    fun applyBottomNavFont(context: Context, mBottomNav: BottomNavigationView) {
        // The BottomNavigationView widget doesn't provide a native way to set the appearance of
        // the text views. So we have to hack in to the view hierarchy here.
        for (i in 0 until mBottomNav.childCount) {
            val child = mBottomNav.getChildAt(i)
            if (child is BottomNavigationMenuView) {
                for (j in 0 until child.childCount) {
                    val item = child.getChildAt(j)
                    val smallItemText = item.findViewById<View>(android.support.design.R.id.smallLabel)
                    if (smallItemText is TextView) {
                        // Set font
                        smallItemText.typeface = ResourcesCompat.getFont(context, R.font.toolbar_font)
                        smallItemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                    }
                    val largeItemText = item.findViewById<View>(android.support.design.R.id.largeLabel)
                    if (largeItemText is TextView) {
                        // Set font here
                        largeItemText.typeface = ResourcesCompat.getFont(context, R.font.toolbar_font)
                        largeItemText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
                    }
                }
            }
        }
    }
}
