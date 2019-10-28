package ru.otus.module.one.view.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.TransitionOptions
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.otus.module.one.R
import ru.otus.module.one.data.service.ChuckNorrisService

class DetailActivity : AppCompatActivity() {

    private val service: ChuckNorrisService = ChuckNorrisService.instance

    companion object {
        const val CATEGORY_EXTRA = "category_extra"

        fun start(category: String, context: Context) {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(CATEGORY_EXTRA, category)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        intent.getStringExtra(CATEGORY_EXTRA)?.let {
            title = "The joke from: $it"
            getJoke(it)
            return
        }
    }

    private fun getJoke(category: String) {
        // launch on an IO thread
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.random(category)

            // handle the response on the Main thread
            withContext(Dispatchers.Main) {
                println(response)
                textView.text = response.value
                Glide
                    .with(this@DetailActivity)
                    .load(response.iconUrl)
                    .into(imageView)
            }
        }
    }
}
