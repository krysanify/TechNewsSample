package com.iapps.news.technewssample

import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

class NewsInfoFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_news_info, container, false)
        val args = arguments ?: return view
        val news = args.getParcelable<NewsEntity>("news") ?: return view

        var text: TextView
        text = view.findViewById(R.id.text_title)
        text.text = news.title

        text = view.findViewById(R.id.text_abstract)
        text.text = news.abstract

        val button = view.findViewById<Button>(R.id.btn_story)
        button.setOnClickListener { _ ->
            startActivity(Intent().apply {
                action = ACTION_VIEW
                data = Uri.parse(news.url)
            })
        }

        val imageView = view.findViewById<ImageView>(R.id.image_media)
        val mediaUrl = news.mediaUrl() ?: return view
        val caption = imageView.context.getString(R.string.place_holder)
        imageView.contentDescription = news.mediaCaption(caption)
        Glide.with(view)
            .load(mediaUrl)
            .apply(RequestOptions()
                .placeholder(R.drawable.place_holder)
                .fitCenter())
            .into(imageView)
        return view
    }
}
