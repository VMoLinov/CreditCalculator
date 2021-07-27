package molinov.creditcalculator.view.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import molinov.creditcalculator.R
import molinov.creditcalculator.model.Schedule

class ScheduleAdapter : RecyclerView.Adapter<ScheduleAdapter.RecyclerItemViewHolder>() {

    private var data: List<Schedule> = arrayListOf(Schedule(1, 10000), Schedule(2, 20000))

    fun setData(data: List<Schedule>) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerItemViewHolder {
        return RecyclerItemViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.schedule_fragment_recycler_item, parent, false) as View
        )
    }

    override fun onBindViewHolder(holder: RecyclerItemViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class RecyclerItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(data: Schedule) {
            if (layoutPosition != RecyclerView.NO_POSITION) {
                itemView.findViewById<TextView>(R.id.scheduleRecyclerViewItem).text =
                    String.format("%s %s", data.pos, data.amount)
                itemView.setOnClickListener {
                    Toast.makeText(
                        itemView.context, "on click", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}