package com.example.vmedvediev.kotlintimer

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.vmedvediev.kotlintimer.util.NotificationUtil
import com.example.vmedvediev.kotlintimer.util.PrefUtil

class TimerExpiredReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        NotificationUtil.showTimerExpired(context)

        PrefUtil.apply {
            setTimerState(TimerActivity.TimerState.Stopped, context)
            setAlarmSetTime(0, context)
        }
    }
}
