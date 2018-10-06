package com.iapps.news.technewssample

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequest
import java.util.Locale.ENGLISH

/**
 *
 */
class NewsListAdapter internal constructor(
    private val mValues: List<NewsEntity>,
    private val mListener: View.OnClickListener
) : RecyclerView.Adapter<NewsListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_news_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(mValues[position], mListener)
    }

    override fun getItemCount(): Int {
        return mValues.size
    }

    inner class ViewHolder internal constructor(internal val root: View) :
        RecyclerView.ViewHolder(root) {
        private val titleText: TextView = root.findViewById(R.id.item_title)
        private val imageView: SimpleDraweeView = root.findViewById(R.id.item_image)

        override fun toString(): String {
            return String.format(ENGLISH, "%s > %s", super.toString(), titleText.text)
        }

        internal fun bind(news: NewsEntity, listener: View.OnClickListener) {
            titleText.text = news.title
            val mediaUrl = news.multimedia[0].url
            val request = ImageRequest.fromUri(Uri.parse(mediaUrl))
            imageView.controller = Fresco.newDraweeControllerBuilder()
                .setImageRequest(request)
                .setOldController(imageView.controller)
                .build()

            root.tag = news
            root.setOnClickListener(listener)
        }
    }
}
