package com.example.notificationsample

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.qualtrics.digital.Qualtrics
import kotlinx.android.synthetic.main.activity_main.*
import com.qualtrics.digital.TargetingResult
import com.qualtrics.digital.IQualtricsCallback
import com.qualtrics.digital.QualtricsSurveyActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Qualtrics.instance().initialize(BuildConfig.brandId, BuildConfig.zoneId, BuildConfig.interceptId, this)

        initView()
        createNotificationChannel()
    }

    private fun initView() {
//        btnShow.setOnClickListener {
//            showInAppNotification()
//        }

        btnSurvey.setOnClickListener {
            Qualtrics.instance().evaluateTargetingLogic(MyCallback())
        }
    }

    /**
     * About head-up Notification be
     *
     * Android Nougat API 24 (7.0) >60s
     * Android Nougat API 25 (7.1.1) >60s
     * Android Oreo API 26 (8.0) >60s
     * Android Oreo API 27 (8.1) >60s
     * Android Pie API 28 (9.0) >60s
     * Android Q API 29 (9.+) > 5s ->become normal notification
     */
    private fun showInAppNotification(url: String) {
//        val intent = Intent(this, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
//        }

        val intent = Intent(this, QualtricsSurveyActivity::class.java)
            .putExtra("targetURL", url)
            .putExtra("interceptID", "SI_4MKhXuBhLhgkxQV")
            .putExtra("creativeID", "CR_8G5H5p3DP7xBg5n")
            .putExtra("actionSetID", "AS_03959275")
            .apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            NotificationCompat.Builder(this, getString(R.string.channel_id))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setContentTitle(getString(R.string.notification_content_title))
                .setContentText(getString(R.string.notification_content))
                .setCategory(Notification.CATEGORY_CALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setFullScreenIntent(pendingIntent, true)
                .setVibrate(LongArray(20))
                .setAutoCancel(true)
                .setOngoing(true)
                .setContentIntent(pendingIntent)

        } else {
            NotificationCompat.Builder(this, getString(R.string.channel_id))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher))
                .setContentTitle(getString(R.string.notification_content_title))
                .setContentText(getString(R.string.notification_content))
                .setDefaults(Notification.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setOngoing(true)
                .setContentIntent(pendingIntent)
        }

        with(NotificationManagerCompat.from(this)) {
            notify(1, builder.build())
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(getString(R.string.channel_id), name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private inner class MyCallback : IQualtricsCallback {
        override fun run(targetingResult: TargetingResult) {
            Log.d("ADAODOADOAOADO", "" + targetingResult.surveyUrl)
            if (targetingResult.passed()) {
                Qualtrics.instance().properties.setString("brandName", "HSBC")
                Qualtrics.instance().properties.notificationIconAsset = R.drawable.ic_close_black_24dp
                Qualtrics.instance().display(this@MainActivity)
//                showInAppNotification(targetingResult.surveyUrl)
            }

        }
    }
}
