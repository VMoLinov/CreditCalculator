package molinov.creditcalculator.repository

import molinov.creditcalculator.model.*

class CalculateRepositoryImpl : CalculateRepository {

    override fun getCalculateFromData(data: DataFields): Calculate =
        parseDataFieldsToCalculate(data)

    override fun getScheduleFromData(data: DataFields): List<Schedule> =
        parseDataFieldsToSchedule(data)
}
