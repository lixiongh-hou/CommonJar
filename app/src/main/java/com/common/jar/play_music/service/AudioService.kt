package com.common.jar.play_music.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.media.MediaPlayer
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.common.jar.MainActivity
import com.common.jar.R
import com.common.jar.play_music.MusicActivity
import com.common.jar.play_music.PlayMusicActivity
import com.common.jar.play_music.config.LocalConfig
import com.common.jar.play_music.data.MusicList
import com.common.jar.play_music.getAlbumArt
import com.common.jar.play_music.service.States.FROM_CONTENT
import com.common.jar.play_music.service.States.FROM_NEXT
import com.common.jar.play_music.service.States.FROM_PER
import com.common.jar.play_music.service.States.FROM_STATES
import com.common.tool.sp.Preference
import com.common.tool.util.SpanBuilder
import kotlin.random.Random

/**
 * @author 李雄厚
 *
 * @features 音乐播放服务
 */
class AudioService : Service() {
    companion object {
        private const val TAG = "AudioService"
    }

    private var musicList: ArrayList<MusicList>? = null
    private var position: Int = -2
    private var mode by Preference("mode", Mode.MODE_ALL)

    private var mediaPlayer: MediaPlayer? = null
    private val binder by lazy { AudioBind() }

    private var mNotificationManager: NotificationManagerCompat? = null
    private var mNotification: NotificationCompat.Builder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(TAG, "intent---$intent")
        when (intent?.getIntExtra("from", -1)) {
            FROM_PER -> {
                binder.playPre()
            }
            FROM_NEXT -> {
                binder.playNext()
            }
            FROM_STATES -> {
                binder.updatePlatState()
            }
            FROM_CONTENT -> {
                binder.notifyUpdateUi()
            }
            else -> {
                //获取音乐集合和List
                intent?.let {
                    val pos = it.getIntExtra("position", -1)
                    if (pos != position) {
                        position = pos
                        musicList =
                            it.getParcelableArrayListExtra<MusicList>("list") as ArrayList<MusicList>
                        //开始播放音乐
                        binder.playItem()
                    } else {
                        binder.notifyUpdateUi()
                    }
//                    Log.e("测试", "专辑封面${(musicList?.get(position)?.path)?.getAlbumArt(this)}")
                }
            }
        }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder = binder

    inner class AudioBind : Binder(), MediaPlayer.OnPreparedListener, IService,
        MediaPlayer.OnCompletionListener {
        /**
         * 音乐开始播放
         */
        override fun onPrepared(mp: MediaPlayer?) {
            mediaPlayer?.start()
            //通知更新UI
            notifyUpdateUi()
            //显示通知
            showNotification()
        }

        /**
         * 音乐播放完成
         */
        override fun onCompletion(mp: MediaPlayer?) {
            autoPlayNext()
        }

        /**
         * 根据播放状态自动播放下一首
         */
        private fun autoPlayNext() {
            musicList?.let {
                position = when (mode) {
                    Mode.MODE_ALL -> (position + 1) % it.size
                    Mode.MODE_RANDOM -> Random.nextInt(it.size)
                    else -> position
                }
                playItem()
            }


        }

        /**
         * 更新播放状态
         */
        override fun updatePlatState() {
            //获取当前播放状态
            val isPlaying = isPlaying()
            //切换当前播放状态
            isPlaying?.let {
                if (it) {
                    //暂停播放
                    pause()
                } else {
                    //开始播放
                    start()
                }
            }
        }

        @SuppressLint("RestrictedApi")
        private fun pause() {
            mediaPlayer?.pause()
            //刷新界面
            notifyUpdateUi()
            //刷新通知栏
            mNotification?.run {
                //先设置好图标
                bigContentView.setImageViewResource(R.id.playing, R.drawable.ic_notice_play_pause)
                contentView.setImageViewResource(R.id.playing, R.drawable.ic_notice_play_pause1)
                //再刷新通知栏
                mNotificationManager?.notify(100, mNotification!!.build())
            }
        }

        @SuppressLint("RestrictedApi")
        private fun start() {
            mediaPlayer?.start()
            //刷新界面
            notifyUpdateUi()
            //刷新通知栏
            mNotification?.run {
                //先设置好图标
                bigContentView.setImageViewResource(R.id.playing, R.drawable.ic_notice_playing)
                contentView.setImageViewResource(R.id.playing, R.drawable.ic_notice_playing1)
                //再刷新通知栏
                mNotificationManager?.notify(100, mNotification!!.build())
            }

        }


        fun notifyUpdateUi() {
            LocalConfig.notifyUpdateUi.postValue(musicList?.get(position))
        }

        override fun isPlaying(): Boolean? = mediaPlayer?.isPlaying
        override fun getDuration(): Int = mediaPlayer?.duration ?: 0
        override fun getProgress(): Int = mediaPlayer?.currentPosition ?: 0
        override fun seekTo(progress: Int) {
            mediaPlayer?.seekTo(progress)
        }

        /**
         * 更新播放模式
         */
        override fun updatePlayMode() {
            mode = when (mode) {
                Mode.MODE_ALL -> Mode.MODE_SINGLE
                Mode.MODE_SINGLE -> Mode.MODE_RANDOM
                else -> Mode.MODE_ALL
            }
        }

        override fun getPlayMode(): Int = mode

        /**
         * 播放下一首
         */
        override fun playNext() {
            musicList?.let {
                position = when (mode) {
                    Mode.MODE_RANDOM -> Random.nextInt(it.size)
                    else -> (position + 1) % it.size
                }
                playItem()
            }
        }

        /**
         * 播放上一首
         */
        override fun playPre() {

            musicList?.let {
                when (mode) {
                    Mode.MODE_RANDOM -> position = Random.nextInt(it.size)
                    else -> {
                        if (position == 0) {
                            position = it.size - 1
                        } else {
                            position--
                        }
                    }
                }
                playItem()
            }


        }

        override fun getMusicList(): ArrayList<MusicList>? = musicList
        override fun getPosition(): Int = position

        /**
         * 设置当前播放位置
         */
        override fun setPosition(position: Int) {
            this@AudioService.position = position
            playItem()
        }

        fun playItem() {
            release()
            mediaPlayer = MediaPlayer()
            mediaPlayer?.apply {
                setOnPreparedListener(this@AudioBind)
                setOnCompletionListener(this@AudioBind)
                setDataSource(musicList?.get(position)?.path)
                prepareAsync()
            }
        }
    }


