package com.example.exovideoplayer.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.exovideoplayer.domain.repository.SamplePagingRepository
import kotlinx.coroutines.flow.flatMapLatest

class SamplePagingViewModel(
    private val repository: SamplePagingRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val pageList = savedStateHandle.getLiveData<String>("photos")
        .asFlow()
        .flatMapLatest { repository.listOfPagingData(20) }
        .cachedIn(viewModelScope)
}