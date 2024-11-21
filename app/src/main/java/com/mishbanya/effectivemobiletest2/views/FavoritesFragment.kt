package com.mishbanya.effectivemobiletest2.views

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import by.kirich1409.viewbindingdelegate.viewBinding
import com.mishbanya.effectivemobiletest2.R
import com.mishbanya.effectivemobiletest2.databinding.FragmentFavoritesBinding
import com.mishbanya.effectivemobiletest2.adapters.CoursesAdapter
import com.mishbanya.effectivemobiletest2domain.viewmodels.FavoritesViewModel
import com.mishbanya.effectivemobiletest2domain.courses.usecases.IOnCourseClickListener
import com.mishbanya.effectivemobiletest2domain.courses.usecases.IOnFavoriteClickListener
import com.mishbanya.effectivemobiletest2domain.main.usecase.FragmentChangeListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FavoritesFragment : Fragment(), IOnCourseClickListener, IOnFavoriteClickListener {
    private val binding by viewBinding(FragmentFavoritesBinding::bind)
    private lateinit var favoritesViewModel: FavoritesViewModel

    private lateinit var favoriteCoursesListener: FragmentChangeListener
    private lateinit var favoriteCoursesRecyclerView: RecyclerView
    @Inject
    lateinit var favoriteCoursesAdapter: CoursesAdapter

    override fun onAttach(context: Context) {
        super.onAttach(context)
        favoriteCoursesListener = context as FragmentChangeListener
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_favorites, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d("Hilt", "Creating favoritesViewModel client instance")
        favoritesViewModel = ViewModelProvider(this)[FavoritesViewModel::class.java]

        initRecyclerView()
        initFavoritesViewModel()
        initObserversFavoritesViewModel()

        loadCourses()
    }

    override fun onCourseClick(position: Int) {
        favoriteCoursesListener.onCourseClicked(position)
    }

    override fun onIsFavoriteClick(position: Int) {
        favoritesViewModel.changeFavoriteness(position)
    }
    private fun initRecyclerView(){
        favoriteCoursesRecyclerView = binding.favoriteCoursesRv
        favoriteCoursesRecyclerView.layoutManager = LinearLayoutManager(context)
        favoriteCoursesAdapter.setContextAndListener(requireContext(), this, this)
        favoriteCoursesRecyclerView.adapter = favoriteCoursesAdapter
    }
    private fun initFavoritesViewModel() {
        Log.d("Hilt", "Creating FavoritesModel client instance")
        favoritesViewModel = ViewModelProvider(this)[FavoritesViewModel::class.java]
    }
    private fun initObserversFavoritesViewModel() {
        favoritesViewModel.favoriteVacancies.observe(viewLifecycleOwner){ data->

            if (data != null) {
                if (data.isEmpty()){
                    activity?.let { showToast(it.getString(R.string.no_courses)) }
                }
                favoriteCoursesAdapter.reload(data)
            }
            else{
                showError()
            }
        }
    }
    private fun loadCourses(){
        favoritesViewModel.getCourses()
    }
    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    private fun showError(){
        activity?.let {
            showToast(it.getString(R.string.error))
        }
    }

}