    private fun showNotification() {
        val channelId = "playId".createNotificationChannel()
        val notificationLayoutBig =
            RemoteViews(packageName, R.layout.notification_big_layout).apply {
                setTextViewText(R.id.playTitle, musicList?.get(position)?.filterName)
                setTextViewText(R.id.playName, musicList?.get(position)?.singer)
                setImageViewResource(R.id.playImage, R.mipmap.ic_launcher)
                setImageViewResource(R.id.playSong, R.drawable.ic_notice_play_song)
                setImageViewResource(R.id.playing, R.drawable.ic_notice_playing)
                setImageViewResource(R.id.playNext, R.drawable.ic_notice_play_next)
                setOnClickPendingIntent(R.id.playSong, getPerPendingIntent())
                setOnClickPendingIntent(R.id.playing, getStatePendingIntent())
                setOnClickPendingIntent(R.id.playNext, getNextPendingIntent())
            }
        val string = SpanBuilder().apply {
            append("${musicList?.get(position)?.filterName}\u3000")
            append(musicList?.get(position)?.singer?:"")
            setProportion(0.8F)
            setForegroundColor(ContextCompat.getColor(this@AudioService, R.color.font66))
        }.create()
        val notificationLayout = RemoteViews(packageName, R.layout.notification_layout).apply {
            setTextViewText(R.id.playTitle, string)
            setImageViewResource(R.id.playImage, R.mipmap.ic_launcher)
            setImageViewResource(R.id.playSong, R.drawable.ic_notice_play_song1)
            setImageViewResource(R.id.playing, R.drawable.ic_notice_playing1)
            setImageViewResource(R.id.playNext, R.drawable.ic_notice_play_next1)
            setOnClickPendingIntent(R.id.playSong, getPerPendingIntent())
            setOnClickPendingIntent(R.id.playing, getStatePendingIntent())
            setOnClickPendingIntent(R.id.playNext, getNextPendingIntent())
        }

        mNotification = NotificationCompat.Builder(this, channelId!!)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setCustomBigContentView(notificationLayoutBig)
            .setCustomContentView(notificationLayout)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(getPendingIntent())
            // 它会在用户点按通知后自动移除通知
            .setAutoCancel(false)
            //除非杀死程序或代码取消，否正不会清除通知
            .setOngoing(true)
        mNotificationManager = NotificationManagerCompat.from(this)
        mNotification?.let { mNotificationManager?.notify(100, it.build()) }

    }

    private fun String.createNotificationChannel(): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            /**
             * IMPORTANCE_NONE 关闭通知
             * IMPORTANCE_MIN 开启通知，不会弹出，但没有提示音，状态栏中无显示
             * IMPORTANCE_LOW 开启通知，不会弹出，不发出提示音，状态栏中显示
             * IMPORTANCE_DEFAULT 开启通知，不会弹出，发出提示音，状态栏中显示
             * IMPORTANCE_HIGH 开启通知，会弹出，发出提示音，状态栏中显示
             */
            val channel1 = NotificationChannel("play", "播放控制", NotificationManager.IMPORTANCE_LOW)
            val channel2 = NotificationChannel(this, "音乐播放", NotificationManager.IMPORTANCE_DEFAULT)
//            //设置提示音
//            channel.setSound();
//            //开启指示灯
//            channel.enableLights();
//            //开启震动
//            channel.enableVibration();
//            //设置锁屏展示
//            channel.setLockscreenVisibility();
//            //设置渠道描述
//            channel.setDescription();
            manager.createNotificationChannels(arrayListOf(channel1, channel2))
            this
        } else {
            null
        }
    }

    private fun getPendingIntent(): PendingIntent {
        val intentA = Intent(this, MainActivity::class.java)
        val intentB = Intent(this, MusicActivity::class.java)
        val intentC = Intent(this, PlayMusicActivity::class.java)

        intentC.putExtra("from", FROM_CONTENT)
        val intents = arrayOf(intentA, intentB, intentC)
        return PendingIntent.getActivities(this, 10001, intents, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun getNextPendingIntent(): PendingIntent {
        val intent = Intent(this, AudioService::class.java)
        intent.putExtra("from", FROM_NEXT)
        return PendingIntent.getService(this, 10002, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


    private fun getStatePendingIntent(): PendingIntent {
        val intent = Intent(this, AudioService::class.java)
        intent.putExtra("from", FROM_STATES)
        return PendingIntent.getService(this, 10003, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


    private fun getPerPendingIntent(): PendingIntent {
        val intent = Intent(this, AudioService::class.java)
        intent.putExtra("from", FROM_PER)
        return PendingIntent.getService(this, 10004, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }


    private fun release() {
        if (mediaPlayer != null) {
            mediaPlayer?.reset()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        release()
    }
}