package ru.otus.module.one.view.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import ru.otus.module.one.R
import ru.otus.module.one.data.service.ChuckNorrisService
import ru.otus.module.one.view.details.DetailActivity
import ru.otus.module.one.view.main.ui.CategoryAdapter

class MainActivity : AppCompatActivity(), CategoryAdapter.Callback {

    private val service: ChuckNorrisService = ChuckNorrisService.instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        recycleView.layoutManager = LinearLayoutManager(this)

        // launch on an IO thread
        CoroutineScope(Dispatchers.IO).launch {
            val response = service.categories()

            // handle the response on the Main thread
            withContext(Dispatchers.Main) {
                val adapter = CategoryAdapter(response, this@MainActivity)
                recycleView.adapter = adapter
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCategorySelect(value: String) {
        DetailActivity.start(value, this)
    }
}
