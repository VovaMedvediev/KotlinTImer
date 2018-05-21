package com.example.vmedvediev.kotlintimer.util

import android.content.Context
import android.preference.PreferenceManager
import com.example.vmedvediev.kotlintimer.TimerActivity


object PrefUtil {

        private const val TIMER_LENGTH_ID = "com.example.timer.timer_length"
        private const val PREVIOUS_TIMER_LENGTH_SECONDS_ID = "com.example.timer.previous_timer_length_seconds"
        private const val TIMER_STATE_ID = "com.example.timer.timer_state"
        private const val SECONDS_REMAINING_ID = "com.example.timer.seconds_remaining"
        private const val ALARM_SET_TIME_ID = "com.example.timer.backgrounded_time"

        private fun getPreferences(context: Context) = PreferenceManager.getDefaultSharedPreferences(context)

        private fun getPreferencesEditor(context: Context) = PreferenceManager.getDefaultSharedPreferences(context).edit()

        fun getTimerLength(context: Context): Int = getPreferences(context).getInt(TIMER_LENGTH_ID, 1)

        fun getPreviousTimerLengthSeconds(context: Context): Long = getPreferences(context).getLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, 0)

        fun getAlarmSetTime(context: Context): Long = getPreferences(context).getLong(ALARM_SET_TIME_ID, 0)

        fun getSecondsRemaining(context: Context): Long = getPreferences(context).getLong(SECONDS_REMAINING_ID, 0)

        fun getTimerState(context: Context): TimerActivity.TimerState {
            val ordinal = getPreferences(context).getInt(TIMER_STATE_ID, 0)
            return TimerActivity.TimerState.values()[ordinal]
        }

        fun setPreviousTimerLengthSeconds(context: Context, seconds: Long) {
            getPreferencesEditor(context).apply {
                putLong(PREVIOUS_TIMER_LENGTH_SECONDS_ID, seconds)
                apply()
            }
        }

        fun setTimerState(context: Context, state: TimerActivity.TimerState) {
            getPreferencesEditor(context).apply {
                putInt(TIMER_STATE_ID, state.ordinal)
                apply()
            }
        }

        fun setSecondsRemaining(context: Context, seconds: Long) {
            getPreferencesEditor(context).apply {
                putLong(SECONDS_REMAINING_ID, seconds)
                apply()
            }
        }

        fun setAlarmSetTime(context: Context, time: Long) {
            getPreferencesEditor(context).apply {
                putLong(ALARM_SET_TIME_ID, time)
                apply()
            }
        }
}