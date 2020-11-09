# Thesis 🎓
Thesis is an Android application that has served as a case-study application for a thesis. This thesis aimed to compare the use of **Kotlin Coroutines** and **RxJava** in regards to how they affect the software quality attributes of an Android application, specifically **Performance** and **Maintainability**.
This application fetches movies from [TMDB API](https://www.themoviedb.org/) and provides a brief synopsis on the movie as well as the possibility to watch its trailer. You can have either a list of trending movies or a list of movies that are currently playing in your location (the app uses GPS to track the user location). The features for this application will either use Kotlin Coroutines or RxJava for the concurrency bits, this can be tweaked in code by changing the `MainConfig.kt` file.

## Architecture ✏️
Thesis follows Model-View-ViewModel (MVVM) as the architectural pattern and the repository pattern for data access. It is a multi-module application where the main goal was to fully isolate concurrency code from what can be common between the use of Kotlin Coroutines and RxJava.

### Modules
 - **App** : Application's user interface
 - **CoroutinesKit** : Concurrency code written with Kotlin Coroutines
 - **RxJavaKit** : Concurrency code written with RxJava
 - **KitProtocol** : Shared components between the App, CoroutinesKit and RxJava kit
 - **Benchmark** : Benchmark tests for both Kotlin Coroutines and RxJava

## Stack 🧠

 - *Kotlin*
 - *Kotlin Coroutines & Flow*
 - *RxJava 2*
 - *Room database*
 - *Retrofit*
 - *Architecture Components*
 - *Jetpack Benchmark library*

