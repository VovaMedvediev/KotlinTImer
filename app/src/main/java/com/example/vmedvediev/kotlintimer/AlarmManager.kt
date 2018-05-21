package com.example.vmedvediev.kotlintimer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.example.vmedvediev.kotlintimer.util.PrefUtil

object AlarmManager {

    fun setAlarm(context: Context, nowSeconds: Long, secondsRemaining: Long): Long {
        val wakeUpTime = (nowSeconds + secondsRemaining) * 1000
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, TimerExpiredReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, wakeUpTime, pendingIntent)
        PrefUtil.setAlarmSetTime(nowSeconds, context)
        return wakeUpTime
    }

    fun removeAlarm(context: Context) {
        val intent = Intent(context, TimerExpiredReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0)
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
        PrefUtil.setAlarmSetTime(0, context)
    }
}