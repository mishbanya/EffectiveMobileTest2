package com.mishbanya.effectivemobiletest2.viewmodels

import androidx.lifecycle.ViewModel
import com.mishbanya.effectivemobiletest2.domain.courses.repository.IVacanciesSaverRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val vacanciesSaverRepositoryImpl: IVacanciesSaverRepository
) : ViewModel() {
    fun clearVacancies(){
        vacanciesSaverRepositoryImpl.saveVacancies(null)
    }
}