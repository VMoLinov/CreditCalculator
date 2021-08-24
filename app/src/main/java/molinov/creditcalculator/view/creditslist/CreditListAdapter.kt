package molinov.creditcalculator.view.creditslist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import molinov.creditcalculator.R
import molinov.creditcalculator.app.CreditListAppState
import molinov.creditcalculator.app.ScheduleAppState
import molinov.creditcalculator.model.Schedule
import molinov.creditcalculator.model.paymentFromSchedule
import molinov.creditcalculator.room.DataEntity
import molinov.creditcalculator.view.schedule.ScheduleAdapter

class CreditListAdapter : RecyclerView.Adapter<CreditListAdapter.ViewHolder>(),
    ItemTouchHelperAdapter {

    var data: MutableList<Pair<DataEntity, List<Schedule>>> = mutableListOf()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(appState: CreditListAppState) {
        if (appState is CreditListAppState.Success) {
            this.data = appState.data
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
        holder.bind(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        ItemTouchHelperViewHolder {
        private val childRecyclerView: RecyclerView
        private val childAdapter: ScheduleAdapter

        init {
            val context = itemView.context
            childRecyclerView = itemView.findViewById(R.id.schedule_recycler_view_item)
            childAdapter = ScheduleAdapter(context)
            childRecyclerView.adapter = childAdapter
        }

        @SuppressLint("ClickableViewAccessibility")
        fun bind(data: Pair<DataEntity, List<Schedule>>) {
            if (layoutPosition != RecyclerView.NO_POSITION) {
                itemView.findViewById<LinearLayoutCompat>(R.id.description).isVisible =
                    data.first.isExpanded
                itemView.findViewById<AppCompatTextView>(R.id.name).text = data.first.name
                itemView.findViewById<AppCompatTextView>(R.id.body).text = data.first.id.toString()
                itemView.findViewById<AppCompatTextView>(R.id.payment).text =
                    paymentFromSchedule(data.second)
                childAdapter.setData(ScheduleAppState.Success(data.second))
                itemView.findViewById<LinearLayoutCompat>(R.id.main_card_view).setOnClickListener {
                    handleSchedulesVisibility()
                }
            }
        }

        private fun handleSchedulesVisibility() {
            data[layoutPosition].first.isExpanded = !data[layoutPosition].first.isExpanded
            notifyItemChanged(layoutPosition)
            for (i in data.indices) {
                if (data[i].first.isExpanded && i != layoutPosition) {
                    data[i].first.isExpanded = false
                    notifyItemChanged(i)
                }
            }
        }

        override fun onItemSelected() {
            itemView.findViewById<MaterialCardView>(R.id.card).cardElevation = 10f
        }

        override fun onItemClear() {
            itemView.findViewById<MaterialCardView>(R.id.card).cardElevation =
                itemView.context.resources.getDimension(R.dimen.card_default_elevation)
        }
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int) {
        data.removeAt(fromPosition).apply {
            data.add(toPosition, this)
        }
        notifyItemMoved(fromPosition, toPosition)
    }

    override fun onItemSwiped(position: Int, direction: Int) {
        when (direction) {
            ItemTouchHelper.START -> {
                data.removeAt(position)
                notifyItemRemoved(position)
            }
            ItemTouchHelper.END -> {
            }
        }
    }
}
