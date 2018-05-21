package com.example.vmedvediev.kotlintimer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.vmedvediev.kotlintimer.AlarmManager.removeAlarm
import com.example.vmedvediev.kotlintimer.AlarmManager.setAlarm
import com.example.vmedvediev.kotlintimer.util.NotificationUtil
import com.example.vmedvediev.kotlintimer.util.PrefUtil

class TimerNotificationActionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action){
            AppConstants.ACTION_STOP -> {
                removeAlarm(context)
                PrefUtil.setTimerState(TimerActivity.TimerState.Stopped, context)
                NotificationUtil.hideTimerNotification(context)
            }
            AppConstants.ACTION_PAUSE -> {
                var secondsRemaining = PrefUtil.getSecondsRemaining(context)
                val alarmSetTime = PrefUtil.getAlarmSetTime(context)
                val nowSeconds = TimerActivity.nowSeconds

                secondsRemaining -= nowSeconds - alarmSetTime
                PrefUtil.setSecondsRemaining(secondsRemaining, context)

                removeAlarm(context)
                PrefUtil.setTimerState(TimerActivity.TimerState.Paused, context)
                NotificationUtil.showTimerPaused(context)
            }
            AppConstants.ACTION_RESUME -> {
                val secondsRemaining = PrefUtil.getSecondsRemaining(context)
                val wakeUpTime = setAlarm(context, TimerActivity.nowSeconds, secondsRemaining)
                PrefUtil.setTimerState(TimerActivity.TimerState.Running, context)
                NotificationUtil.showTimerRunning(context, wakeUpTime)
            }
            AppConstants.ACTION_START -> {
                val minutesRemaining = PrefUtil.getTimerLength(context)
                val secondsRemaining = minutesRemaining * 60L
                val wakeUpTime = setAlarm(context, TimerActivity.nowSeconds, secondsRemaining)
                PrefUtil.apply {
                    setTimerState(TimerActivity.TimerState.Running, context)
                    setSecondsRemaining(secondsRemaining, context)
                }
                NotificationUtil.showTimerRunning(context, wakeUpTime)
            }
        }
    }
}
