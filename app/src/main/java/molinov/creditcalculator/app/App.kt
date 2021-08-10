package molinov.creditcalculator.app

import android.app.Application
import androidx.room.Room
import molinov.creditcalculator.room.ScheduleDataBase

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        appInstance = this
    }

    companion object {

        private lateinit var appInstance: App
        private const val DB_NAME = "DataEntity.db"
        val schedule_dao by lazy {
            Room.databaseBuilder(
                appInstance.applicationContext,
                ScheduleDataBase::class.java,
                DB_NAME
            ).allowMainThreadQueries().build().scheduleDao()
        }
    }
}
