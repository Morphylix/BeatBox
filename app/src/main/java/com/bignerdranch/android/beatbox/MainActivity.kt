package com.bignerdranch.android.beatbox

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.beatbox.databinding.ActivityMainBinding
import com.bignerdranch.android.beatbox.databinding.ListItemSoundBinding

const val INITIAL_PROGRESS = 100

class MainActivity : Activity(), SeekBar.OnSeekBarChangeListener {
    private lateinit var beatBox: BeatBox
    private lateinit var progressText: TextView // TODO: REPLACE WITH BINDING? IS IT POSSIBLE?
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =
            DataBindingUtil.setContentView(this, R.layout.activity_main)
        beatBox = BeatBox(assets)
        binding.recyclerView.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = SoundAdapter(beatBox.sounds)
        }
        binding.seekBar.setOnSeekBarChangeListener(this)
        progressText = findViewById(R.id.seek_bar_textview)
        binding.seekBarTextview.text = getString(R.string.string_playback_speed, INITIAL_PROGRESS)
    }

    private inner class SoundHolder(private val binding: ListItemSoundBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.viewModel = SoundViewModel(beatBox)
        }

        fun bind(sound: Sound) {
            binding.apply {
                viewModel?.sound = sound
                executePendingBindings()
            }
        }
    }

    private inner class SoundAdapter(private val sounds: List<Sound>) :
        RecyclerView.Adapter<SoundHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SoundHolder {
            val binding = DataBindingUtil.inflate<ListItemSoundBinding>(
                layoutInflater,
                R.layout.list_item_sound,
                parent,
                false
            )
            return SoundHolder(binding)
        }

        override fun onBindViewHolder(holder: SoundHolder, position: Int) {
            holder.bind(sounds[position])
        }

        override fun getItemCount(): Int = sounds.size

    }

    override fun onDestroy() {
        super.onDestroy()
        beatBox.release()
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        if (beatBox.lastPlayedSound != null) {
            val convertedProgress = p1 / 100.0f
            beatBox.playRate = convertedProgress
        }
        binding.seekBarTextview.text = getString(R.string.string_playback_speed, p1)
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {

    }

    override fun onStopTrackingTouch(p0: SeekBar?) {

    }

}