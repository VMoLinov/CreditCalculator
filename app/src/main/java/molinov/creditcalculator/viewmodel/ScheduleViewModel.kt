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
import molinov.creditcalculator.utils.fromScheduleToEntity

class ScheduleViewModel(
    val scheduleLiveData: MutableLiveData<ScheduleAppState> = MutableLiveData(),
    val navLiveData: MutableLiveData<DataFields> = MutableLiveData(),
    private val scheduleRepository: LocalRepository = LocalRepositoryImpl(schedule_dao),
    private val calculateRepository: CalculateRepositoryImpl = CalculateRepositoryImpl()
) : ViewModel() {

    fun saveDataToDB(data: List<Schedule>) {
        val index = scheduleRepository.saveData(DataEntity(0, "name"))
        scheduleRepository.saveSchedule(fromScheduleToEntity(data, index))
    }

    fun getDataFromDB(): List<Pair<Long, List<Schedule>>>? {
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
