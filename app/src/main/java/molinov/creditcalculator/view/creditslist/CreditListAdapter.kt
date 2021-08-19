package molinov.creditcalculator.view.creditslist

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import molinov.creditcalculator.R
import molinov.creditcalculator.app.CreditListAppState
import molinov.creditcalculator.model.Schedule
import molinov.creditcalculator.room.DataEntity
import molinov.creditcalculator.view.schedule.ScheduleAdapter

class CreditListAdapter : RecyclerView.Adapter<CreditListAdapter.ViewHolder>() {

    var data: List<Pair<DataEntity, List<Schedule>>> = mutableListOf()

    fun setData(appState: CreditListAppState) {
        if (appState is CreditListAppState.Success && appState.data != null) {
            this.data = appState.data
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val childRecyclerView: RecyclerView
        val childAdapter: ScheduleAdapter

        init {
            val context = itemView.context
            childRecyclerView = itemView.findViewById(R.id.scheduleView)
            childAdapter = ScheduleAdapter(context)
            childRecyclerView.adapter = childAdapter
        }
    }
}
