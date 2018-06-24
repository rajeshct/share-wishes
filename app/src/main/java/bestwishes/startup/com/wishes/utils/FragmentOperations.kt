package bestwishes.startup.com.wishes.utils

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction


/**
 * Created by rajesh on 9/12/17.
 */
class FragmentOperations private constructor(){


    private object Holder { val INSTANCE = FragmentOperations() }

    companion object {
        val instance: FragmentOperations by lazy { Holder.INSTANCE }
    }


    fun openFragment(manager: FragmentManager, clazz: Class<out Fragment>, frameContent: Int) {
        val transaction = manager.beginTransaction()
        val tag = clazz.simpleName
        if (!isFragmentAdded(manager, tag)) {
            val fragment: Fragment
            try {
                fragment = clazz.newInstance()
                val currentFragment = getCurrentFragment(manager)
                if (currentFragment != null) {
                    transaction.hide(currentFragment)
                }
                transaction.add(frameContent, fragment, tag)
                transaction.commit()
            } catch (e: InstantiationException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }

        } else {
            showFragment(manager, tag, transaction)
        }
    }

    private fun isFragmentAdded(manager: FragmentManager, tag: String): Boolean {
        val fragmentList = manager.fragments
        if (fragmentList != null) {
            if (fragmentList.size > 0) {
                fragmentList
                        .filter { it != null && it::class.java.simpleName == tag }
                        .forEach { return true }
            }
        }
        return false
    }

    private fun showFragment(manager: FragmentManager, tag: String, transaction: FragmentTransaction) {
        val fragmentList = manager.fragments
        fragmentList?.filterNotNull()?.forEach {
            if (it::class.java.simpleName == tag) {
                transaction.show(it)
            } else {
                transaction.hide(it)
            }
        }
        transaction.commit()
    }

    private fun getCurrentFragment(manager: FragmentManager): Fragment? {
        val fragmentList = manager.fragments
        if (fragmentList != null)
            if (fragmentList.size > 0)
                fragmentList
                        .filter { it != null && it.isVisible }
                        .forEach { return it }
        return null
    }


}