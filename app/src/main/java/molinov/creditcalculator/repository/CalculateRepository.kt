package molinov.creditcalculator.repository

import molinov.creditcalculator.model.Calculate
import molinov.creditcalculator.model.DataFields
import molinov.creditcalculator.model.Schedule

interface CalculateRepository {

    fun getCalculateFromData(data: DataFields): Calculate

    fun getScheduleFromData(data: DataFields): List<Schedule>
}
