package com.example.vmedvediev.kotlintimer

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import com.example.vmedvediev.kotlintimer.AlarmManager.removeAlarm
import com.example.vmedvediev.kotlintimer.AlarmManager.setAlarm
import com.example.vmedvediev.kotlintimer.util.NotificationUtil
import com.example.vmedvediev.kotlintimer.util.PrefUtil
import kotlinx.android.synthetic.main.activity_timer.*
import kotlinx.android.synthetic.main.content_timer.*
import java.util.*

class TimerActivity : AppCompatActivity() {

    companion object {
        const val MILLS_IN_ONE_SECOND = 1000
        const val SECONDS_IN_ONE_MINUTE = 60
        val nowSeconds: Long
            get() = Calendar.getInstance().timeInMillis / MILLS_IN_ONE_SECOND
    }

    private lateinit var timer: CountDownTimer
    private var timerLengthSeconds: Long = 0
    private var timerState = TimerState.Stopped
    private var secondsRemaining: Long = 0

    enum class TimerState {
        Stopped, Paused, Running
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timer)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setIcon(R.drawable.ic_timer)
            title = "      Timer"
        }

        startFloatingActionButton.setOnClickListener {

            startTimer()
            timerState =  TimerState.Running
            updateButtons()
        }

        pauseFloatingActionButton.setOnClickListener {
            timer.cancel()
            timerState = TimerState.Paused
            updateButtons()
        }

        stopFloatingActionButton.setOnClickListener {
            timer.cancel()
            onTimerFinished()
        }
    }

    override fun onResume() {
        super.onResume()

        initTimer()

        removeAlarm(this)
        NotificationUtil.hideTimerNotification(this)
    }

    override fun onPause() {
        super.onPause()

        if (timerState == TimerState.Running) {
            timer.cancel()
            val wakeUpTime = setAlarm(this, nowSeconds, secondsRemaining)
            NotificationUtil.showTimerRunning(this, wakeUpTime)
        } else if (timerState == TimerState.Paused) {
            NotificationUtil.showTimerPaused(this)
        }

        PrefUtil.apply {
            setPreviousTimerLengthSeconds(applicationContext, timerLengthSeconds)
            setSecondsRemaining(applicationContext, secondsRemaining)
            setTimerState(applicationContext, timerState)
        }
    }

    private fun initTimer() {
        timerState = PrefUtil.getTimerState(this)

        if (timerState == TimerState.Stopped) {
            setNewTimerLength()
        } else {
            setPreviousTimerLength()
        }
        secondsRemaining = if (timerState == TimerState.Running || timerState == TimerState.Paused)
            PrefUtil.getSecondsRemaining(this)
        else
            timerLengthSeconds

        val alarmSetTime = PrefUtil.getAlarmSetTime(this)
        if (alarmSetTime > 0)
            secondsRemaining -= nowSeconds - alarmSetTime

        if (secondsRemaining <= 0)
            onTimerFinished()
        else if (timerState == TimerState.Running)
            startTimer()

        updateButtons()
        updateCountdownUI()
    }

    private fun onTimerFinished() {
        timerState = TimerState.Stopped
        setNewTimerLength()

        countdownProgressBar.progress = 0

        PrefUtil.setSecondsRemaining(this, timerLengthSeconds)
        secondsRemaining = timerLengthSeconds

        updateButtons()
        updateCountdownUI()
    }

    private fun startTimer() {
        val countDownInterval = 1000L
        timerState = TimerState.Running

        timer = object : CountDownTimer(secondsRemaining * MILLS_IN_ONE_SECOND, countDownInterval) {

            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / MILLS_IN_ONE_SECOND
                updateCountdownUI()
            }
        }.start()
    }

    private fun setNewTimerLength() {
        val lengthInMinutes = PrefUtil.getTimerLength(this)
        timerLengthSeconds = ((lengthInMinutes * SECONDS_IN_ONE_MINUTE).toLong())
        countdownProgressBar.max = timerLengthSeconds.toInt()
    }

    private fun setPreviousTimerLength() {
        timerLengthSeconds = PrefUtil.getPreviousTimerLengthSeconds(this)
        countdownProgressBar.max = timerLengthSeconds.toInt()
    }

    private fun updateCountdownUI() {
        val minutesUntilFinished = secondsRemaining / SECONDS_IN_ONE_MINUTE
        val secondsInMinuteUntilFinished = secondsRemaining - minutesUntilFinished * SECONDS_IN_ONE_MINUTE
        val secondsStr = secondsInMinuteUntilFinished.toString()
        countdownTextView.text = "$minutesUntilFinished:${if (secondsStr.length == 2) secondsStr else "0" + secondsStr}"
        countdownProgressBar.progress = (timerLengthSeconds - secondsRemaining).toInt()
    }

    private fun updateButtons() {
        when (timerState) {
            TimerState.Running -> {
                startFloatingActionButton.isEnabled = false
                pauseFloatingActionButton.isEnabled = true
                stopFloatingActionButton.isEnabled = true
            }
            TimerState.Stopped -> {
                startFloatingActionButton.isEnabled = true
                pauseFloatingActionButton.isEnabled = false
                stopFloatingActionButton.isEnabled = false
            }
            TimerState.Paused -> {
                startFloatingActionButton.isEnabled = true
                pauseFloatingActionButton.isEnabled = false
                stopFloatingActionButton.isEnabled = true
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_timer, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}