package com.mishbanya.effectivemobiletest2.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mishbanya.effectivemobiletest2.R
import com.mishbanya.effectivemobiletest2data.courses.model.CourseModel
import com.mishbanya.effectivemobiletest2domain.courses.usecases.IOnCourseClickListener
import com.mishbanya.effectivemobiletest2domain.courses.usecases.IOnFavoriteClickListener
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class CoursesAdapter @Inject constructor(
) : ListAdapter<CourseModel, CoursesAdapter.VacancyViewHolder>(VacancyDiffCallback()) {

    private lateinit var context: Context
    private lateinit var listener: IOnCourseClickListener
    private lateinit var favListener: IOnFavoriteClickListener

    fun setContextAndListener(context: Context, listener: IOnCourseClickListener, favListener: IOnFavoriteClickListener) {
        this.context = context
        this.listener = listener
        this.favListener = favListener
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): VacancyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.course_item, parent, false)
        return VacancyViewHolder(view)
    }

    override fun onBindViewHolder(holder: VacancyViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }
    inner class VacancyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val coursePreview : ConstraintLayout = itemView.findViewById(R.id.course_preview)
        private val courseIsFavorite : ImageView = itemView.findViewById(R.id.course_is_favorite)
        private val courseRating : TextView = itemView.findViewById(R.id.course_rating)
        private val courseTime : TextView = itemView.findViewById(R.id.course_time)
        private val courseTitle : TextView = itemView.findViewById(R.id.course_title)
        private val courseDesc : TextView = itemView.findViewById(R.id.course_desc)

        private val courseButton : Button = itemView.findViewById(R.id.course_button)
        private val coursePrice : TextView = itemView.findViewById(R.id.course_price)

        init {
            courseIsFavorite.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    favListener.onIsFavoriteClick(position)
                }
            }
        }

        fun bind(data: CourseModel){
            data.cover?.let { setPreview(it) }
            courseTitle.text = data.title
            courseRating.text = data.rating?.toString() ?: "5.0"
            courseTime.text = data.becamePublishedAt?.let { date ->
                val formatter = SimpleDateFormat("dd MMM", Locale.getDefault())
                formatter.format(date)
            } ?: "Unknown date"
            val rawText: String = Html.fromHtml(data.description, Html.FROM_HTML_MODE_LEGACY).toString()
            courseDesc.text = rawText.trim()
            coursePrice.text = data.price?.toString() ?: "Бесплатно"

            if (data.isFavorite){
                courseIsFavorite.setImageResource(R.drawable.bookmark_fill)
            }else{
                courseIsFavorite.setImageResource(R.drawable.bookmark)
            }


            courseButton.setOnClickListener {
                listener.onCourseClick(data)
            }
        }

        private fun setPreview(url: String){
            Picasso.get()
                .load(url)
                .into(object : Target {
                    override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                        val drawable = BitmapDrawable(context.resources, bitmap)
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
    }

    class VacancyDiffCallback : DiffUtil.ItemCallback<CourseModel>()  {
        override fun areItemsTheSame(oldItem: CourseModel, newItem: CourseModel): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: CourseModel, newItem: CourseModel): Boolean {
            return oldItem == newItem
        }
    }

    fun reload(data: List<CourseModel>){
        submitList(data)
    }
}