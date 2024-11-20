package com.mishbanya.effectivemobiletest2.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mishbanya.effectivemobiletest2domain.courses.repository.IChangeCourseFavoritenessRepository
import com.mishbanya.effectivemobiletest2domain.courses.repository.ICoursesGetterRepository
import com.mishbanya.effectivemobiletest2domain.courses.repository.ICoursesRepository
import com.mishbanya.effectivemobiletest2domain.courses.repository.ICoursesSaverRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class MainFragmentViewModel @Inject constructor(
    private val coursesRepository: ICoursesRepository,
    private val coursesGetterRepositoryImpl: ICoursesSaverRepository,
    private val coursesSaverRepositoryImpl: ICoursesGetterRepository,
    private val changeCourseFavoritenessRepository: IChangeCourseFavoritenessRepository
) :ViewModel() {
    private val disposables = CompositeDisposable()

    private val _courses = MutableLiveData<List<CourseModel>?>()

    val courses: MutableLiveData<List<CourseModel>?>
        get() = _courses
    private fun setResponse(responseData: ResponseData?) {
        if (responseData != null) {
            _offers.value = responseData.offerModels
            if (_courses.value.isNullOrEmpty()) {
                _courses.value = responseData.vacancies
                if(saveVacancies(responseData.vacancies)){
                    Log.d("VacanciesSaverRepository", "vacancies saved")
                }
            }
        }
    }
    private fun setVacancies(vacancies: List<VacancyModel>?) {
        if (vacancies != null) {
            _courses.value = vacancies
        }
    }
    private fun tryGettingVacanciesFromSP(){
        val recievedVacancies = getVacancies()
        if (!recievedVacancies.isNullOrEmpty()) {
            _courses.value = recievedVacancies
        }
    }

    fun getCourses(){
        tryGettingVacanciesFromSP()
        disposables.clear()
        val disposable = coursesRepository.getOffersAndVacancies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                if (response.isSuccessful) {
                    response.body()?.let { setResponse(it) }
                } else {
                    Log.e("getOffersAndVacancies", "getOffersAndVacancies failed: ${response.code()}")
                    setResponse(null)
                }
            }, { error ->
                Log.e("getOffersAndVacancies", "getOffersAndVacancies failed", error)

                if (error is HttpException) {
                    Log.d("getOffersAndVacancies", "HTTP Error: ${error.code()}")
                } else {
                    Log.d("getOffersAndVacancies", "Error: ${error.message}")
                }
                setResponse(null)
            })
        disposables.add(disposable)
    }
    fun countMultipleVacancies(count: Int): String{
        return multipleLangRepository.multipleVacanciesLang(count)
    }
    private fun saveVacancies(vacancies: List<VacancyModel>?): Boolean {
        return coursesSaverRepositoryImpl.saveVacancies(vacancies)
    }
    private fun getVacancies(): List<VacancyModel>?{
        return coursesGetterRepositoryImpl.getVacancies()
    }
    fun changeFavoriteness(position: Int){
        val vacancyList = _courses.value?.toList()
        vacancyList?.get(position)?.let { changeCourseFavoritenessRepository.changeFavoriteness(it.id) }
        setVacancies(getVacancies())
    }
    fun offerClick(context: Context, position: Int) {
        val offerList = _offers.value?.toList()
        if (offerList != null) {
            val link = offerList[position].link
            offerLinkOpenerRepository.offerLinkOpen(link, context)
        }
    }

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}