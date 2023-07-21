package com.binarydzins.readera.fragment

import DashboardRecyclerAdapter
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.binarydzins.readera.R
import com.binarydzins.readera.R.id.action_sort
import com.binarydzins.readera.mode.Book
import com.binarydzins.readera.util.ConnectionManager
import org.json.JSONException
import java.util.Collections


class Dashboard : Fragment() {

    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager

    val bookInfoList = arrayListOf<Book>()
    lateinit var progressLayout : RelativeLayout
    lateinit var progressBar : ProgressBar

    var ratingComparator = Comparator<Book>{ book1, book2 ->

       if (book1.bookRating.compareTo(book2.bookRating, true)==0){
           book1.bookName .compareTo(book2.bookName,true )
       }else{
           book1.bookRating.compareTo(book2.bookRating,true)
       }
    }

    private lateinit var recyclerAdapter: DashboardRecyclerAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_dashboard_, container, false)

        setHasOptionsMenu(true)

        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)

        layoutManager = LinearLayoutManager(activity)

        val queue = Volley.newRequestQueue(activity as Context)
        val url = "http://13.235.250.119/v1/book/fetch_books/"



        progressBar = view.findViewById(R.id.progressBar)
        progressLayout = view.findViewById(R.id.progressLayout)


        progressLayout.visibility = View.VISIBLE


        if(ConnectionManager().checkConnectivity(activity as Context)){
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {

                    try {
                        val success = it.getBoolean("success")
                        progressLayout.visibility = View.GONE
                        if (success) {
                            val data = it.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val bookJsonObject = data.getJSONObject(i)
                                val bookObject = Book(
                                    bookJsonObject.getString("book_id"),
                                    bookJsonObject.getString("name"),
                                    bookJsonObject.getString("author"),
                                    bookJsonObject.getString("rating"),
                                    bookJsonObject.getString("price"),
                                    bookJsonObject.getString("image")
                                )
                                bookInfoList.add(bookObject)
                                recyclerAdapter =
                                    DashboardRecyclerAdapter(activity as Context, bookInfoList)

                                recyclerDashboard.adapter = recyclerAdapter
                                recyclerDashboard.layoutManager = layoutManager

                            }
                        } else {
                            Toast.makeText(activity as Context, "Some Error Occured", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }catch (e: JSONException){
                        Toast.makeText(activity as Context, "Some Unexpected Error Has Occured!!! ", Toast.LENGTH_SHORT).show()
                    }



                }, Response.ErrorListener {
                    if (activity != null){
                        Toast.makeText(activity as Context, "Volley error occurred!", Toast.LENGTH_SHORT).show()
                    }
                }) {

                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-type"] = "application/json"
                        headers["token"] = "27af6bb10488c6"
                        return headers
                    }
                }

            queue.add(jsonObjectRequest)

        }else{
            val Dialog = AlertDialog.Builder(activity as Context)
            Dialog.setTitle("Error")
            Dialog.setMessage("Internet Connection Not Found")
            Dialog.setPositiveButton("Open Settings") { text, listner ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                activity?.finish()
            }
            Dialog.setNegativeButton("Exit") { text, listner ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            Dialog.create()
            Dialog.show()
        }

        return view
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
            inflater?.inflate(R.menu.menu_dashboard, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val id = item?.itemId
        if(id == action_sort){
            Collections.sort(bookInfoList,ratingComparator)
            bookInfoList.reverse()
        }
        recyclerAdapter.notifyDataSetChanged()

        return super.onOptionsItemSelected(item)
    }

}