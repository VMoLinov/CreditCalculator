package molinov.creditcalculator.view.schedule

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import molinov.creditcalculator.R
import molinov.creditcalculator.app.ScheduleAppState
import molinov.creditcalculator.model.Schedule

class ScheduleAdapter : RecyclerView.Adapter<ScheduleAdapter.RecyclerItemViewHolder>() {

    private var data: List<Schedule> = mutableListOf()

    fun setData(scheduleAppState: ScheduleAppState) {
        if (scheduleAppState is ScheduleAppState.Success) {
            this.data = scheduleAppState.data
            notifyDataSetChanged()
        }
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

    inner class RecyclerItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        fun bind(data: Schedule) {
            if (layoutPosition != RecyclerView.NO_POSITION) {
                itemView.findViewById<AppCompatTextView>(R.id.number).text =
                    (layoutPosition + 1).toString()
                itemView.findViewById<AppCompatTextView>(R.id.date).text = data.date
                itemView.findViewById<AppCompatTextView>(R.id.payment).text = data.payment
                itemView.findViewById<AppCompatTextView>(R.id.balance).text = data.balance
                itemView.setOnClickListener {
                    Toast.makeText(
                        itemView.context, "on click", Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
