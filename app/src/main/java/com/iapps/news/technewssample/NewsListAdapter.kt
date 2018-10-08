package com.iapps.news.technewssample

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
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
        private val bylineText: TextView = root.findViewById(R.id.item_byline)
        private val imageView: ImageView = root.findViewById(R.id.item_image)

        override fun toString(): String {
            return String.format(ENGLISH, "%s > %s", super.toString(), titleText.text)
        }

        internal fun bind(news: NewsEntity, listener: View.OnClickListener) {
            root.tag = news
            root.setOnClickListener(listener)

            titleText.text = news.title
            bylineText.text = news.byline

            val mediaUrl = news.mediaUrl() ?: return
            val caption = imageView.context.getString(R.string.place_holder)
            imageView.contentDescription = news.mediaCaption(caption)
            Glide.with(root)
                .load(mediaUrl)
                .apply(RequestOptions()
                    .placeholder(R.drawable.place_holder)
                    .fitCenter())
                .into(imageView)
        }
    }
}
