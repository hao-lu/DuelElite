package com.lucidity.haolu.searchcards.view.fragment

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.palette.graphics.Palette
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.android.material.tabs.TabLayout
import com.lucidity.haolu.searchcards.R
import com.lucidity.haolu.searchcards.viewmodel.SearchCardDetailsViewModel
import com.lucidity.haolu.searchcards.view.adapter.SearchCardDetailsViewPagerAdapter
import com.lucidity.haolu.searchcards.databinding.FragmentSearchCardDetailsBinding
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.net.URLEncoder
import java.net.UnknownHostException

class SearchCardDetailsFragment : Fragment() {

    private lateinit var viewPagerAdapter: SearchCardDetailsViewPagerAdapter
    private lateinit var binding: FragmentSearchCardDetailsBinding
    private lateinit var viewmodel: SearchCardDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewmodel = ViewModelProvider(this).get(SearchCardDetailsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_search_card_details,
            container,
            false
        )
//        binding.viewmodel = viewmodel
        val cardName = arguments?.getString("cardName")
        LoadImageHeaderTask(
            requireContext()
        ).execute(cardName)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val cardName = arguments?.getString("cardName")
        viewPagerAdapter =
            SearchCardDetailsViewPagerAdapter(
                this,
                cardName!!
            )
        binding.container.adapter = viewPagerAdapter

//        val tabLayout = view.findViewById<TabLayout>(R.id.tabs)
//        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
//            tab.text = when (position) {
//                0 -> getString()
//                else -> getString(R.string.roll)
//            }
//        }.attach()

    }

    /**
     * Gets the mImageUrl for the card
     */
    private class LoadImageHeaderTask(val context: Context) : AsyncTask<String, Void, Void>() {
        private val TAG = "LoadImageHeaderTask"
        private val BASE_URL = "https://yugioh.wikia.com/wiki/"
        private var mImageUrl = ""
        private lateinit var mTarget: Target
        private val mActivity = context as AppCompatActivity

        override fun onPreExecute() {
            super.onPreExecute()
        }

        override fun doInBackground(vararg params: String?): Void? {

            val cardName = params[0]
            val encoder = URLEncoder.encode(cardName!!, "UTF-8")
            val cardNamePath = encoder.replace("+", "_")
            val cardUrl = BASE_URL + cardNamePath

            try {
                Log.d(TAG, "QUERY : " + cardNamePath.replace(" ", "_"))
                val document = Jsoup.connect(cardUrl).get()
                // <table class = cardtable>
                val cardTable: Element = document.getElementsByClass("cardtable").first()
                // <a href = ... >
                mImageUrl = cardTable.select("tr")[1].
                getElementsByClass("cardtable-cardimage")[0].
                select("a[href]")[0].
                attr("href").toString()
                Log.d(TAG, mImageUrl)
            } catch (httpStatusException: HttpStatusException) {
                Log.d(TAG, "No webpage")
            } catch (unknownHostException: UnknownHostException) {
                mActivity.runOnUiThread {  Toast.makeText(context, "No internet connection. Connect to the internet and try again.", Toast.LENGTH_LONG).show() }
                Log.d(TAG, "No Internet")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }

        override fun onPostExecute(result: Void?) {
            super.onPostExecute(result)
            if (mImageUrl != "") {
                // Load image header with card image
//                val mActivity = context as AppCompatActivity
                val imageHeader = mActivity.findViewById(R.id.image_header) as ImageView
                val collapseToolbar = mActivity.findViewById(R.id.collapse_toolbar) as CollapsingToolbarLayout
                val tabs = mActivity.findViewById(R.id.tabs) as TabLayout
                Picasso.with(context).load(mImageUrl).into(imageHeader)

                mTarget = object : Target {
                    override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}

                    override fun onBitmapFailed(errorDrawable: Drawable?) {}

                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
//                    imageHeader.setImageBitmap(bitmap)
                        val swatch = Palette.from(bitmap!!).setRegion(25, 25, 35, 35).generate()
                        val dominant = swatch.dominantSwatch
                        if (dominant == null) Log.d(TAG, "DOMINANT NULL")
                        if (dominant != null) {
                            collapseToolbar.setContentScrimColor(dominant.rgb)
                            tabs.setSelectedTabIndicatorColor(dominant.rgb)
                            mActivity.window.statusBarColor = dominant.rgb
                        }
//                        val color = swatch.lightVibrantSwatch
//                        if (color != null) {
//                            tabs.setSelectedTabIndicatorColor(color.rgb)
//                        }
                    }
                }
                Picasso.with(context).load(mImageUrl).into(mTarget)

                // Load fragment when image is clicked
                imageHeader.setOnClickListener {
                    val bundle = Bundle()
                    bundle.putString("imageUrl", mImageUrl)

                    val fragment =
                        ImageDialogFragment()
                    fragment.arguments = bundle
                    val ft = mActivity.supportFragmentManager.beginTransaction()
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                    ft.replace(android.R.id.content, fragment).addToBackStack(null).commit()
                }
            }
        }
    }

}