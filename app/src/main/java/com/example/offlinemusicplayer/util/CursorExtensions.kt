package com.example.offlinemusicplayer.util

import android.database.Cursor

internal fun Cursor.getIntFromCol(columnName: String): Int {
    try {
        return getInt(getColumnIndexOrThrow(columnName))
    } catch (ex: Throwable) {
        throw IllegalStateException("invalid column $columnName", ex)
    }
}

internal fun Cursor.getLongFromCol(columnName: String): Long {
    try {
        return getLong(getColumnIndexOrThrow(columnName))
    } catch (ex: Throwable) {
        throw IllegalStateException("invalid column $columnName", ex)
    }
}

internal fun Cursor.getStringFromCol(columnName: String): String {
    try {
        return getString(getColumnIndexOrThrow(columnName))
    } catch (ex: Throwable) {
        throw IllegalStateException("invalid column $columnName", ex)
    }
}

internal fun Cursor.getStringOrNullFromCol(columnName: String): String? {
    try {
        return getString(getColumnIndexOrThrow(columnName))
    } catch (ex: Throwable) {
        throw IllegalStateException("invalid column $columnName", ex)
    }
}