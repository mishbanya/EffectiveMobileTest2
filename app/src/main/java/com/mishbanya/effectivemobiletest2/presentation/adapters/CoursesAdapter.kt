package com.mishbanya.effectivemobiletest2.presentation.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mishbanya.effectivemobiletest2.R
import com.mishbanya.effectivemobiletest2.domain.common.repository.IMultipleLangRepository
import com.mishbanya.effectivemobiletest2.domain.courses.entity.VacancyModel
import com.mishbanya.effectivemobiletest2.domain.courses.usecases.IOnVacancyClickListener
import javax.inject.Inject

class CoursesAdapter @Inject constructor(
    private val multipleLangRepository: IMultipleLangRepository
) : ListAdapter<VacancyModel, CoursesAdapter.VacancyViewHolder>(VacancyDiffCallback()) {

    private lateinit var context: Context
    private lateinit var listener: IOnVacancyClickListener

    fun setContextAndListener(context: Context, listener: IOnVacancyClickListener) {
        this.context = context
        this.listener = listener
    }
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CoursesAdapter.VacancyViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.course_item, parent, false)
        return VacancyViewHolder(view)
    }

    override fun onBindViewHolder(holder: VacancyViewHolder, position: Int) {
        val data = getItem(position)
        holder.bind(data)
    }
    inner class VacancyViewHolder (itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val lookingNumber : TextView = itemView.findViewById(R.id.vacancy_looking_number)
        private val title : TextView = itemView.findViewById(R.id.vacancy_title)
        private val town : TextView = itemView.findViewById(R.id.vacancy_town)
        private val company : TextView = itemView.findViewById(R.id.vacancy_company)
        private val experience : TextView = itemView.findViewById(R.id.vacancy_experience)
        private val publishedDate : TextView = itemView.findViewById(R.id.vacancy_published_date)

        private val vacancyButton : Button = itemView.findViewById(R.id.vacancy_button)
        private val isFavorite : ImageView = itemView.findViewById(R.id.vacancy_is_favorite)

        init {
            vacancyButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onVacancyClick(position)
                }
            }
            isFavorite.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    listener.onIsFavoriteClick(position)
                }
            }
        }

        fun bind(data: VacancyModel){
            title.text = data.title
            town.text = data.addressModel.town
            company.text = data.company
            experience.text = data.experienceModel.previewText

            if(data.lookingNumber!=null) {
                lookingNumber.text = "Сейчас просматривает ${multipleLangRepository.multiplePeopleLang(data.lookingNumber)}"
            }else{
                lookingNumber.visibility = View.INVISIBLE
            }
            publishedDate.text = "Опубликовано ${data.publishedDate}"

            if (data.isFavorite){
                isFavorite.setImageResource(R.drawable.heart_active)
            }else{
                isFavorite.setImageResource(R.drawable.heart_default)
            }
        }
    }

    class VacancyDiffCallback : DiffUtil.ItemCallback<VacancyModel>()  {
        override fun areItemsTheSame(oldItem: VacancyModel, newItem: VacancyModel): Boolean {
            return oldItem.title == newItem.title
        }

        override fun areContentsTheSame(oldItem: VacancyModel, newItem: VacancyModel): Boolean {
            return oldItem == newItem
        }
    }

    fun reload(data: List<VacancyModel>){
        submitList(data)
    }
}