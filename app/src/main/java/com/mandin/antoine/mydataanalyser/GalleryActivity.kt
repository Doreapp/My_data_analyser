package com.mandin.antoine.mydataanalyser

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mandin.antoine.mydataanalyser.views.ImageAdapter
import kotlinx.android.synthetic.main.activity_gallery.*


class GalleryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gallery)

        initList()
    }

    fun testImageIdList(): List<Int> {
        return listOf(
            R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_background,
            R.drawable.ic_launcher_foreground,
            R.drawable.ic_launcher_foreground,
            R.drawable.ic_launcher_foreground,
            R.drawable.ic_launcher_foreground,
            R.drawable.ic_launcher_foreground,
            R.drawable.ic_launcher_foreground
        )
    }

    fun initList() {
        listImages.setHasFixedSize(true)
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(applicationContext, 3)
        listImages.layoutManager = layoutManager

        val adapter = ImageAdapter(testImageIdList())
        listImages.adapter = adapter
    }
}