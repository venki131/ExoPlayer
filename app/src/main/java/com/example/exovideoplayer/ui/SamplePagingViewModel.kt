package com.example.exovideoplayer.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.exovideoplayer.domain.repository.SamplePagingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import javax.inject.Inject

@HiltViewModel
class SamplePagingViewModel @Inject constructor(
    private val repository: SamplePagingRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        const val KEY_LIST = "list"
        const val DEFAULT_LIST = "androiddev"
    }

    init {
        if (!savedStateHandle.contains(KEY_LIST)) {
            savedStateHandle.set(KEY_LIST, DEFAULT_LIST)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val pageList = savedStateHandle.getLiveData<String>(KEY_LIST)
        .asFlow()
        .flatMapLatest { repository.listOfPagingData(20) }
        .cachedIn(viewModelScope)

}