package com.example.thesis.config

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.example.coroutineskit.viewmodel.CoroutinesViewModel
import com.example.kitprotocol.protocol.KitViewModel
import com.example.kitprotocol.protocol.KitViewModelFactory
import com.example.rxjavakit.viewmodel.RxJavaViewModel

object MainConfig {

    /**
     * Defines whether to use Kotlin Coroutines or RxJava for asynchronous tasks.
     */

    private val library: Library = Library.RxJava

    /**
     * If true, the app will use mocked values for network calls.
     */

    private const val useMockedNetwork: Boolean = false

    /**
     * Provides the correct ViewModel according to [library] and [useMockedNetwork].
     */

    fun getViewModel(viewModelStoreOwner: ViewModelStoreOwner, application: Application): KitViewModel {
        val clazz = if (library == Library.Coroutines) CoroutinesViewModel::class.java else RxJavaViewModel::class.java
        return ViewModelProvider(viewModelStoreOwner, KitViewModelFactory(application, useMockedNetwork)).get(clazz)
    }
}