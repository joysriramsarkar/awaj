package com.awaj.assistant.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.awaj.assistant.data.models.AppAliasEntity
import com.awaj.assistant.data.models.CommandLog
import com.awaj.assistant.data.models.RoutineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CommandLogDao {
    @Query("SELECT * FROM command_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<CommandLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: CommandLog): Long

    @Query("DELETE FROM command_logs WHERE timestamp < :cutoffTimestamp")
    suspend fun deleteLogsOlderThan(cutoffTimestamp: Long)

    @Query("DELETE FROM command_logs")
    suspend fun clearAllLogs()

    @Query("DELETE FROM command_logs WHERE id = :logId")
    suspend fun deleteLogById(logId: Long)
}

@Dao
interface AppAliasDao {
    @Query("SELECT * FROM app_aliases")
    fun getAllAliases(): Flow<List<AppAliasEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlias(alias: AppAliasEntity)

    @Query("DELETE FROM app_aliases WHERE id = :id")
    suspend fun deleteAlias(id: Long)
}

@Dao
interface RoutineDao {
    @Query("SELECT * FROM saved_routines")
    fun getAllRoutines(): Flow<List<RoutineEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutine(routine: RoutineEntity)

    @Query("DELETE FROM saved_routines WHERE id = :id")
    suspend fun deleteRoutine(id: Long)
}
