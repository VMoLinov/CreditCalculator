package molinov.creditcalculator.app

import molinov.creditcalculator.model.Schedule

sealed class ScheduleAppState {
    data class Success(val data: List<Schedule>) : ScheduleAppState()
    object Loading : ScheduleAppState()
}
