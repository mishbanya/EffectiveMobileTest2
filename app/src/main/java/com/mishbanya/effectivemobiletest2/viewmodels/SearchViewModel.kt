package com.mishbanya.effectivemobiletest2.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mishbanya.effectivemobiletest2.domain.common.entity.ResponseData
import com.mishbanya.effectivemobiletest2.domain.common.repository.IMultipleLangRepository
import com.mishbanya.effectivemobiletest2.domain.common.repository.IOffersAndVacanciesRepository
import com.mishbanya.effectivemobiletest2.domain.offers.entity.OfferModel
import com.mishbanya.effectivemobiletest2.domain.offers.repository.IOfferLinkOpenerRepository
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
class SearchViewModel @Inject constructor(
    private val offersAndVacanciesRepository: IOffersAndVacanciesRepository,
    private val offerLinkOpenerRepository: IOfferLinkOpenerRepository,
    private val vacanciesGetterRepositoryImpl: IVacanciesGetterRepository,
    private val vacanciesSaverRepositoryImpl: IVacanciesSaverRepository,
    private val changeVacancyFavoritenessRepository: IChangeVacancyFavoritenessRepository,
    private val multipleLangRepository: IMultipleLangRepository
) :ViewModel() {
    private val disposables = CompositeDisposable()
    private val _offers = MutableLiveData<List<OfferModel>?>()
    private val _vacancies = MutableLiveData<List<VacancyModel>?>()

    val offers: MutableLiveData<List<OfferModel>?>
        get() = _offers

    val vacancies: MutableLiveData<List<VacancyModel>?>
        get() = _vacancies
    private fun setResponse(responseData: ResponseData?) {
        if (responseData != null) {
            _offers.value = responseData.offerModels
            if (_vacancies.value.isNullOrEmpty()) {
                _vacancies.value = responseData.vacancies
                if(saveVacancies(responseData.vacancies)){
                    Log.d("VacanciesSaverRepository", "vacancies saved")
                }
            }
        }
    }
    private fun setVacancies(vacancies: List<VacancyModel>?) {
        if (vacancies != null) {
            _vacancies.value = vacancies
        }
    }
    private fun tryGettingVacanciesFromSP(){
        val recievedVacancies = getVacancies()
        if (!recievedVacancies.isNullOrEmpty()) {
            _vacancies.value = recievedVacancies
        }
    }

    fun getOffersAndVacancies(){
        tryGettingVacanciesFromSP()
        disposables.clear()
        val disposable = offersAndVacanciesRepository.getOffersAndVacancies()
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
        return vacanciesSaverRepositoryImpl.saveVacancies(vacancies)
    }
    private fun getVacancies(): List<VacancyModel>?{
        return vacanciesGetterRepositoryImpl.getVacancies()
    }
    fun changeFavoriteness(position: Int){
        val vacancyList = _vacancies.value?.toList()
        vacancyList?.get(position)?.let { changeVacancyFavoritenessRepository.changeFavoriteness(it.id) }
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