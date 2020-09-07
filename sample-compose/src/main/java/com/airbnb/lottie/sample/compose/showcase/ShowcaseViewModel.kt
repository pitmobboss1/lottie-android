package com.airbnb.lottie.sample.compose.showcase

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.airbnb.lottie.sample.compose.LottieComposeApplication
import com.airbnb.lottie.sample.compose.api.FeaturedAnimationsResponse
import com.airbnb.lottie.sample.compose.api.LottieFilesApi
import com.airbnb.lottie.sample.compose.dagger.AssistedViewModelFactory
import com.airbnb.mvrx.*
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import javax.inject.Inject

data class ShowcaseState(
    val featuredAnimations: Async<FeaturedAnimationsResponse> = Uninitialized
) : MvRxState

class ShowcaseViewModel @AssistedInject constructor(
    @Assisted initialState: ShowcaseState,
    private var api: LottieFilesApi
) : MavericksViewModel<ShowcaseState>(initialState) {
    private val _featuredAnimations = MutableStateFlow(Uninitialized as Async<FeaturedAnimationsResponse>)
    val featuredAnimations: StateFlow<Async<FeaturedAnimationsResponse>> = _featuredAnimations

    init {
        fetchFeatured()
    }

    fun fetchFeatured() {
        viewModelScope.launch(Dispatchers.IO) {
            _featuredAnimations.value = Loading()
            _featuredAnimations.value = try {
                Success(api.getFeatured())
            } catch (e: Throwable) {
                Log.d("Gabe", "fetchFeatured: failed", e)
                Fail(e)
            }
        }
    }

    @AssistedInject.Factory
    interface Factory : AssistedViewModelFactory<ShowcaseViewModel, ShowcaseState> {
        override fun create(initialState: ShowcaseState): ShowcaseViewModel
    }
}