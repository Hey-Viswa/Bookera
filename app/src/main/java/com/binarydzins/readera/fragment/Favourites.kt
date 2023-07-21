package com.binarydzins.readera.fragment

import DashboardRecyclerAdapter
import android.annotation.SuppressLint
import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.binarydzins.readera.R
import com.binarydzins.readera.adapter.FavouriteRecyclerAdapter
import com.binarydzins.readera.database.BookEntity
import com.binarydzins.readera.database.Bookdatabase

class Favourites : Fragment() {

    lateinit var recyclerFavourite :RecyclerView
    lateinit var progressLayout :RelativeLayout
    lateinit var progressBar : ProgressBar
    lateinit var layoutManager : RecyclerView.LayoutManager
    lateinit var recyclerAdapter : FavouriteRecyclerAdapter
    var dbBookList = listOf<BookEntity>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_favourites,container,false)

        recyclerFavourite = view.findViewById(R.id.recyclerFavourites)
        progressLayout = view.findViewById(R.id.progressLayout)
        progressBar = view.findViewById(R.id.progressBar)

        layoutManager = GridLayoutManager(activity as Context,2)

        dbBookList = RetrieveFavourites(activity as Context).execute().get()


        if (activity != null) {
            progressLayout.visibility = View.GONE
            recyclerAdapter = FavouriteRecyclerAdapter(activity as Context,dbBookList)
            recyclerFavourite.adapter = recyclerAdapter
            recyclerFavourite.layoutManager = layoutManager
        }

        return view
    }


    class RetrieveFavourites(val context: Context): AsyncTask<Void, Void, List<BookEntity>>(){
        override fun doInBackground(vararg p0: Void?): List<BookEntity> {
            val db = Room.databaseBuilder(context,Bookdatabase::class.java,"book-db").build()

            return db.BookDao().getAllBooks()
        }

    }


}