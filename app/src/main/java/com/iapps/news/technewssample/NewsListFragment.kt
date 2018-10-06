package com.iapps.news.technewssample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

/**
 *
 */
class NewsListFragment : Fragment(), NewsService.Callback, View.OnClickListener {
    private lateinit var recyclerView: RecyclerView
    private var listAdapter: NewsListAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_news_list, container, false)
        recyclerView = view.findViewById(R.id.list)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (null == listAdapter) {
            NewsService.getNews(this)
        }
        recyclerView.adapter = listAdapter
    }

    override fun onError(message: String) {
        val snack = Snackbar.make(recyclerView, message, Snackbar.LENGTH_INDEFINITE)
        snack.setAction(android.R.string.ok) { snack.dismiss() }.show()
    }

    override fun onResult(results: List<NewsEntity>) {
        if (isDetached) return
        listAdapter = NewsListAdapter(results, this)
        recyclerView.adapter = listAdapter
    }

    override fun onClick(v: View) {
        val news = v.tag as NewsEntity
        val parent = activity ?: return
        val args = Bundle()
        args.putParcelable("news", news)
        Navigation.findNavController(parent, R.id.nav_host_fragment)
            .navigate(R.id.news_info_fragment, args)
    }
}
