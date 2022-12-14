package com.enesselcuk.watchmovies.ui.homeFragment

import androidx.lifecycle.*
import com.enesselcuk.watchmovies.domain.MoviesRepos
import com.enesselcuk.watchmovies.source.remote.response.categorys.MoviesResponse
import com.enesselcuk.watchmovies.util.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repos: MoviesRepos
) : ViewModel() {
    private val _moviesTrendingState = MutableStateFlow(HomeUiState())
    val moviesTrendingState: StateFlow<HomeUiState> = _moviesTrendingState

    private val _allMovies: MutableStateFlow<HomeUiState> =
        MutableStateFlow(HomeUiState())
    val allMovies: StateFlow<HomeUiState> = _allMovies

    fun getTrendingMovies() {
        viewModelScope.launch {
            repos.getTrending(PAGE).collectLatest {
                moviesUiState(it)
            }
        }
    }

    fun getLivingToken() {
        viewModelScope.launch {
            repos.pagerShuffle(PAGE)
                .collect { networkResult ->
                    moviesTrendUiState(networkResult)
                }
        }
    }

    companion object {
        const val PAGE = 1
    }

    private fun moviesUiState(networkResult: NetworkResult<MoviesResponse>) {
        when (networkResult) {
            is NetworkResult.Success -> {
                _allMovies.update { pagerUi ->
                    pagerUi.copy(
                        moviesResponse = networkResult.data,
                        isLoading = true
                    )
                }
            }
            is NetworkResult.Error -> {
                _allMovies.update { pagerUi ->
                    pagerUi.copy(
                        isError = networkResult.message,
                        isLoading = true
                    )
                }
            }
            is NetworkResult.Loading -> {
                _allMovies.update { pagerUi ->
                    pagerUi.copy(
                        isLoading = false
                    )
                }
            }
        }
    }


    private fun moviesTrendUiState(networkResult: NetworkResult<MoviesResponse>) {
        when (networkResult) {
            is NetworkResult.Success -> {
                _moviesTrendingState.update { pagerUi ->
                    pagerUi.copy(
                        moviesResponse = networkResult.data,
                        isLoading = false
                    )
                }
            }
            is NetworkResult.Error -> {
                _moviesTrendingState.update { pagerUi ->
                    pagerUi.copy(
                        isError = networkResult.message,
                        isLoading = false
                    )
                }
            }
            is NetworkResult.Loading -> {
                _moviesTrendingState.update { pagerUi ->
                    pagerUi.copy(
                        isLoading = true
                    )
                }
            }
        }
    }


}