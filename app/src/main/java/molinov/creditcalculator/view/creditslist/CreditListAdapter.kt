package molinov.creditcalculator.view.creditslist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import molinov.creditcalculator.R
import molinov.creditcalculator.app.CreditListAppState
import molinov.creditcalculator.app.ScheduleAppState
import molinov.creditcalculator.model.Schedule
import molinov.creditcalculator.room.DataEntity
import molinov.creditcalculator.view.schedule.ScheduleAdapter

class CreditListAdapter(
    private var onListItemClickListener: OnListItemClickListener
) : RecyclerView.Adapter<CreditListAdapter.ViewHolder>() {

    var data: List<Pair<DataEntity, List<Schedule>>> = mutableListOf()
    var isScheduleVisible = mutableListOf<Boolean>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(appState: CreditListAppState) {
        if (appState is CreditListAppState.Success) {
            this.data = appState.data
            for (i in data.indices) isScheduleVisible.add(false)
            notifyDataSetChanged()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.credit_list_fragment_recycle_item, parent, false
            ) as View
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(data[position], isScheduleVisible[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val childRecyclerView: RecyclerView
        private val childAdapter: ScheduleAdapter

        init {
            val context = itemView.context
            childRecyclerView = itemView.findViewById(R.id.schedule_recycler_view_item)
            childAdapter = ScheduleAdapter(context)
            childRecyclerView.adapter = childAdapter
        }

        fun bind(data: Pair<DataEntity, List<Schedule>>, isVisible: Boolean) {
            if (layoutPosition != RecyclerView.NO_POSITION) {
                itemView.findViewById<LinearLayoutCompat>(R.id.description).isVisible = isVisible
                itemView.findViewById<AppCompatTextView>(R.id.name).text = data.first.name
                itemView.findViewById<AppCompatTextView>(R.id.body).text = data.first.id.toString()
                childAdapter.setData(ScheduleAppState.Success(data.second))
                itemView.findViewById<LinearLayoutCompat>(R.id.main_card_view).setOnClickListener {
                    handleSchedulesVisibility()
                }
            }
        }

        private fun handleSchedulesVisibility() {
            isScheduleVisible[layoutPosition] = !isScheduleVisible[layoutPosition]
            notifyItemChanged(layoutPosition)
            for (i in isScheduleVisible.indices) {
                if (isScheduleVisible[i] && i != layoutPosition) {
                    isScheduleVisible[i] = false
                    notifyItemChanged(i)
                }
            }
        }
    }
}
