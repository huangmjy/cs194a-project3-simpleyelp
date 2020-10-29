package edu.stanford.huangmjy.simpleyelp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TAG = "MainActivity"
private const val BASE_URL = "https://api.yelp.com/v3/"
private const val API_KEY = "INTENTIONALLY REMOVED"
private const val DEFAULT_TERM = "Avocado Toast"
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        callSearch(DEFAULT_TERM)

        etSearchTerm.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                callSearch(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

        })
    }
        private fun callSearch(searchTerm: String) {
            val restaurants = mutableListOf<YelpRestaurant>()
            val adapter = RestaurantsAdapter(this, restaurants)
            rvRestaurants.adapter = adapter
            rvRestaurants.layoutManager = LinearLayoutManager(this)

            val retrofit =
                Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

            val yelpService = retrofit.create(YelpService::class.java)

            yelpService.searchRestaurants("Bearer $API_KEY", searchTerm,"New York").enqueue(object: Callback<YelpSearchResult> {
                override fun onFailure(call: Call<YelpSearchResult>, t: Throwable) {
                    Log.i(TAG, "onFailure $t")
                }

                override fun onResponse(call: Call<YelpSearchResult>, response: Response<YelpSearchResult>) {
                    Log.i(TAG, "onResponse $response")
                    val body = response.body()
                    if (body == null) {
                        Log.w(TAG, "Did not reeive valid response body from Yelp API... exiting")
                        return
                    }
                    restaurants.addAll(body.restaurants)
                    adapter.notifyDataSetChanged()
                }
            })
        }
}