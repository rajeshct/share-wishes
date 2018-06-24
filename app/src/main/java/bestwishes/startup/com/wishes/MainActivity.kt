package bestwishes.startup.com.wishes

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.BottomNavigationView
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import bestwishes.startup.com.wishes.constants.AppConstants.DIALOG_TAG
import bestwishes.startup.com.wishes.dialog.PermissionDialog
import bestwishes.startup.com.wishes.firebase.FireBaseGetData
import bestwishes.startup.com.wishes.fragments.FavouriteFragment
import bestwishes.startup.com.wishes.fragments.HomeFragment
import bestwishes.startup.com.wishes.interfaces.viewcallbacks.ActivityOpeartion
import bestwishes.startup.com.wishes.livedata.ToolbarTitle
import bestwishes.startup.com.wishes.model.viewmodel.HomeViewModel
import bestwishes.startup.com.wishes.sharecontents.ShareContents
import bestwishes.startup.com.wishes.utils.BottomNavigationHelper
import bestwishes.startup.com.wishes.utils.ChangeToolbarText
import bestwishes.startup.com.wishes.utils.FragmentOperations
import com.google.android.gms.ads.*
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ActivityOpeartion {


    private lateinit var toolbarTitle: ToolbarTitle
    lateinit var mAdView: AdView
    private var isVisibleToUser = false
    private lateinit var mInterstitialAd: InterstitialAd
    private var PERMISSIONS_REQUEST_CODE = 1
    var image: Bitmap? = null
    private var shareType = 0
    lateinit var tag: String


    override fun onPause() {
        mAdView.pause()
        isVisibleToUser = false
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        mAdView.resume()
        isVisibleToUser = true
    }


    public override fun onDestroy() {
        mAdView.destroy()
        super.onDestroy()
    }


    override fun showInterstitialAd() {
        if (mInterstitialAd.isLoaded)
            mInterstitialAd.show()
        else {
            if (!mInterstitialAd.isLoading && !mInterstitialAd.isLoaded) {
                val adRequest = AdRequest.Builder().build()
                mInterstitialAd.loadAd(adRequest)
            }
        }
    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.home -> {
                FragmentOperations.instance.openFragment(manager = supportFragmentManager
                        , clazz = HomeFragment::class.java, frameContent = R.id.frameLayout)
                return@OnNavigationItemSelectedListener true
            }
            R.id.favourite -> {
                FragmentOperations.instance.openFragment(manager = supportFragmentManager
                        , clazz = FavouriteFragment::class.java, frameContent = R.id.frameLayout)
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        loadBannerAd()
        loadInterstitialAd()
        setSupportActionBar(toolbar)
        updateToolbarLater()
        ChangeToolbarText().getToolbarTitleView(this, toolbar)
        BottomNavigationHelper().applyBottomNavFont(this, bottomNavigationView)
        FragmentOperations.instance.openFragment(manager = supportFragmentManager
                , clazz = HomeFragment::class.java, frameContent = R.id.frameLayout)
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
    }

    private fun updateToolbarLater() {
        // Get the ViewModel.
        toolbarTitle = ViewModelProviders.of(this).get(ToolbarTitle::class.java)
        FireBaseGetData.instance.getTitleFromFireBase(toolbarTitle)
        // Create the observer which updates the UI.
        val nameObserver = Observer<String> { t -> toolbar.title = t }
        // Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        toolbarTitle.getCurrentName()?.observe(this, nameObserver)
    }

    private fun loadInterstitialAd() {
        mInterstitialAd = InterstitialAd(this)
        mInterstitialAd.adUnitId = getString(R.string.interstiatialAdId)
        mInterstitialAd.loadAd(AdRequest.Builder()
                .build())
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            override fun onAdFailedToLoad(errorCode: Int) {
                // Code to be executed when an ad request fails.
            }

            override fun onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            override fun onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            override fun onAdClosed() {
                try {
                    // Code to be executed when when the interstitial ad is closed.
                    mInterstitialAd.loadAd(AdRequest.Builder().build())
                } catch (exp: Exception) {
                }
            }
        }
    }

    private fun loadBannerAd() {
        MobileAds.initialize(this, getString(R.string.adUnitId))
        mAdView = findViewById(R.id.adView)
        val adRequest = AdRequest.Builder()
                .build()
        mAdView.loadAd(adRequest)
        mAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                mAdView.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(p0: Int) {
                super.onAdFailedToLoad(p0)
                mAdView.visibility = View.GONE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_rate, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menu_rateUs) {
            val uri = Uri.parse("market://details?id=" + BuildConfig.APPLICATION_ID)
            val goToMarket = Intent(Intent.ACTION_VIEW, uri)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            else
                goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            try {
                startActivity(goToMarket)
            } catch (e: ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID)))
            }

        }
        return super.onOptionsItemSelected(item)
    }


    override fun imageShare(tag: String, image: Bitmap, shareType: Int) {
        // Here, thisActivity is the current activity
        this.image = image
        this.shareType = shareType
        this.tag = tag
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                // DialogFragment.show() will take care of adding the fragment
                // in a transaction.  We also want to remove any currently showing
                // dialog, so make our own transaction and take care of that here.
                val ft = supportFragmentManager.beginTransaction()
                val prev = supportFragmentManager.findFragmentByTag(DIALOG_TAG)
                if (prev != null) {
                    ft.remove(prev)
                }
                ft.addToBackStack(null)
                // Create and show the dialog.
                val newFragment = PermissionDialog()
                newFragment.show(ft, DIALOG_TAG)
            } else {
                // No explanation needed, we can request the permission.
                requestPermissionForApp()
                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            ShareContents(tag = tag, context = this, shareContents = shareType, bitmapImageForShare = image)
        }
    }

    override fun requestPermissionForApp() {
        ActivityCompat.requestPermissions(this, arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ),
                PERMISSIONS_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_CODE) {
            // If request is cancelled, the result arrays are empty.
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission was granted, yay! Do the
                // contacts-related task you need to do.
                Handler().postDelayed({ ShareContents(tag = tag, context = this@MainActivity, shareContents = shareType, bitmapImageForShare = image) }, 200)
            } else {
                // permission denied, boo! Disable the
                // functionality that depends on this permission.
                Snackbar.make(container, getString(R.string.requestPermission), Snackbar.LENGTH_LONG)
                        .setAction(getString(R.string.tryAgain), {

                        }).show()
            }
        }


    }

    override fun updateFavourite(homeViewModel: HomeViewModel, save: Boolean) {
        val favouriteFragment: Fragment? = supportFragmentManager.findFragmentByTag(FavouriteFragment::class.java.simpleName)
        if (favouriteFragment != null) {
            if (save) {
                (favouriteFragment as FavouriteFragment).addNewData(homeViewModel)
            } else {
                (favouriteFragment as FavouriteFragment).removeData(homeViewModel)
            }
        }
    }

    override fun updateHome(homeViewModel: HomeViewModel) {
        val favouriteFragment: Fragment? = supportFragmentManager.findFragmentByTag(HomeFragment::class.java.simpleName)
        if (favouriteFragment != null) {
            (favouriteFragment as HomeFragment).updateFavourite(homeViewModel)
        }
    }
}

