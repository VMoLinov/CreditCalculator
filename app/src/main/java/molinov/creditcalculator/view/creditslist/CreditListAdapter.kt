package molinov.creditcalculator.view.creditslist

import android.annotation.SuppressLint
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.BackgroundColorSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import molinov.creditcalculator.R
import molinov.creditcalculator.app.CreditListAppState
import molinov.creditcalculator.app.ScheduleAppState
import molinov.creditcalculator.model.Schedule
import molinov.creditcalculator.model.getFormattedNumber
import molinov.creditcalculator.model.paymentFromSchedule
import molinov.creditcalculator.room.DataFieldsEntity
import molinov.creditcalculator.utils.formattedYears
import molinov.creditcalculator.utils.fromEntityToDataFields
import molinov.creditcalculator.view.schedule.ScheduleAdapter
import molinov.creditcalculator.viewmodel.CreditListViewModel

class CreditListAdapter(
    private val viewModel: CreditListViewModel
) : RecyclerView.Adapter<CreditListAdapter.ViewHolder>(),
    ItemTouchHelperAdapter {

    var data: MutableList<Pair<DataFieldsEntity, List<Schedule>>> = mutableListOf()

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
        fun bind(data: Pair<DataFieldsEntity, List<Schedule>>) {
            if (layoutPosition != RecyclerView.NO_POSITION) {
                itemView.findViewById<LinearLayoutCompat>(R.id.description).isVisible =
                    data.first.isExpanded
                itemView.findViewById<AppCompatTextView>(R.id.name).text = data.first.name
                itemView.findViewById<AppCompatTextView>(R.id.body).text = bodyText(data)
                itemView.findViewById<AppCompatTextView>(R.id.overPayment).text =
                    getFormattedNumber(data.second[data.second.lastIndex].percent)
                itemView.findViewById<AppCompatTextView>(R.id.payment).text =
                    paymentFromSchedule(data.second)
                childAdapter.setData(ScheduleAppState.Success(data.second))
                itemView.findViewById<LinearLayoutCompat>(R.id.main_card_view).setOnClickListener {
                    handleSchedulesVisibility()
                }
            }
        }

        private fun bodyText(data: Pair<DataFieldsEntity, List<Schedule>>): SpannableStringBuilder {
            val s = SpannableStringBuilder()
            val dataFirst = fromEntityToDataFields(data.first)
            s.append(getFormattedNumber(dataFirst.amount.toString()))
            s.setSpan(
                ForegroundColorSpan(
                    itemView.resources.getColor(R.color.recycle_red_light, itemView.context.theme)
                ),
                s.length - getFormattedNumber(dataFirst.amount.toString()).length,
                s.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            s.append(" " + itemView.resources.getString(R.string.on) + " ")
            s.append(dataFirst.loanTerm.toString() + " ")
            s.append(
                if (dataFirst.isMonths) itemView.resources.getString(R.string.months)
                else formattedYears(dataFirst.loanTerm, itemView)
            )
            s.append(" " + itemView.resources.getString(R.string.under) + " ")
            s.append(dataFirst.rate.toString() + itemView.resources.getString(R.string.percent_sign))
            s.setSpan(
                BackgroundColorSpan(
                    itemView.resources.getColor(
                        android.R.color.holo_blue_light,
                        itemView.context.theme
                    )
                ),
                s.length - dataFirst.rate.toString().length - 1,
                s.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            return s
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
        viewModel.update(data[fromPosition], data[toPosition])
    }

    override fun onItemSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition
        when (direction) {
            ItemTouchHelper.START -> {
                viewModel.delete(data[position].first.id)
                data.removeAt(position)
                notifyItemRemoved(position)
            }
            ItemTouchHelper.END -> {
                val action = CreditListFragmentDirections.actionCreditListToMain(
                    fromEntityToDataFields(data[position].first)
                )
                Navigation.findNavController(viewHolder.itemView)
                    .navigate(action)
            }
        }
    }
}
