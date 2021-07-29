package molinov.creditcalculator.app

import molinov.creditcalculator.model.Schedule

sealed class ScheduleFragmentAppState {
    data class Success(val data: List<Schedule>) : ScheduleFragmentAppState()
    object Loading : ScheduleFragmentAppState()
}
