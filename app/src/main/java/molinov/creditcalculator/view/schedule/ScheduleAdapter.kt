package molinov.creditcalculator.view.schedule

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import molinov.creditcalculator.R
import molinov.creditcalculator.app.ScheduleAppState
import molinov.creditcalculator.model.Schedule
import molinov.creditcalculator.model.getFormattedNumber

class ScheduleAdapter(private var mContext: Context) :
    RecyclerView.Adapter<ScheduleAdapter.BaseViewHolder>() {

    var data: List<Schedule> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(scheduleAppState: ScheduleAppState) {
        if (scheduleAppState is ScheduleAppState.Success) {
            this.data = scheduleAppState.data
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when (viewType) {
            TYPE_MAIN -> {
                MainViewHolder(
                    LayoutInflater.from(mContext).inflate(
                        R.layout.schedule_fragment_recycler_main_item, parent, false
                    ) as View
                )
            }
            else -> {
                TotalViewHolder(
                    LayoutInflater.from(mContext).inflate(
                        R.layout.schedule_fragment_recycler_total_item, parent, false
                    ) as View
                )
            }
        }
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.bind(data[position])
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].type
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class MainViewHolder(view: View) : BaseViewHolder(view) {
        override fun bind(data: Schedule) {
            if (layoutPosition != RecyclerView.NO_POSITION) {
                itemView.findViewById<AppCompatTextView>(R.id.number).text =
                    (layoutPosition + 1).toString()
                itemView.findViewById<AppCompatTextView>(R.id.date).text = data.date
                itemView.findViewById<AppCompatTextView>(R.id.mainDebt).text =
                    getFormattedNumber(data.mainDebt)
                itemView.findViewById<AppCompatTextView>(R.id.percent).text =
                    getFormattedNumber(data.percent)
                itemView.findViewById<AppCompatTextView>(R.id.balance).text =
                    getFormattedNumber(data.balance)
            }
        }
    }

    inner class TotalViewHolder(view: View) : BaseViewHolder(view) {
        override fun bind(data: Schedule) {
            if (layoutPosition != RecyclerView.NO_POSITION) {
                itemView.findViewById<AppCompatTextView>(R.id.mainDebt).text =
                    getFormattedNumber(data.mainDebt)
                itemView.findViewById<AppCompatTextView>(R.id.percent).text =
                    getFormattedNumber(data.percent)
            }
        }
    }

    abstract inner class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        open fun bind(data: Schedule) {}
    }

    companion object {
        const val TYPE_MAIN = 1
        const val TYPE_TOTAL = 2
    }
}
