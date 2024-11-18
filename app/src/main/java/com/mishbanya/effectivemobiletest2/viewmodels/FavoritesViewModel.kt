package com.mishbanya.effectivemobiletest2.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mishbanya.effectivemobiletest2.domain.common.repository.IMultipleLangRepository
import com.mishbanya.effectivemobiletest2.domain.common.repository.IOffersAndVacanciesRepository
import com.mishbanya.effectivemobiletest2.domain.courses.entity.VacancyModel
import com.mishbanya.effectivemobiletest2.domain.courses.repository.IChangeVacancyFavoritenessRepository
import com.mishbanya.effectivemobiletest2.domain.courses.repository.IVacanciesGetterRepository
import com.mishbanya.effectivemobiletest2.domain.courses.repository.IVacanciesSaverRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class FavoritesViewModel @Inject constructor(
    private val offersAndVacanciesRepository: IOffersAndVacanciesRepository,
    private val vacanciesGetterRepositoryImpl: IVacanciesGetterRepository,
    private val vacanciesSaverRepositoryImpl: IVacanciesSaverRepository,
    private val changeVacancyFavoritenessRepository: IChangeVacancyFavoritenessRepository,
    private val multipleLangRepository: IMultipleLangRepository
) :ViewModel() {
    private val disposables = CompositeDisposable()
    private val _vacancies = MutableLiveData<List<VacancyModel>?>()
    private val _favoriteVacancies = MutableLiveData<List<VacancyModel>?>()

    val favoriteVacancies: MutableLiveData<List<VacancyModel>?>
        get() = _favoriteVacancies
    val vacancies: MutableLiveData<List<VacancyModel>?>
        get() = _vacancies

    private fun setVacancies(vacancies: List<VacancyModel>?) {
        if (vacancies != null) {
            _vacancies.value = vacancies
            _favoriteVacancies.value = vacancies.filter { it.isFavorite }
            if (_vacancies.value != tryGettingVacanciesFromSP()) {
                if (saveVacanciesInSP(vacancies)) {
                    Log.d("VacanciesSaverRepository", "vacancies saved")
                }
            }
        }
    }
    private fun tryGettingVacanciesFromSP(): List<VacancyModel>? {
        val recievedVacancies = getVacanciesFromSP()
        if (!recievedVacancies.isNullOrEmpty()) {
            return recievedVacancies
        }
        return null
    }

    fun getVacancies(){
        if(!tryGettingVacanciesFromSP().isNullOrEmpty()){
            setVacancies(tryGettingVacanciesFromSP())
            return
        }
        disposables.clear()
        val disposable = offersAndVacanciesRepository.getOffersAndVacancies()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ response ->
                if (response.isSuccessful) {
                    response.body()?.let { setVacancies(it.vacancies) }
                } else {
                    Log.e("getOffersAndVacancies", "getOffersAndVacancies failed: ${response.code()}")
                    setVacancies(null)
                }
            }, { error ->
                Log.e("getOffersAndVacancies", "getOffersAndVacancies failed", error)

                if (error is HttpException) {
                    Log.d("getOffersAndVacancies", "HTTP Error: ${error.code()}")
                } else {
                    Log.d("getOffersAndVacancies", "Error: ${error.message}")
                }
                setVacancies(null)
            })
        disposables.add(disposable)
    }
    fun countMultipleVacancies(count: Int): String{
        return multipleLangRepository.multipleVacanciesLang(count)
    }
    fun changeFavoriteness(position: Int){
        val vacancyList = _favoriteVacancies.value?.toList()
        vacancyList?.get(position)?.let { changeVacancyFavoritenessRepository.changeFavoriteness(it.id) }
        setVacancies(getVacanciesFromSP())
    }
    private fun saveVacanciesInSP(vacancies: List<VacancyModel>?): Boolean {
        return vacanciesSaverRepositoryImpl.saveVacancies(vacancies)
    }
    private fun getVacanciesFromSP(): List<VacancyModel>?{
        return vacanciesGetterRepositoryImpl.getVacancies()
    }
}