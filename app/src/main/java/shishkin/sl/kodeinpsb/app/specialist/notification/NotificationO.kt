package shishkin.sl.kodeinpsb.app.specialist.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build


import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import shishkin.sl.kodeinpsb.R
import shishkin.sl.kodeinpsb.app.ApplicationConstant
import shishkin.sl.kodeinpsb.app.screen.main.MainActivity
import shishkin.sl.kodeinpsb.common.ApplicationUtils
import shishkin.sl.kodeinpsb.sl.specialist.ApplicationSpecialist

@RequiresApi(Build.VERSION_CODES.O)
class NotificationO : INotificationShortSpecialist {
    companion object {
        const val CANAL_NAME = "Notification Service Canal"
    }

    private val GROUP_NAME = ApplicationSpecialist.appContext.getString(R.string.app_name)
    private var id = -1
    private val nm: NotificationManager = ApplicationUtils.getSystemService(
        ApplicationSpecialist.appContext,
        Context.NOTIFICATION_SERVICE
    )

    override fun addNotification(title: String?, message: String) {
        id = System.currentTimeMillis().toInt()
        show(title, message)
    }

    override fun replaceNotification(title: String?, message: String) {
        if (id == -1) {
            id = System.currentTimeMillis().toInt()
        }
        show(title, message)
    }


    private fun show(title: String?, message: String) {
        val context = ApplicationSpecialist.appContext

        var channel: NotificationChannel? = nm.getNotificationChannel(GROUP_NAME)
        if (channel == null) {
            channel =
                NotificationChannel(GROUP_NAME, CANAL_NAME, NotificationManager.IMPORTANCE_LOW)
            channel.enableLights(true)
            channel.lightColor = R.color.red
            nm.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        intent.action = ApplicationConstant.ACTION_CLICK

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_ONE_SHOT
        )

        val notificationBuilder = NotificationCompat.Builder(context, GROUP_NAME)
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.icon)
            .setAutoCancel(true)
            .setWhen(System.currentTimeMillis())
            .setDefaults(0)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setGroup(GROUP_NAME)
            .setContentText(message)
        if (!title.isNullOrEmpty()) {
            notificationBuilder.setContentTitle(title)
        }
        nm.notify(id, notificationBuilder.build())
    }


    override fun clear() {
        nm.cancelAll()
    }

}