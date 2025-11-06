package com.lyrisync.util

import android.util.Log
import com.lyrisync.BuildConfig

/**
 * A simple logging utility that only logs messages when the app is in a debug build.
 * This prevents log spam in release versions of the application.
 */
object Logger {

    /**
     * Logs an error message. Use this for critical failures, exceptions,
     * and other unexpected errors that need immediate attention.
     * Corresponds to Log.e().
     *
     * @param tag Used to identify the source of a log message. It usually identifies
     *            the class or activity where the log call occurs.
     * @param message The message you would like logged.
     */
    fun logError(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.e(tag, message)
        }
    }

    /**
     * Logs a warning message. Use this for potential issues that are not yet errors,
     * or for situations that are unexpected but recoverable.
     * Corresponds to Log.w().
     *
     * @param tag Used to identify the source of a log message.
     * @param message The message you would like logged.
     */
    fun logWarning(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.w(tag, message)
        }
    }

    /**
     * Logs an informational message. Use this to log interesting application
     * lifecycle events or major state changes.
     * Corresponds to Log.i().
     *
     * @param tag Used to identify the source of a log message.
     * @param message The message you would like logged.
     */
    fun logInfo(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.i(tag, message)
        }
    }

    /**
     * Logs a debug message. Use this for temporary logs to understand
     * the flow of code during development. These are typically the first
     * to be removed when a feature is complete.
     * Corresponds to Log.d().
     *
     * @param tag Used to identify the source of a log message.
     * @param message The message you would like logged.
     */
    fun logDebug(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d(tag, message)
        }
    }

    /**
     * Logs a verbose message. Use this for the most detailed and granular
     * information that you would not want to see by default.
     * Corresponds to Log.v().
     *
     * @param tag Used to identify the source of a log message.
     * @param message The message you would like logged.
     */
    fun logVerbose(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.v(tag, message)
        }
    }
}