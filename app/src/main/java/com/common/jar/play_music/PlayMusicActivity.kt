package com.common.jar.play_music

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.*
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.common.jar.R
import com.common.jar.databinding.ActivityPlayMusicBinding
import com.common.jar.dialog.MusicListDialog
import com.common.jar.play_music.config.LocalConfig
import com.common.jar.play_music.model.PlayMusicModel
import com.common.jar.play_music.service.AudioService
import com.common.jar.play_music.service.IService
import com.common.jar.play_music.service.Mode
import com.common.tool.base.BaseActivity

/**
 * @author 李雄厚
 *
 * @features 音乐播放
 */
class PlayMusicActivity : BaseActivity<ActivityPlayMusicBinding, PlayMusicModel>(),
    View.OnClickListener, SeekBar.OnSeekBarChangeListener {


    private val connection by lazy { MyServiceConnection() }
    private var iService: IService? = null

    override val layoutId: Int = R.layout.activity_play_music
    override fun initView(savedInstanceState: Bundle?) {
        binding.model = model
//        Log.e("测试", "musicList---${Gson().toJson(musicList)}")
//        Log.e("测试", "position---$position")


        LocalConfig.notifyUpdateUi.observe(this){
            initTitleBar(it?.filterName ?: "歌名错误")
            getTitleBar().findViewById<AppCompatTextView>(R.id.baseTitleText).apply {
                setTextColor(ContextCompat.getColor(this@PlayMusicActivity, R.color.white))
            }
            getTitleBar().findViewById<AppCompatTextView>(R.id.baseTitleHint).apply {
                setTextColor(ContextCompat.getColor(this@PlayMusicActivity, R.color.white))
                visibility = View.VISIBLE
                text = it?.singer ?: "歌手错误"
            }

            getTitleBar().findViewById<AppCompatImageView>(R.id.baseTitleClose).apply {
                imageTintList =
                    ContextCompat.getColorStateList(this@PlayMusicActivity, R.color.white)
            }
            //设置歌曲名称

            binding.lyricView.setSongName(it?.filterName?:"")
            updatePlatStateView()
            //设置总进度时间
            model.durationTime.set((iService?.getDuration() ?: 0).formatTime())
            //设置总进度最大值
            binding.duration = iService?.getDuration() ?: 0
            //设置歌词播放总进度
            binding.lyricView.duration = iService?.getDuration() ?: 0
            //更新播放进度
            starUpdateProgress()
            //更新播放模式图标
            updatePlayModeView()
        }
        //通过AudioService播放音乐
        val intent = intent
        intent.setClass(this, AudioService::class.java)
        //先绑定服务
        bindService(intent, connection, BIND_AUTO_CREATE)
        //再开启服务
        startService(intent)


        initClick()
    }


    private fun initClick() {
        binding.state.setOnClickListener(this)
        binding.mode.setOnClickListener(this)
        binding.seekBar.setOnSeekBarChangeListener(this)
        binding.next.setOnClickListener(this)
        binding.pre.setOnClickListener(this)
        binding.playList.setOnClickListener(this)

        binding.lyricView.setlListener {
            iService?.seekTo(it)
            updateProgress(it)
        }

    }

    override fun initData() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.state -> updatePlatState()
            R.id.mode -> updatePlayMode()
            R.id.next -> iService?.playNext()
            R.id.pre -> iService?.playPre()
            R.id.playList -> iService?.getMusicList()?.let {
                MusicListDialog.instance(it, iService?.getPosition() ?: -1).apply {
                    playClickEvent = { pos ->
                        iService?.setPosition(pos)
                    }
                    show(supportFragmentManager)
                }
            }
        }
    }

    /**
     * 更新播放模式
     */
    private fun updatePlayMode() {
        iService?.updatePlayMode()
        //更新播放模式图标
        updatePlayModeView()
    }

    /**
     * 更新播放模式图标
     */
    private fun updatePlayModeView() {
        iService?.let {
            //获取播放模式
            //更新图标
            binding.mode.setImageResource(
                when (it.getPlayMode()) {
                    Mode.MODE_ALL -> R.drawable.ic_cycle_play
                    Mode.MODE_SINGLE -> R.drawable.ic_single_play
                    else -> R.drawable.ic_random_play
                }
            )

        }

    }

    /**
     * 更新播放状态
     */
    private fun updatePlatState() {
        //更新播放状态
        iService?.updatePlatState()
        //更新播放状态图标
        updatePlatStateView()

    }

    /**
     * 更新播放状态图标
     */
    private fun updatePlatStateView() {
        //获取当前播放状态
        val isPlaying = iService?.isPlaying()
        //根据播放状态更新图标
        isPlaying?.let {
            if (it) {
                //正在播放
                binding.state.setImageResource(R.drawable.ic_playing)
                handler.post(task)
            } else {
                //暂停播放
                binding.state.setImageResource(R.drawable.ic_play_pause)
                handler.removeCallbacks(task)
            }
        }
    }

    /**
     * 更新播放进度
     */
    private val handler = Handler(Looper.myLooper()!!)
    private val task: Runnable = Runnable { starUpdateProgress() }

    /**
     * 开始更新当前进度
     */
    private fun starUpdateProgress() {
        //获取当前进度
        val progress = iService?.getProgress() ?: 0
        //更新进度数据
        updateProgress(progress)
        //定时获取进度
        handler.post(task)
    }

    /**
     * 更新进度图标
     */
    private fun updateProgress(progress: Int) {
        model.progressTime.set(progress.formatTime())
        binding.progress = progress
        binding.lyricView.updateProgress(progress)
    }

    /**
     * 手指拖动进度条改变回调
     * @param fromUser true通过用户手指拖动改变进度
     */
    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        if (!fromUser) return
        //更新播放进度
        iService?.seekTo(progress)
        //更新进度界面
        updateProgress(progress)
    }

    /**
     * 手指触摸回调
     */
    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    /**
     * 手指抬起回调
     */
    override fun onStopTrackingTouch(seekBar: SeekBar?) {
    }

    inner class MyServiceConnection : ServiceConnection {
        /**
         * 绑定服务成功
         */
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            iService = service as IService
        }

        /**
         * 服务意外断开
         */
        override fun onServiceDisconnected(name: ComponentName?) {
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        unbindService(connection)
        handler.removeCallbacks(task)
    }


}