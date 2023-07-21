package com.binarydzins.readera.activity

import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.binarydzins.readera.R
import com.binarydzins.readera.database.BookEntity
import com.binarydzins.readera.database.Bookdatabase
import com.binarydzins.readera.util.ConnectionManager
import com.squareup.picasso.Picasso
import com.squareup.picasso.RequestCreator
import org.json.JSONObject

class Description_Activity : AppCompatActivity() {


    lateinit var txtBookName: TextView
    lateinit var txtBookAuthor: TextView
    lateinit var txtBookPrice: TextView
    lateinit var txtBookRating: TextView
    lateinit var imgBookImage: ImageView // Change the type to ImageView
    lateinit var toolbar: Toolbar
    lateinit var txtBookDesc: TextView
    lateinit var progressBar: ProgressBar
    lateinit var progressLayout: RelativeLayout
    lateinit var btnAddToFav: Button

    var bookId: String? = "100"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_description)

        txtBookName = findViewById(R.id.txtBookName)
        txtBookAuthor = findViewById(R.id.txtBookPrice)
        txtBookPrice = findViewById(R.id.txtBookPrice)
        txtBookRating = findViewById(R.id.txtBookPrice)
        imgBookImage = findViewById(R.id.imgBookImage)
        progressBar = findViewById(R.id.progressBar)
        txtBookDesc = findViewById(R.id.txtBookDesc)
        progressBar.visibility = View.GONE
        progressLayout = findViewById(R.id.progressLayout)
        progressLayout.visibility = View.GONE
        btnAddToFav = findViewById(R.id.btnAddToFav)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Book Details"

        if (intent != null) {
            bookId = intent.getStringExtra("book_id")
        } else {
            finish()
            Toast.makeText(
                this@Description_Activity,
                "Some unexpected error occurred!",
                Toast.LENGTH_SHORT,
            ).show()
        }

        if (bookId == "100") {
            finish()
            Toast.makeText(
                this@Description_Activity,
                "Some unexpected error occurred!",
                Toast.LENGTH_SHORT
            ).show()
        }

        val queue = Volley.newRequestQueue(this@Description_Activity)
        val url = "http://13.235.250.119/v1/book/get_book/"


        if (ConnectionManager().checkConnectivity(this@Description_Activity)) {
            val jsonParams = JSONObject()
            jsonParams.put("book_id", bookId)

            val jsonRequest =
                object : JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {


                    try {
                        val success = it.getBoolean("success")
                        if (success) {
                            val bookJsonObject = it.getJSONObject("book_data")
                            progressLayout.visibility = View.GONE
                            val bookImageUrl = bookJsonObject.getString("image")

                            Picasso.get().load(bookJsonObject.getString("image"))
                                .error(R.drawable.default_book_cover).into(imgBookImage)
                            txtBookName.text = bookJsonObject.getString("name")
                            txtBookAuthor.text = bookJsonObject.getString("author")
                            txtBookPrice.text = bookJsonObject.getString("price")
                            txtBookRating.text = bookJsonObject.getString("rating")
                            txtBookDesc.text = bookJsonObject.getString("description")

                            val bookEntity = BookEntity(
                                bookId?.toInt() as Int,
                                txtBookName.text.toString(),
                                txtBookAuthor.text.toString(),
                                txtBookAuthor.text.toString(),
                                txtBookPrice.text.toString(),
                                txtBookDesc.text.toString(),
                                bookImageUrl
                            )

                            val checkFav = DBAsyncTask(applicationContext, bookEntity, 1).execute()
                            val isFav = checkFav.get()

                            if (isFav) {
                                btnAddToFav.text = "Remove from Favourites"
                                val favColor =
                                    ContextCompat.getColor(applicationContext, R.color.colorFav)
                                btnAddToFav.setBackgroundColor(favColor)
                            } else {
                                btnAddToFav.text = "Add to Favourites"
                                val noFavColor = ContextCompat.getColor(
                                    applicationContext,
                                    com.google.android.material.R.color.m3_ref_palette_dynamic_primary50
                                )
                                btnAddToFav.setBackgroundColor(noFavColor)
                            }
                            btnAddToFav.setOnClickListener {
                                if (!DBAsyncTask(applicationContext, bookEntity, 1).execute()
                                        .get()
                                ) {
                                    val async =
                                        DBAsyncTask(applicationContext, bookEntity, 2).execute()
                                    val result = async.get()
                                    if (result) {
                                        Toast.makeText(
                                            this@Description_Activity,
                                            "Book Added To Favourites",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        btnAddToFav.text = "Remove from Favourites"
                                        val favcolor = ContextCompat.getColor(
                                            applicationContext,
                                            R.color.colorFav
                                        )
                                        btnAddToFav.setBackgroundColor(favcolor)
                                    } else {
                                        Toast.makeText(
                                            this@Description_Activity,
                                            "Some error occured",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    val async =
                                        DBAsyncTask(applicationContext, bookEntity, 3).execute()
                                    val result = async.get()

                                    if (result) {
                                        Toast.makeText(
                                            this@Description_Activity,
                                            "Book removed from favourites ",
                                            Toast.LENGTH_SHORT
                                        ).show()

                                        btnAddToFav.text = "Add to favorites"
                                        val noFavColor = ContextCompat.getColor(applicationContext,
                                            com.google.android.material.R.color.m3_ref_palette_dynamic_primary50)
                                        btnAddToFav.setBackgroundColor(noFavColor)
                                    } else{
                                        Toast.makeText(
                                            this@Description_Activity,
                                            "Some error occured",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }

                        } else {
                            Toast.makeText(
                                this@Description_Activity,
                                "Some error Occurred!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(
                            this@Description_Activity,
                            "Some error Occurred!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }, Response.ErrorListener {
                    Toast.makeText(
                        this@Description_Activity,
                        "Volley Error $it",
                        Toast.LENGTH_SHORT
                    ).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-Type"] = "application/json"
                        headers["token"] = "27af6bb10488c6"
                        return headers
                    }
                }
            queue.add(jsonRequest)
        } else {
            val Dialog = AlertDialog.Builder(this@Description_Activity)
            Dialog.setTitle("Error")
            Dialog.setMessage("Internet Connection Not Found")
            Dialog.setPositiveButton("Open Settings") { text, listner ->
                val settingsIntent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(settingsIntent)
                finish()
            }
            Dialog.setNegativeButton("Exit") { text, listner ->
                ActivityCompat.finishAffinity(this@Description_Activity)
            }
            Dialog.create()
            Dialog.show()
        }


    }

    class DBAsyncTask(val context: Context, val bookEntity: BookEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {


        val db = Room.databaseBuilder(context, Bookdatabase::class.java, "book-db").build()


        override fun doInBackground(vararg p0: Void?): Boolean {

            when (mode) {
                1 -> {
                    val book: BookEntity? = db.BookDao().getBookById(bookEntity.book_id.toString())
                    db.close()
                    return book != null

                }

                2 -> {
                    db.BookDao().insertBook((bookEntity))
                    db.close()
                    return true
                }

                3 -> {
                    db.BookDao().deleteBook(bookEntity)
                    db.close()
                    return true
                }
            }


            return false
        }

    }
}


private fun RequestCreator.into(imgBookImage: TextView?) {

}
