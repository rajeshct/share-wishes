package bestwishes.startup.com.wishes.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import bestwishes.startup.com.wishes.BuildConfig
import bestwishes.startup.com.wishes.R
import bestwishes.startup.com.wishes.adapter.HomeAdapter
import bestwishes.startup.com.wishes.customview.ranimation.SlideInBottomAnimatorAdapter
import bestwishes.startup.com.wishes.firebase.FireBaseGetData
import bestwishes.startup.com.wishes.interfaces.viewcallbacks.IHomeDataCallback
import bestwishes.startup.com.wishes.model.viewmodel.HomeViewModel
import com.google.android.youtube.player.YouTubeApiServiceUtil
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubeStandalonePlayer
import kotlinx.android.synthetic.main.recyler_view.*

/**
 * Created by rajesh on 9/12/17.
 */
class HomeFragment : BaseFragment(), IHomeDataCallback, HomeAdapter.IRecyclerViewItemClickCallBack {


    private lateinit var scaleInAnimatorAdapter: SlideInBottomAnimatorAdapter
    private var isVisibleToUser = false
    private lateinit var homeData: MutableList<HomeViewModel>

    override fun showProgress() {
        if (context != null) {
            loading.visibility = View.VISIBLE
        }
    }

    override fun hideProgress() {
        if (context != null) {
            loading.visibility = View.GONE
        }
    }

    override fun showMessage(message: String) {
        if (context != null) {
            text_noData.visibility = View.VISIBLE
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }


    override fun homeData(data: MutableList<HomeViewModel>) {
        if (isVisibleToUser) {
            if (pullToRefresh.isRefreshing)
                pullToRefresh.isRefreshing = false
            if (data.isEmpty()) {
                noData()
            } else {
                this.homeData.clear()
                this.homeData.addAll(data)
                scaleInAnimatorAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun noData() {
        if (context != null) text_noData.visibility = View.VISIBLE
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.recyler_view, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        homeData = mutableListOf()
        val homeAdapter = HomeAdapter(homeData, false)
        scaleInAnimatorAdapter = SlideInBottomAnimatorAdapter(homeAdapter)
        homeAdapter.initializeCallback(this)
        recyclerView.adapter = scaleInAnimatorAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        pullToRefresh.setOnRefreshListener {
            if (homeData.isEmpty()) {
                FireBaseGetData.instance.getDataFromFireBase(this)
            } else {
                pullToRefresh.isRefreshing = false
                pullToRefresh.isEnabled = false
            }
        }
    }


    override fun onPause() {
        super.onPause()
        isVisibleToUser = false
    }

    override fun onResume() {
        super.onResume()
        if (homeData.isEmpty()) {
            FireBaseGetData.instance.getDataFromFireBase(this)
        }
        isVisibleToUser = true
    }


    /**
     * After updating local database update list also
     */
    override fun updateList(position: Int) {
        if (context != null) {
            homeData[position].isFavourite = false
            scaleInAnimatorAdapter.notifyItemChanged(position)
        }
    }

    fun updateFavourite(homeViewModel: HomeViewModel) {
        if (context != null) {
            homeViewModel.isFavourite = true
            val position = homeData.indexOf(homeViewModel)
            if (position >= 0) {
                homeData[position].isFavourite = false
                scaleInAnimatorAdapter.notifyItemChanged(position)
            }
        }
    }



}