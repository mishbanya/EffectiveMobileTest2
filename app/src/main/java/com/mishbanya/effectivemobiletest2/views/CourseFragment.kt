package com.mishbanya.effectivemobiletest2.views

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.mishbanya.effectivemobiletest2.R
import com.mishbanya.effectivemobiletest2data.courses.model.CourseModel
import com.mishbanya.effectivemobiletest2domain.courses.repository.ILinkOpener
import com.mishbanya.effectivemobiletest2domain.courses.repositoryimpl.LinkOpenerImpl
import com.mishbanya.effectivemobiletest2domain.main.usecase.FragmentChangeListener
import com.mishbanya.effectivemobiletest2domain.viewmodels.CourseViewModel
import com.mishbanya.effectivemobiletest2domain.viewmodels.FavoritesViewModel
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class CourseFragment(
    private val data: CourseModel
) : Fragment() {
    private lateinit var courseViewModel: CourseViewModel
    private lateinit var changeListener: FragmentChangeListener
    private val linkOpener: ILinkOpener = LinkOpenerImpl()
    override fun onAttach(context: Context) {
        super.onAttach(context)
        changeListener = context as FragmentChangeListener
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_course, container, false)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("Hilt", "Creating coursesViewModel client instance")
        courseViewModel = ViewModelProvider(this)[CourseViewModel::class.java]

        setupUI(view)
    }

    private fun loadCourse(data: CourseModel) {
        courseViewModel.load(data)
    }

    private fun setupUI(view: View) {
        data.cover?.let { setPreview(it) }
        val courseTitle = view.findViewById<TextView>(R.id.course_title)
        val courseDesc = view.findViewById<TextView>(R.id.course_desc)
        val courseTime = view.findViewById<TextView>(R.id.course_time)
        val courseStartButton = view.findViewById<Button>(R.id.course_start)
        val coursesBackButton = view.findViewById<ImageView>(R.id.button_back)

        coursesBackButton.setOnClickListener{
            changeListener.onBackToMainClicked()
        }

        courseTitle.text = data.title
        courseDesc.text = data.description
        courseTime.text = data.becamePublishedAt?.let { date ->
            val formatter = SimpleDateFormat("dd MMM", Locale.getDefault())
            formatter.format(date)
        } ?: "Unknown date"
        val rawText: String = Html.fromHtml(data.description, Html.FROM_HTML_MODE_LEGACY).toString()
        courseDesc.text = rawText.trim()

        courseStartButton.setOnClickListener {
            data.url?.let { it1 -> linkOpener.offerLinkOpen(it1, requireContext()) } ?: showToast("Ссылка пустая")
        }
    }
    private fun setPreview(url: String){
        val coursePreview : ConstraintLayout = requireView().findViewById(R.id.course_preview)
        Picasso.get()
            .load(url)
            .into(object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                    val drawable = BitmapDrawable(requireContext().resources, bitmap)
                    coursePreview.background = drawable
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    coursePreview.background = errorDrawable
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    coursePreview.background = placeHolderDrawable
                }
            })
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}