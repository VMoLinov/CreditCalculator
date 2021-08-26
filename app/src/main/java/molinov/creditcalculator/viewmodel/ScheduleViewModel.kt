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
import molinov.creditcalculator.room.DataEntity
import molinov.creditcalculator.utils.fromScheduleDataToPair

class ScheduleViewModel(
    val scheduleLiveData: MutableLiveData<ScheduleAppState> = MutableLiveData(),
    val navLiveData: MutableLiveData<DataFields> = MutableLiveData(),
    private val scheduleRepository: LocalRepository = LocalRepositoryImpl(schedule_dao),
    private val calculateRepository: CalculateRepositoryImpl = CalculateRepositoryImpl()
) : ViewModel() {

    fun saveDataToDB(data: List<Schedule>, name: String) {
        scheduleRepository.insert(DataEntity(0, name), data)
    }

    fun getDataFromDB(): List<Pair<DataEntity, List<Schedule>>>? {
        return fromScheduleDataToPair(scheduleRepository.getAllData())
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
