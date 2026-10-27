package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.model.CheckInRecord
import com.example.model.SupportRequestRecord
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Database(
    entities = [CheckInRecord::class, SupportRequestRecord::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun checkInDao(): CheckInDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "campuswell_database"
                )
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Populate default 7-day sample data reflecting the mockup
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    populateInitialData(database.checkInDao())
                                }
                            }
                        }
                    })
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        suspend fun populateInitialData(dao: CheckInDao) {
            val sampleRecords = listOf(
                CheckInRecord(
                    id = UUID.randomUUID().toString(),
                    date = "2025-04-22",
                    dayOfWeek = "Tue",
                    mood = "Good",
                    sleepHours = 7.0f,
                    waterGlasses = 6,
                    studyHours = 5.2f,
                    stressLevel = 2,
                    timestamp = System.currentTimeMillis() - 6 * 86400000L
                ),
                CheckInRecord(
                    id = UUID.randomUUID().toString(),
                    date = "2025-04-23",
                    dayOfWeek = "Wed",
                    mood = "Okay",
                    sleepHours = 8.2f,
                    waterGlasses = 7,
                    studyHours = 3.5f,
                    stressLevel = 3,
                    timestamp = System.currentTimeMillis() - 5 * 86400000L
                ),
                CheckInRecord(
                    id = UUID.randomUUID().toString(),
                    date = "2025-04-24",
                    dayOfWeek = "Thu",
                    mood = "Great",
                    sleepHours = 6.0f,
                    waterGlasses = 8,
                    studyHours = 6.0f,
                    stressLevel = 2,
                    timestamp = System.currentTimeMillis() - 4 * 86400000L
                ),
                CheckInRecord(
                    id = UUID.randomUUID().toString(),
                    date = "2025-04-25",
                    dayOfWeek = "Fri",
                    mood = "Good",
                    sleepHours = 6.8f,
                    waterGlasses = 6,
                    studyHours = 4.8f,
                    stressLevel = 2,
                    timestamp = System.currentTimeMillis() - 3 * 86400000L
                ),
                CheckInRecord(
                    id = UUID.randomUUID().toString(),
                    date = "2025-04-26",
                    dayOfWeek = "Sat",
                    mood = "Great",
                    sleepHours = 9.0f,
                    waterGlasses = 5,
                    studyHours = 2.5f,
                    stressLevel = 1,
                    timestamp = System.currentTimeMillis() - 2 * 86400000L
                ),
                CheckInRecord(
                    id = UUID.randomUUID().toString(),
                    date = "2025-04-27",
                    dayOfWeek = "Sun",
                    mood = "Good",
                    sleepHours = 7.5f,
                    waterGlasses = 7,
                    studyHours = 1.5f,
                    stressLevel = 2,
                    timestamp = System.currentTimeMillis() - 1 * 86400000L
                ),
                CheckInRecord(
                    id = "today_record",
                    date = "2025-04-28",
                    dayOfWeek = "Mon",
                    mood = "Good",
                    sleepHours = 7.5f,
                    waterGlasses = 6,
                    studyHours = 4.5f,
                    stressLevel = 2,
                    timestamp = System.currentTimeMillis()
                )
            )
            for (item in sampleRecords) {
                dao.insertCheckIn(item)
            }
        }
    }
}
