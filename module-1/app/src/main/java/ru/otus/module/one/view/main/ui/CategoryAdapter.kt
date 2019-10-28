package ru.otus.module.one.view.main.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.category_item.view.*
import ru.otus.module.one.R

class CategoryAdapter(
    private val items: List<String>?,
    private val callback: Callback?
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        items?.get(position)?.let {
            holder.onBind(it, callback)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        return CategoryViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.category_item,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items?.size ?: 0
    }


    public interface Callback {
        fun onCategorySelect(value: String)
    }


    class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

        private var category: String = ""
        private var callback: Callback? = null

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            callback?.onCategorySelect(category)
        }

        fun onBind(category: String, callback: Callback?) {
            this.callback = callback
            this.category = category
            with(itemView) {
                textView.text = category
            }
        }
    }
}