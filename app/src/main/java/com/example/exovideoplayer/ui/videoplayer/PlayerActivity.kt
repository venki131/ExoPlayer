package com.example.exovideoplayer.ui.videoplayer

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.exovideoplayer.databinding.ActivityPlayerBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlayerActivity : AppCompatActivity() {

    private val viewBinding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityPlayerBinding.inflate(layoutInflater)
    }

    private val model: SamplePagingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(viewBinding.root)
        initRvAdapter()
        //HandleException(this,this).dummyApiCall()
    }

    private var recyclerView: RecyclerView? = null
    private val lifecycleOwner: LifecycleOwner = this
    private fun initRvAdapter() {
        recyclerView = viewBinding.videoRecyclerview
        val videoUrls = listOf(
            "https://storage.googleapis.com/stockgro-feed-assets-qa/post_media/428de1c3-03d2-46a5-a5b8-2bab2eca9850.mp4",
            "https://storage.googleapis.com/stockgro-feed-assets-qa/post_media/5b007f34-5ecb-4d0f-8972-fa643ad528b5.mp4",
            "https://storage.googleapis.com/stockgro-feed-assets-qa/post_media/07ef8030-d627-47bc-a09d-b0a34514c3f8.mp4",
            "https://storage.googleapis.com/stockgro-feed-assets-qa/post_media/b5ad16e9-01d6-4a29-9b6f-cb68069f4d85.mp4",
            "https://storage.googleapis.com/stockgro-feed-assets-qa/post_media/8f55abf0-ffd5-42d3-8253-64a28470ac9d.mp4",
            "https://storage.googleapis.com/exoplayer-test-media-0/BigBuckBunny_320x180.mp4",
            "https://storage.googleapis.com/exoplayer-test-media-0/Jazz_In_Paris.mp3",
        )

        val repeatedVideoUrls = MutableList<String?>(50) {
            videoUrls[it % videoUrls.size]
        }

        val videoPlayer = VideoPlayer(this)
        //val adapter = VideoPagingAdapter(videoPlayer)
        val adapter = VideoRvAdapter(this, repeatedVideoUrls)
        /*lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                model.pageList.collectLatest {
                    adapter.submitData(it)
                }
            }
        }*/

        recyclerView?.adapter = adapter
        val videoPlayScrollListener = VideoPlayScrollListener(lifecycleOwner, videoPlayer)
        recyclerView?.addOnScrollListener(videoPlayScrollListener)
        viewBinding.addToFavourites.setOnClickListener {
            adapter.notifyDataSetChanged()
        }
    }
}