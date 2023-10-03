package com.example.exovideoplayer.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.exovideoplayer.domain.repository.SamplePagingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class SamplePagingViewModel @Inject constructor(
    private val repository: SamplePagingRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val pageList = savedStateHandle.getLiveData<String>("photos")
        .asFlow()
        .flatMapLatest { repository.listOfPagingData(20) }
        .cachedIn(viewModelScope)

    val pageListLiveData = MutableLiveData<String>() // Assuming you have a specific type for the data
    val pageListFlow: Flow<PagingData<String>> = pageListLiveData.asFlow()
        .flatMapLatest { repository.listOfPagingData(20) }
        .cachedIn(viewModelScope)

    fun updatePageList(newData: String) {
        pageListLiveData.value = newData
    }

}