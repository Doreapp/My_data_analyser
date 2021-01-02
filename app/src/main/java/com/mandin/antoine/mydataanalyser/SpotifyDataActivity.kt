package com.mandin.antoine.mydataanalyser

import android.net.Uri
import android.os.Bundle
import androidx.documentfile.provider.DocumentFile
import com.mandin.antoine.mydataanalyser.spotify.asynctasks.LoadStreamsTask
import com.mandin.antoine.mydataanalyser.spotify.model.data.StreamsData
import com.mandin.antoine.mydataanalyser.spotify.model.data.StreamsStats
import com.mandin.antoine.mydataanalyser.tools.TaskRunner
import com.mandin.antoine.mydataanalyser.utils.Preferences
import com.mandin.antoine.mydataanalyser.utils.Utils
import com.mandin.antoine.mydataanalyser.views.LoadingDialog
import com.mandin.antoine.mydataanalyser.views.adapters.DurationItemAdapter
import com.mandin.antoine.mydataanalyser.views.adapters.NumberedItemAdapter
import kotlinx.android.synthetic.main.activity_posts.*
import kotlinx.android.synthetic.main.activity_spotify_data.*

/**
 * Activity used to display spotify data and statistics
 */
class SpotifyDataActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spotify_data)

        loadFromStorage()
    }

    /**
     * Display data and stats about streams
     *
     * @see StreamsData
     * @see StreamsStats
     */
    private fun displayData(streamsData: StreamsData?, streamsStats: StreamsStats?) {
        streamsData?.let { data ->
            tvStreamCounts.text = "${data.streams.size} streams"
        }

        streamsStats?.let { stats ->
            tvStreamTime.text = "${Utils.formatDurationUntilHour(stats.streamTime)} listened"

            with(chartStreamCount) {
                countsWeekly = stats.streamCountByWeek
                countsMonthly = stats.streamCountByMonth
                countsYearly = stats.streamCountByYear
                lineLabel = "Stream count"
                showCountsYearly()
            }

            with(chartStreamTime) {
                countsWeekly = stats.streamTimeByWeek
                countsMonthly = stats.streamTimeByMonth
                countsYearly = stats.streamTimeByYear
                lineLabel = "Stream time"
                showCountsYearly()
            }

            // artist count
            val artistCountAdapter = object : NumberedItemAdapter<MutableMap.MutableEntry<String, Int>>(
                ArrayList(stats.artistCounts.entries)
            ) {
                override fun getName(value: MutableMap.MutableEntry<String, Int>): String {
                    return value.key
                }

                override fun getNumber(value: MutableMap.MutableEntry<String, Int>): Int {
                    return value.value
                }
            }

            listArtistCounts.adapter = artistCountAdapter
            listArtistCounts.isShowMoreButtonVisible = true

            // track count
            val trackCountAdapter = object : NumberedItemAdapter<MutableMap.MutableEntry<String, Int>>(
                ArrayList(stats.trackCounts.entries)
            ) {
                override fun getName(value: MutableMap.MutableEntry<String, Int>): String {
                    return value.key
                }

                override fun getNumber(value: MutableMap.MutableEntry<String, Int>): Int {
                    return value.value
                }
            }

            listTrackCounts.adapter = trackCountAdapter
            listTrackCounts.isShowMoreButtonVisible = true

            // artist time
            val artistTimeAdapter = object : DurationItemAdapter<MutableMap.MutableEntry<String, Long>>(
                ArrayList(stats.artistTimes.entries)
            ) {
                override fun getName(value: MutableMap.MutableEntry<String, Long>): String {
                    return value.key
                }

                override fun getDuration(value: MutableMap.MutableEntry<String, Long>): Long {
                    return value.value
                }
            }

            listArtistTimes.adapter = artistTimeAdapter
            listArtistTimes.isShowMoreButtonVisible = true

            // track time
            val trackTimeAdapter = object : DurationItemAdapter<MutableMap.MutableEntry<String, Long>>(
                ArrayList(stats.trackTimes.entries)
            ) {
                override fun getName(value: MutableMap.MutableEntry<String, Long>): String {
                    return value.key
                }

                override fun getDuration(value: MutableMap.MutableEntry<String, Long>): Long {
                    return value.value
                }
            }

            listTrackTimes.adapter = trackTimeAdapter
            listTrackTimes.isShowMoreButtonVisible = true
        }
    }

    /**
     * Load stream data from storage, using [Preferences.getSpotifyFolderUri] and [LoadStreamsTask]
     */
    private fun loadFromStorage() {
        val folderUri = Uri.parse(Preferences.getSpotifyFolderUri(this))
        if (folderUri != null) {
            val docFile = DocumentFile.fromTreeUri(this, folderUri)
            if (docFile != null) {
                with(LoadingDialog(this)) {
                    hasProgress = true
                    TaskRunner().executeAsync(
                        LoadStreamsTask(docFile, this@SpotifyDataActivity, observer),
                        object : TaskRunner.Callback<LoadStreamsTask.Result> {
                            override fun onComplete(result: LoadStreamsTask.Result) {
                                displayData(result.streamsData, result.streamsStats)
                                dismiss()
                            }
                        })
                    show()
                }
            } else {
                alert("We were not able to found the spotify folder in your storage.")
            }
        } else {
            alert("You didn't specified the spotify folder location in your storage.")
        }
    }
}