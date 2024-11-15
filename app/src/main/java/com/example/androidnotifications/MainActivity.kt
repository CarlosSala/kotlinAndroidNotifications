package com.example.androidnotifications

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.RemoteViews
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.Person
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.IconCompat
import com.example.androidnotifications.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val channelName = "channelName"
    private val channelId = "channelId"

    private lateinit var notificationStyle1: Notification
    private val notificationStyle1ID = 0

    private lateinit var notificationStyle2: Notification
    private val notificationStyle2ID = 1

    private lateinit var notificationStyle3: Notification
    private val notificationStyle3ID = 2

    private lateinit var notificationStyle4: Notification
    private val notificationStyle4ID = 3

    private lateinit var notificationStyle5: Notification
    private val notificationStyle5ID = 4

    private lateinit var notificationCustomStyle: Notification
    private val notificationCustomStyleID = 5

    private lateinit var emailGroup: Notification
    private lateinit var emailNotification: Notification
    private val emailGroupID = 20
    private var emailNotificationID = 6

    companion object {
        const val INTENT_REQUEST = 0
        const val GROUP_KEY_EMAIL = "com.example.androidnotifications.email"
        private const val REQUEST_CODE_POST_NOTIFICATIONS = 1001
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        /*     ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                 val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                 v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                 insets
             }*/

        // Verify and ask POST_NOTIFICATIONS permissions in Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, "android.permission.POST_NOTIFICATIONS"
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf("android.permission.POST_NOTIFICATIONS"),
                    REQUEST_CODE_POST_NOTIFICATIONS
                )
            }
        }

        createNotificationChannel()

        buildNotificationStyle1()
        buildNotificationStyle2()
        buildNotificationStyle3()
        buildNotificationStyle4()
        buildNotificationStyle5()
        createGroupBuilder()
        buildEmailNotification()
        buildNotificationCustomStyle()

        buttonsListener()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_POST_NOTIFICATIONS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this, "Permission is required to show notifications", Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun buildNotificationStyle1() {
        val myBitmap = R.drawable.sky.createBitmap(this)

        notificationStyle1 = NotificationCompat.Builder(this, channelId).also {
            it.setSmallIcon(R.drawable.ic_notification)
            it.setContentTitle("Title of notification")
            it.setContentText("Content of notification")
            it.setStyle(
                NotificationCompat.BigPictureStyle()
                    .bigPicture(myBitmap)
                //.bigLargeIcon(null)
            )
            it.setLargeIcon(myBitmap)
        }.build()
    }

    private fun buildNotificationStyle2() {
        val myBitmap = R.drawable.sky.createBitmap(this)

        notificationStyle2 = NotificationCompat.Builder(this, channelId).also {
            it.setSmallIcon(R.drawable.ic_notification)
            it.setContentTitle("Title of notification")
            it.setContentText("Content of notification")
            it.setLargeIcon(myBitmap)
            it.setStyle(NotificationCompat.BigTextStyle().bigText(getString(R.string.large_text)))
        }.build()
    }

    private fun buildNotificationStyle3() {
        notificationStyle3 = NotificationCompat.Builder(this, channelId).also {
            it.setSmallIcon(R.drawable.ic_notification)
            it.setContentTitle("user@gmail.com")
            it.setContentText("You have 3 new messages")
            it.setStyle(
                NotificationCompat.InboxStyle()
                    .addLine("Good morning!, I hope you have a nice day...")
                    .addLine("Today is your birthday...")
                    .addLine("Javier invites you to the party...")
            )
        }.build()
    }

    private fun buildNotificationStyle4() {
        val captainIcon = R.drawable.captain.createBitmap(this)
        val soldierIcon = R.drawable.soldier.createBitmap(this)

        val message1 = NotificationCompat.MessagingStyle.Message(
            "Soldier !! I didn't see you yesterday at the camouflage test",
            System.currentTimeMillis(),
            Person.Builder().also {
                it.setName("Captain")
                it.setIcon(IconCompat.createWithAdaptiveBitmap(captainIcon))
            }.build()
        )

        val message2 = NotificationCompat.MessagingStyle.Message(
            "¡Thanks, My captain!",
            System.currentTimeMillis(),
            Person.Builder().also {
                it.setName("Soldier Ryan")
                it.setIcon(IconCompat.createWithAdaptiveBitmap(soldierIcon))
            }.build()
        )

        notificationStyle4 = NotificationCompat.Builder(this, channelId).also {
            it.setSmallIcon(R.drawable.ic_notification)
            it.setStyle(
                NotificationCompat.MessagingStyle(
                    Person.Builder().also {
                        it.setName("My name")
                    }.build()
                )
                    .addMessage(message1)
                    .addMessage(message2)
            )
        }.build()
    }


    private fun buildNotificationStyle5() {
        val myBitmap = BitmapFactory.decodeResource(resources, R.drawable.image_profile)

        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(
                INTENT_REQUEST,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
        }

        notificationStyle5 = NotificationCompat.Builder(this, channelId).also {
            it.setSmallIcon(R.drawable.ic_notification)
            it.setContentTitle("Custom Media Player")
            it.setContentText("New song")
            it.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

            if (pendingIntent != null) {
                it.addAction(R.drawable.ic_previous, "Previous", pendingIntent)
                it.addAction(R.drawable.ic_pause, "Pause", pendingIntent)
                it.addAction(R.drawable.ic_next, "Next", pendingIntent)
            }

            // with library
            it.setStyle(
                androidx.media.app.NotificationCompat.MediaStyle()
                    .setShowActionsInCompactView(0, 1, 2)
            )
            it.setLargeIcon(myBitmap)
        }.build()
    }

    private fun buildEmailNotification() {
        val myBitmap = R.drawable.image_profile.createBitmap(this)

        emailNotification = NotificationCompat.Builder(this, channelId).also {
            it.setSmallIcon(R.drawable.ic_notification)
            it.setContentTitle("user@gmail.com")
            it.setContentText("Email notification")
            it.setLargeIcon(myBitmap)
            it.setGroup(GROUP_KEY_EMAIL)
        }.build()
    }

    private fun createGroupBuilder() {
        emailGroup = NotificationCompat.Builder(this, channelId).also {
            it.setSmallIcon(R.drawable.ic_notification)
            it.setContentTitle("Group notifications")
            it.setContentText("You have some emails pending to read")
            it.setGroup(GROUP_KEY_EMAIL)
            it.setGroupSummary(true)
        }.build()
    }

    private fun buildNotificationCustomStyle() {
        val notificationLayout = RemoteViews(packageName, R.layout.notification_small)
        val notificationLayoutExpanded = RemoteViews(packageName, R.layout.notification_expanded)

        notificationCustomStyle = NotificationCompat.Builder(this, channelId).also {
            it.setSmallIcon(R.drawable.ic_notification)
            // it.setStyle(NotificationCompat.DecoratedCustomViewStyle())
            it.setCustomContentView(notificationLayout)
            it.setCustomBigContentView(notificationLayoutExpanded)
        }.build()
    }

    private fun buttonsListener() {
        val notificationManager = NotificationManagerCompat.from(this)

        if (checkNotificationPermission()) {
            binding.apply {
                btnNotificationStyle1.setOnClickListener {
                    notificationManager.notify(notificationStyle1ID, notificationStyle1)
                }
                btnNotificationStyle2.setOnClickListener {
                    notificationManager.notify(notificationStyle2ID, notificationStyle2)
                }
                btnNotificationStyle3.setOnClickListener {
                    notificationManager.notify(notificationStyle3ID, notificationStyle3)
                }
                btnNotificationStyle4.setOnClickListener {
                    notificationManager.notify(notificationStyle4ID, notificationStyle4)
                }
                btnNotificationStyle5.setOnClickListener {
                    notificationManager.notify(notificationStyle5ID, notificationStyle5)
                }
                btnGroupNotifications.setOnClickListener {
                    notificationManager.notify(emailGroupID, emailGroup)
                    notificationManager.notify(emailNotificationID++, emailNotification)
                }
                btnNotificationCustomStyle1.setOnClickListener {
                    notificationManager.notify(notificationCustomStyleID, notificationCustomStyle)
                }
            }
        }
    }

    /**
     * Verify if POST_NOTIFICATIONS has been granted.
     */
    private fun checkNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permission = "android.permission.POST_NOTIFICATIONS"
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                true
            } else {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT)
                    .show()
                false
            }
        } else {
            // Permission is not required in Android 13 or lower
            true
        }
    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelImportance = NotificationManager.IMPORTANCE_HIGH

            val channel = NotificationChannel(channelId, channelName, channelImportance).apply {
                lightColor = Color.RED
                enableLights(true)
            }

            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }

    }
}