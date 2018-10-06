package com.iapps.news.technewssample

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.facebook.drawee.backends.pipeline.Fresco
import com.facebook.drawee.view.SimpleDraweeView
import com.facebook.imagepipeline.request.ImageRequest

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

        val imageView = view.findViewById<SimpleDraweeView>(R.id.image_media)
        val mediaUrl = news.multimedia[0].url
        val request = ImageRequest.fromUri(Uri.parse(mediaUrl))
        imageView.controller = Fresco.newDraweeControllerBuilder()
            .setImageRequest(request)
            .setOldController(imageView.controller)
            .build()
        return view
    }
}
