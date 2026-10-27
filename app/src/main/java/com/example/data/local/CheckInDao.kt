package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.model.CheckInRecord
import com.example.model.SupportRequestRecord
import kotlinx.coroutines.flow.Flow

@Dao
interface CheckInDao {
    @Query("SELECT * FROM check_ins ORDER BY timestamp DESC")
    fun getAllCheckIns(): Flow<List<CheckInRecord>>

    @Query("SELECT * FROM check_ins WHERE date = :date LIMIT 1")
    suspend fun getCheckInByDate(date: String): CheckInRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCheckIn(checkIn: CheckInRecord)

    @Update
    suspend fun updateCheckIn(checkIn: CheckInRecord)

    @Query("SELECT * FROM check_ins WHERE isSynced = 0")
    suspend fun getUnsyncedCheckIns(): List<CheckInRecord>

    @Query("UPDATE check_ins SET isSynced = 1 WHERE id = :id")
    suspend fun markCheckInSynced(id: String)

    // Support Requests
    @Query("SELECT * FROM support_requests ORDER BY timestamp DESC")
    fun getAllSupportRequests(): Flow<List<SupportRequestRecord>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupportRequest(request: SupportRequestRecord)

    @Query("SELECT * FROM support_requests WHERE isSynced = 0")
    suspend fun getUnsyncedSupportRequests(): List<SupportRequestRecord>

    @Query("UPDATE support_requests SET isSynced = 1 WHERE id = :id")
    suspend fun markSupportRequestSynced(id: String)
}
