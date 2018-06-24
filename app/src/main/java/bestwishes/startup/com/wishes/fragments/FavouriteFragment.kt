package bestwishes.startup.com.wishes.fragments

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import bestwishes.startup.com.wishes.BuildConfig
import bestwishes.startup.com.wishes.R
import bestwishes.startup.com.wishes.adapter.HomeAdapter
import bestwishes.startup.com.wishes.customview.ranimation.SlideInBottomAnimatorAdapter
import bestwishes.startup.com.wishes.interfaces.viewcallbacks.IHomeDatabaseCallback
import bestwishes.startup.com.wishes.localdatabase.HomeFragmentDatabaseOperation
import bestwishes.startup.com.wishes.model.viewmodel.HomeViewModel
import bestwishes.startup.com.wishes.slidedismiss.SimpleItemTouchHelperCallback
import com.google.android.youtube.player.YouTubeApiServiceUtil
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubeStandalonePlayer
import kotlinx.android.synthetic.main.recyler_view.*


/**
 * Created by rajesh on 10/12/17.
 */
class FavouriteFragment : BaseFragment(), IHomeDatabaseCallback, HomeAdapter.IRecyclerViewItemClickCallBack {


    private lateinit var homeData: MutableList<HomeViewModel>
    private lateinit var homeDatabaseOperation: HomeFragmentDatabaseOperation
    private lateinit var scaleInAnimatorAdapter: SlideInBottomAnimatorAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.recyler_view, container, false)
    }

    private fun fetchDataFromLocalDatabase() {
        homeDatabaseOperation = HomeFragmentDatabaseOperation()
        homeDatabaseOperation.getFavouriteData(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pullToRefresh.setOnRefreshListener {
            fetchDataFromLocalDatabase()
        }
        homeData = mutableListOf()
        val homeAdapter = HomeAdapter(homeData, true)
        scaleInAnimatorAdapter = SlideInBottomAnimatorAdapter(homeAdapter)
        homeAdapter.initializeCallback(this)
        recyclerView.adapter = scaleInAnimatorAdapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        val callback = SimpleItemTouchHelperCallback(homeAdapter)
        val mItemTouchHelper = ItemTouchHelper(callback)
        mItemTouchHelper.attachToRecyclerView(recyclerView)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        fetchDataFromLocalDatabase()
    }

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

    override fun favouriteData(data: List<HomeViewModel>) {
        if (context != null) {
            if (pullToRefresh.isRefreshing)
                pullToRefresh.isRefreshing = false
            if (data.isEmpty()) {
                noData()
            } else {
                text_noData.visibility = View.GONE
                homeData.clear()
                homeData.addAll(data)
                scaleInAnimatorAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun noData() {
        if (homeData.isEmpty()) text_noData.visibility = View.VISIBLE
    }

    fun addNewData(homeViewModel: HomeViewModel) {
        if (context != null) {
            val position = homeData.indexOf(homeViewModel)
            if (position < 0) {
                text_noData.visibility = View.GONE
                homeData.add(homeViewModel)
                scaleInAnimatorAdapter.notifyItemInserted(homeData.size)
            }
        }
    }

    fun removeData(homeViewModel: HomeViewModel) {
        if (context != null) {
            val position = homeData.indexOf(homeViewModel)
            if (position > -1) {
                homeData.remove(homeViewModel)
                scaleInAnimatorAdapter.notifyItemRemoved(position)
                noData()
            }

        }
    }


}