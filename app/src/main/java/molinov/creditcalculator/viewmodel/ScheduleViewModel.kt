package molinov.creditcalculator.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import molinov.creditcalculator.app.AppState

class ScheduleViewModel(
    val scheduleLiveData: MutableLiveData<AppState> = MutableLiveData()
) : ViewModel() {
}
