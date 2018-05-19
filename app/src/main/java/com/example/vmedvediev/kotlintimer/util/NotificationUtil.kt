package com.example.vmedvediev.kotlintimer.util

import android.annotation.TargetApi
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.support.v4.app.NotificationCompat
import com.example.vmedvediev.kotlintimer.AppConstants
import com.example.vmedvediev.kotlintimer.R
import com.example.vmedvediev.kotlintimer.TimerActivity
import com.example.vmedvediev.kotlintimer.TimerNotificationActionReceiver
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


object NotificationUtil {

    private const val CHANNEL_ID_TIMER = "menu_timer"
    private const val CHANNEL_NAME_TIMER = "Timer App Timer"
    private const val TIMER_ID = 0

    private fun getNotificationManager(context: Context): NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private fun getNotificationBuilder(context: Context): NotificationCompat.Builder = getBasicNotificationBuilder(context, CHANNEL_ID_TIMER, true)

    private fun getDateFormat(): DateFormat = SimpleDateFormat.getTimeInstance(SimpleDateFormat.SHORT)

    fun hideTimerNotification(context: Context) = getNotificationManager(context).cancel(TIMER_ID)

    private fun setUpPendingIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, TimerNotificationActionReceiver::class.java)
        intent.action = action
        return PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    fun showTimerExpired(context: Context){
            val notificationBuilder = getNotificationBuilder(context)
                    .setContentTitle("Timer Expired!")
                    .setContentText("Start again?")
                    .setContentIntent(getPendingIntentWithStack(context, TimerActivity::class.java))
                    .addAction(R.drawable.ic_play_arrow, "Start", setUpPendingIntent(context, AppConstants.ACTION_START))

            getNotificationManager(context).apply {
                createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)
                notify(TIMER_ID, notificationBuilder.build())
            }
        }

    fun showTimerRunning(context: Context, wakeUpTime: Long){
        val notificationBuilder =  getNotificationBuilder(context)
                .setContentTitle("Timer is Running.")
                .setContentText("End: ${getDateFormat().format(Date(wakeUpTime))}")
                .setContentIntent(getPendingIntentWithStack(context, TimerActivity::class.java))
                .setOngoing(true)
                .addAction(R.drawable.ic_stop, "Stop", setUpPendingIntent(context, AppConstants.ACTION_STOP))
                .addAction(R.drawable.ic_pause, "Pause", setUpPendingIntent(context, AppConstants.ACTION_PAUSE))

        getNotificationManager(context).apply {
            createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)
            notify(TIMER_ID, notificationBuilder.build())
        }
    }

        fun showTimerPaused(context: Context){
            val notificationBuilder = getNotificationBuilder(context)
                    .setContentTitle("Timer is paused.")
                    .setContentText("Resume?")
                    .setContentIntent(getPendingIntentWithStack(context, TimerActivity::class.java))
                    .setOngoing(true)
                    .addAction(R.drawable.ic_play_arrow, "Resume", setUpPendingIntent(context, AppConstants.ACTION_RESUME))

            getNotificationManager(context).apply {
                createNotificationChannel(CHANNEL_ID_TIMER, CHANNEL_NAME_TIMER, true)
                notify(TIMER_ID, notificationBuilder.build())
            }
        }

        private fun getBasicNotificationBuilder(context: Context, channelId: String, playSound: Boolean)
                : NotificationCompat.Builder{
            val notificationSound: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val notificationBuilder = NotificationCompat.Builder(context, channelId)
                    .setSmallIcon(R.drawable.ic_timer)
                    .setAutoCancel(true)
                    .setDefaults(0)
            if (playSound) notificationBuilder.setSound(notificationSound)
            return notificationBuilder
        }

        private fun <T> getPendingIntentWithStack(context: Context, javaClass: Class<T>): PendingIntent{
            val resultIntent = Intent(context, javaClass)
            resultIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

            val stackBuilder = TaskStackBuilder.create(context)
            stackBuilder.addParentStack(javaClass)
            stackBuilder.addNextIntent(resultIntent)

            return stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }

        @TargetApi(26)
        private fun NotificationManager.createNotificationChannel(channelID: String,
                                                                  channelName: String,
                                                                  playSound: Boolean){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                val channelImportance = if (playSound) NotificationManager.IMPORTANCE_DEFAULT
                else NotificationManager.IMPORTANCE_LOW
                val notificationChannel = NotificationChannel(channelID, channelName, channelImportance)
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.BLUE
                this.createNotificationChannel(notificationChannel)
            }
    }
}