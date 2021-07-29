package molinov.creditcalculator.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import molinov.creditcalculator.app.MainFragmentAppState
import molinov.creditcalculator.app.ScheduleFragmentAppState

class ScheduleViewModel(
    val scheduleLiveData: MutableLiveData<ScheduleFragmentAppState> = MutableLiveData()
) : ViewModel() {
}
