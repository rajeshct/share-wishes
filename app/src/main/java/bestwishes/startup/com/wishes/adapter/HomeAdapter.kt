package bestwishes.startup.com.wishes.adapter

import android.graphics.Bitmap
import android.graphics.Color
import android.support.v7.widget.AppCompatImageView
import android.support.v7.widget.AppCompatTextView
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import bestwishes.startup.com.wishes.R
import bestwishes.startup.com.wishes.constants.AppConstants
import bestwishes.startup.com.wishes.model.viewmodel.HomeViewModel
import bestwishes.startup.com.wishes.slidedismiss.ItemTouchHelperAdapter
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.include_favourite.view.*
import kotlinx.android.synthetic.main.include_share_layout.view.*
import kotlinx.android.synthetic.main.row_home_adapter_image.view.*
import kotlinx.android.synthetic.main.row_home_adapter_text.view.*

/**
 * Created by rajesh on 9/12/17.
 */
class HomeAdapter(private var homeData: MutableList<HomeViewModel>,
                  private var hideFavourite: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
        ItemTouchHelperAdapter {

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        return false
    }

    override fun onItemDismiss(position: Int) {
        iRecyclerViewCallback.saveToLocalDatabase(homeViewModel = homeData[position]
                , isSave = false, position = position, isFavourite = hideFavourite)
        homeData.removeAt(position)
        if (homeData.isEmpty()) {
            iRecyclerViewCallback.noData()
        }
        notifyItemRemoved(position)
    }

    lateinit var iRecyclerViewCallback: IRecyclerViewItemClickCallBack
    private var isViewLoading = false


    fun initializeCallback(iRecyclerViewCallback: IRecyclerViewItemClickCallBack) {
        this.iRecyclerViewCallback = iRecyclerViewCallback
    }

    object ViewTypes {
        val TEXT_VIEW_TYPE = 1
        val IMAGE_VIEW_TYPE = 2
        val VIDEO_VIEW_TYPE = 3
    }

    override fun getItemCount(): Int {
        return homeData.size
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder? {
        val layoutInflater = LayoutInflater.from(parent?.context)
        when (viewType) {
            ViewTypes.IMAGE_VIEW_TYPE -> return ImageViewHolder(layoutInflater.inflate(R.layout.row_home_adapter_image, parent, false))
            ViewTypes.VIDEO_VIEW_TYPE -> return VideoViewHolder(layoutInflater.inflate(R.layout.row_home_adapter_image, parent, false))
            ViewTypes.TEXT_VIEW_TYPE -> return TextViewHolder(layoutInflater.inflate(R.layout.row_home_adapter_text, parent, false))
        }
        return null
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        isViewLoading = true
        when (holder?.itemViewType) {
            ViewTypes.VIDEO_VIEW_TYPE -> setVideoData(holder as VideoViewHolder, position)
            ViewTypes.IMAGE_VIEW_TYPE -> setImageData(holder as ImageViewHolder, homeData[position], position)
            ViewTypes.TEXT_VIEW_TYPE -> setTextData(holder as TextViewHolder, homeData[position], position)
        }
        if (position % AppConstants.ADV_POSITION == 0 && position != 0)
            iRecyclerViewCallback.showInterstitialAD()
        isViewLoading = false
    }

    private fun setVideoData(videoViewHolder: VideoViewHolder, position: Int) {
        val homeViewModel = homeData[position]
        videoViewHolder.play_view.visibility = View.VISIBLE
        videoViewHolder.imageContent.setImage("https://i.ytimg.com/vi/${homeViewModel.content}/hqdefault.jpg")
        videoViewHolder.textSource.text = homeViewModel.contentSource
        if (homeViewModel.isFavourite)
            videoViewHolder.likeButton.setColorFilter(Color.parseColor(AppConstants.SELECTED_COLOR))
        else
            videoViewHolder.likeButton.setColorFilter(Color.parseColor(AppConstants.UN_SELECTED_COLOR))
        videoViewHolder.likeButton.setOnClickListener {
            val tempHomeViewModel = homeData[videoViewHolder.adapterPosition]
            if (tempHomeViewModel.isFavourite) {
                iRecyclerViewCallback.saveToLocalDatabase(tempHomeViewModel, false, videoViewHolder.adapterPosition, hideFavourite)
            } else {
                tempHomeViewModel.isFavourite = true
                if (!isViewLoading) notifyItemChanged(position)
                iRecyclerViewCallback.saveToLocalDatabase(tempHomeViewModel, true, videoViewHolder.adapterPosition, hideFavourite)
            }
        }
        videoViewHolder.imageContent.setOnClickListener { iRecyclerViewCallback.playVideo(homeViewModel.content) }
        videoViewHolder.fbShare.setOnClickListener {
            iRecyclerViewCallback.textShare(homeViewModel.content,
                    AppConstants.FACEBOOK_SHARE, homeViewModel.hashCode().toString())
        }
        videoViewHolder.whatsAppShare.setOnClickListener {
            iRecyclerViewCallback.textShare(homeViewModel.content,
                    AppConstants.WHATSAPP_SHARE, homeViewModel.hashCode().toString())
        }
        videoViewHolder.instagramShare.setOnClickListener {
            OnItemClick(AppConstants.INSTAGRAM_SHARE, image = videoViewHolder.imageContent, tag = homeViewModel.hashCode().toString())
        }
        videoViewHolder.otherShare.setOnClickListener {
            iRecyclerViewCallback.textShare(homeViewModel.content,
                    AppConstants.OTHER_SHARE, homeViewModel.hashCode().toString())
        }
    }


    private fun setTextData(textViewHolder: TextViewHolder, homeViewModel: HomeViewModel, position: Int) {
        textViewHolder.textContent.text = homeViewModel.content
        textViewHolder.textSource.text = homeViewModel.contentSource

        if (!homeViewModel.contentBackground.isEmpty()) {
            try {
                textViewHolder.textContent.setBackgroundColor(Color.parseColor(homeViewModel.contentBackground))
            } catch (exp: IllegalArgumentException) {

            }
        }
        if (!homeViewModel.contentColor.isEmpty()) {
            try {
                textViewHolder.textContent.setTextColor(Color.parseColor(homeViewModel.contentColor))
            } catch (exp: IllegalArgumentException) {

            }
        }
        if (homeViewModel.isFavourite)
            textViewHolder.likeButton.setColorFilter(Color.parseColor(AppConstants.SELECTED_COLOR))
        else
            textViewHolder.likeButton.setColorFilter(Color.parseColor(AppConstants.UN_SELECTED_COLOR))

        textViewHolder.likeButton.setOnClickListener {
            val tempHomeViewModel = homeData[textViewHolder.adapterPosition]
            if (tempHomeViewModel.isFavourite) {
                iRecyclerViewCallback.saveToLocalDatabase(tempHomeViewModel
                        , false, textViewHolder.adapterPosition, hideFavourite)
            } else {
                tempHomeViewModel.isFavourite = true
                if (!isViewLoading) notifyItemChanged(position)
                iRecyclerViewCallback.saveToLocalDatabase(tempHomeViewModel,
                        true, textViewHolder.adapterPosition, hideFavourite)
            }
        }
        shareClick(textViewHolder, textViewHolder, homeViewModel)
    }

    private fun setImageData(imageViewHolder: ImageViewHolder, homeViewModel: HomeViewModel, position: Int) {
        imageViewHolder.imageContent.setImage(homeViewModel.content)
        imageViewHolder.textSource.text = homeViewModel.contentSource
        if (homeViewModel.isFavourite)
            imageViewHolder.likeButton.setColorFilter(Color.parseColor(AppConstants.SELECTED_COLOR))
        else
            imageViewHolder.likeButton.setColorFilter(Color.parseColor(AppConstants.UN_SELECTED_COLOR))
        imageViewHolder.likeButton.setOnClickListener {
            val tempHomeViewModel = homeData[imageViewHolder.adapterPosition]
            if (tempHomeViewModel.isFavourite) {
                iRecyclerViewCallback.saveToLocalDatabase(tempHomeViewModel, false, imageViewHolder.adapterPosition, hideFavourite)
            } else {
                tempHomeViewModel.isFavourite = true
                if (!isViewLoading) notifyItemChanged(position)
                iRecyclerViewCallback.saveToLocalDatabase(tempHomeViewModel, true, imageViewHolder.adapterPosition, hideFavourite)
            }
        }
        imageViewHolder.imageContent.setOnClickListener { iRecyclerViewCallback.openFullImage(homeViewModel.content) }
        shareClick(imageViewHolder, imageViewHolder, homeViewModel)
    }

    private fun shareClick(shareViewHolder: ShareViewHolder, viewHolder: RecyclerView.ViewHolder, homeViewModel: HomeViewModel) {
        var textViewHolder: AppCompatTextView? = null
        var imageViewHolder: AppCompatImageView? = null

        if (viewHolder is TextViewHolder)
            textViewHolder = viewHolder.textContent
        else
            imageViewHolder = (viewHolder as ImageViewHolder).imageContent

        shareViewHolder.fbShare.setOnClickListener(OnItemClick(AppConstants.FACEBOOK_SHARE,
                image = imageViewHolder, content = textViewHolder
                , tag = homeViewModel.hashCode().toString()))
        shareViewHolder.whatsAppShare.setOnClickListener(OnItemClick(AppConstants.WHATSAPP_SHARE,
                image = imageViewHolder, content = textViewHolder, tag = homeViewModel.hashCode().toString()))
        shareViewHolder.instagramShare.setOnClickListener(OnItemClick(AppConstants.INSTAGRAM_SHARE,
                image = imageViewHolder, content = textViewHolder, tag = homeViewModel.hashCode().toString()))
        shareViewHolder.otherShare.setOnClickListener(OnItemClick(AppConstants.OTHER_SHARE,
                image = imageViewHolder, content = textViewHolder, tag = homeViewModel.hashCode().toString()))
    }

    override fun getItemViewType(position: Int): Int {
        return homeData[position].contentType
    }


    inner class TextViewHolder(view: View) : ShareViewHolder(view) {
        val textContent: AppCompatTextView = view.text_content
    }

    inner open class ImageViewHolder(view: View) : ShareViewHolder(view) {
        val imageContent: AppCompatImageView = view.iv_content
    }

    inner open class VideoViewHolder(view: View) : ImageViewHolder(view) {
        val play_view: AppCompatImageView = view.iv_play
    }

    inner class OnItemClick @JvmOverloads constructor(private val shareType: Int,
                                                      private val image: AppCompatImageView? = null,
                                                      private val content: AppCompatTextView? = null,
                                                      private val tag: String) : View.OnClickListener {
        override fun onClick(p0: View?) {
            when (shareType) {
                AppConstants.FACEBOOK_SHARE, AppConstants.OTHER_SHARE -> {
                    if (image == null)
                        iRecyclerViewCallback.textShare(content = content?.text.toString(),
                                shareType = shareType)
                    else
                        iRecyclerViewCallback.textShare(content = "", shareType = shareType)
                }
                else -> {
                    if (content == null && image != null) {
                        image.isDrawingCacheEnabled = true
                        iRecyclerViewCallback.imageShare(image.drawingCache, shareType, tag)
                    } else {
                        if (content != null) {
                            content.isDrawingCacheEnabled = true
                            iRecyclerViewCallback.imageShare(content.drawingCache, shareType, tag)
                        }
                    }
                }
            }
        }
    }

    inner open class ShareViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textSource: AppCompatTextView = view.text_source
        internal val fbShare: AppCompatImageView = view.ib_facebook_share
        internal val whatsAppShare: AppCompatImageView = view.ib_whatsapp_share
        internal val instagramShare: AppCompatImageView = view.ib_instagram_share
        internal val otherShare: AppCompatImageView = view.ib_other_share
        internal val likeButton: AppCompatImageView = view.star_button

        init {
            if (hideFavourite)
                likeButton.visibility = View.GONE
        }
    }


    interface IRecyclerViewItemClickCallBack {
        fun imageShare(image: Bitmap, shareType: Int, tag: String)
        fun textShare(content: String, shareType: Int, tag: String = "")
        fun showInterstitialAD()
        fun saveToLocalDatabase(homeViewModel: HomeViewModel, isSave: Boolean, position: Int, isFavourite: Boolean)
        fun noData()
        fun openFullImage(content: String)
        fun playVideo(content: String)
    }

    private fun AppCompatImageView.setImage(imagePath: String) {
        Glide.with(this).load(imagePath).into(this)
    }
}

