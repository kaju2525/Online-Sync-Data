package karun.com.googlemapdemo

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import karun.com.online_sync.Model
import karun.com.online_sync.R
import kotlinx.android.synthetic.main.names.view.*

class MyAdapter(private val list: ArrayList<Model>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): MyAdapter.ViewHolder {
        val view = LayoutInflater.from(parent!!.context).inflate(R.layout.names, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }


    override fun onBindViewHolder(holder: MyAdapter.ViewHolder?, position: Int) {

        holder!!.bindItem(list[position])

        if (position % 2 == 0) {
            holder.itemView.setBackgroundColor(Color.GRAY)
        } else
            holder.itemView.setBackgroundColor(Color.LTGRAY)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bindItem(model: Model) {
            itemView.textViewName.text = model.name
            if (model.status == 0)
                itemView.imageViewStatus.setBackgroundResource(R.drawable.stopwatch)
            else
                itemView.imageViewStatus.setBackgroundResource(R.drawable.success)


        }
    }
}

