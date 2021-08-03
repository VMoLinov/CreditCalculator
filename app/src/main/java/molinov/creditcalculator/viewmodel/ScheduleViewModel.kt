package molinov.creditcalculator.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import molinov.creditcalculator.app.ScheduleAppState
import molinov.creditcalculator.model.DataFields
import molinov.creditcalculator.repository.CalculateRepositoryImpl

class ScheduleViewModel(
    val scheduleLiveData: MutableLiveData<ScheduleAppState> = MutableLiveData(),
    val navLiveData: MutableLiveData<DataFields> = MutableLiveData(),
    private val calculateRepository: CalculateRepositoryImpl = CalculateRepositoryImpl()
) : ViewModel() {

    fun getSchedule(data: DataFields) {
        scheduleLiveData.value = ScheduleAppState.Loading
        Thread {
            scheduleLiveData.postValue(
                ScheduleAppState.Success(calculateRepository.getScheduleFromData(data))
            )
        }.start()
    }
}
