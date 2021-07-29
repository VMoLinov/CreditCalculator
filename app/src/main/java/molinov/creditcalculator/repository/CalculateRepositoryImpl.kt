package molinov.creditcalculator.repository

import molinov.creditcalculator.model.*

class CalculateRepositoryImpl : CalculateRepository {

    override fun getCalculateFromData(data: DataFields): Calculate = parseDataToCalculate(data)
    override fun getScheduleFromData(data: DataFields): List<Schedule> = parseDataToSchedule(data)
}