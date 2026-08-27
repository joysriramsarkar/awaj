package com.awaj.assistant.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "command_logs")
data class CommandLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rawText: String,
    val parsedAction: String,
    val riskLevel: String,
    val isConfirmed: Boolean,
    val isSuccess: Boolean,
    val resultSummary: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "app_aliases")
data class AppAliasEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val aliasName: String,
    val targetPackage: String,
    val displayName: String,
    val isCustom: Boolean = true
)

@Entity(tableName = "saved_routines")
data class RoutineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nameBangla: String,
    val triggerPhrase: String,
    val actionsJson: String,
    val isEnabled: Boolean = true
)
