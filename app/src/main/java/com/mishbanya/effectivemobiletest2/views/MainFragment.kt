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
import com.mishbanya.effectivemobiletest2.adapters.CoursesAdapter
import com.mishbanya.effectivemobiletest2.databinding.FragmentMainBinding
import com.mishbanya.effectivemobiletest2.viewmodels.MainFragmentViewModel
import com.mishbanya.effectivemobiletest2domain.courses.usecases.IOnCourseClickListener
import com.mishbanya.effectivemobiletest2domain.courses.usecases.IOnFavoriteClickListener
import com.mishbanya.effectivemobiletest2domain.main.usecase.FragmentChangeListener
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment @Inject constructor(
    private val coursesAdapter: CoursesAdapter
) : Fragment(), IOnFavoriteClickListener, IOnCourseClickListener {

    private val binding by viewBinding(FragmentMainBinding::bind)
    private lateinit var mainViewModel: MainFragmentViewModel

    private lateinit var searchListener: FragmentChangeListener

    private lateinit var coursesRecyclerView: RecyclerView

    override fun onAttach(context: Context) {
        super.onAttach(context)
        searchListener = context as FragmentChangeListener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        initSearchViewModel()
        initObserversSearchViewModel()

        loadCourses()
    }
    private fun loadCourses(){
        mainViewModel.getCourses()
    }

    override fun onCourseClick(position: Int) {
        searchListener.onCourseClicked(position)
    }

    override fun onIsFavoriteClick(position: Int) {
        mainViewModel.changeFavoriteness(position)
    }
    private fun initRecyclerView(){
        coursesRecyclerView = binding.coursesRv
        coursesRecyclerView.layoutManager = LinearLayoutManager(context)
        coursesAdapter.setContextAndListener(requireContext(), this)
        coursesRecyclerView.adapter = coursesAdapter
    }
    private fun initSearchViewModel() {
        Log.d("Hilt", "Creating SearchViewModel client instance")
        mainViewModel = ViewModelProvider(this)[MainFragmentViewModel::class.java]
    }
    private fun initObserversSearchViewModel() {
        mainViewModel.courses.observe(viewLifecycleOwner){ data->

            if (data != null) {
                if (data.isEmpty()){
                    activity?.let { showToast(it.getString(R.string.no_courses)) }
                }
                coursesAdapter.reload(data)
            }
            else{
                showError()
            }
        }
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