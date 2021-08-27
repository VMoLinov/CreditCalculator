package molinov.creditcalculator.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import molinov.creditcalculator.app.App.Companion.schedule_dao
import molinov.creditcalculator.app.ScheduleAppState
import molinov.creditcalculator.model.DataFields
import molinov.creditcalculator.model.Schedule
import molinov.creditcalculator.repository.CalculateRepositoryImpl
import molinov.creditcalculator.repository.LocalRepository
import molinov.creditcalculator.repository.LocalRepositoryImpl
import molinov.creditcalculator.utils.fromDataToEntity

class ScheduleViewModel(
    val scheduleLiveData: MutableLiveData<ScheduleAppState> = MutableLiveData(),
    val navLiveData: MutableLiveData<DataFields> = MutableLiveData(),
    private val scheduleRepository: LocalRepository = LocalRepositoryImpl(schedule_dao),
    private val calculateRepository: CalculateRepositoryImpl = CalculateRepositoryImpl()
) : ViewModel() {

    fun saveDataToDB(data: List<Schedule>, name: String, dataFields: DataFields) {
        dataFields.apply {
            scheduleRepository.insert(fromDataToEntity(dataFields, name), data)
        }
    }

    fun getSchedule(data: DataFields) {
        scheduleLiveData.value = ScheduleAppState.Loading
        Thread {
            scheduleLiveData.postValue(
                ScheduleAppState.Success(calculateRepository.getScheduleFromData(data))
            )
        }.start()
    }
}
