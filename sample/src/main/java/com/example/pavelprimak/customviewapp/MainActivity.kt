package com.example.pavelprimak.customviewapp

import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import com.pavelprimak.seekbar24hour.customView.SeekBar24HourView
import com.pavelprimak.seekbar24hour.model.Event

class MainActivity : AppCompatActivity() {
    private val seekBarView: SeekBar24HourView by lazy {
        findViewById<SeekBar24HourView>(R.id.seek_bar)
    }
    private val btn: Button by lazy {
        findViewById<Button>(R.id.btn_draw)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mainEventsList = ArrayList<Event>()
        mainEventsList.add(Event(3600, 3600))
        val lineView = seekBarView.lineGraphView
        seekBarView.setCursorDrawable(ContextCompat.getDrawable(this,R.drawable.marker_center)!!)

        lineView?.setMainEventList(mainEventsList)
        btn.setOnClickListener({
            mainEventsList.clear()
            seekBarView.setPositionInPercents(100f)
            Toast.makeText(this,seekBarView.getPositionInPercents().toString(),Toast.LENGTH_LONG).show()
            mainEventsList.add(Event(7200, 3600))
            lineView?.setMarkEventList(mainEventsList)
        })
    }
}