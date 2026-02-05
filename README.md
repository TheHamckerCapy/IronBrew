# **🍺 Brewery \- Modern Android Beer App**

A robust, offline-first Android application built with **Kotlin** and **Jetpack Compose**. This project demonstrates modern Android development practices, featuring paginated data caching, custom UI animations, text-to-speech integration.

## **📱 Features**

* **Offline-First Pagination:** Seamless infinite scrolling using **Paging 3** with a **RemoteMediator** that caches data into a **Room** database.  
* **Favorites System:** Save your favorite brews locally to access them anytime.  
* **Text-to-Speech (TTS):** Listen to beer descriptions with a built-in accessibility player.  
* **Custom Navigation:** A custom-built **Animated Bottom Navigation Bar** using Canvas and Spring animations.

## **🛠 Tech Stack & Libraries**

* **Language:** [Kotlin](https://kotlinlang.org/)  
* **UI:** [Jetpack Compose](https://developer.android.com/jetpack/compose) (Material3)  
* **Architecture:** MVVM (Model-View-ViewModel) with Unidirectional Data Flow (MVI pattern for Events/State)  
* **Dependency Injection:** [Dagger Hilt](https://dagger.dev/hilt/)  
* **Network:** [Retrofit](https://square.github.io/retrofit/) & [Moshi](https://github.com/square/moshi)  
* **Local Storage:** [Room Database](https://developer.android.com/training/data-storage/room)  
* **Pagination:** [Paging 3](https://www.google.com/search?q=https://developer.android.com/topic/libraries/architecture/paging/v3)  
* **Image Loading:** [Coil](https://coil-kt.github.io/coil/)  
* **Asynchronous:** Coroutines & Flow  
* **Navigation:** Jetpack Navigation Compose

## **🏗 Architecture Overview**

The app follows the **Repository Pattern** and **Clean Architecture** principles.

* **Data Layer:**  
  * **Remote:** Retrofit service fetching data from the API.  
  * **Local:** Room database acting as the single source of truth.  
  * **Mediator:** BeerRemoteMediator coordinates fetching data from the network and saving it to the DB for the Paging library.  
* **Domain Layer:**  
  * Contains pure data classes (Beer) and Mappers.  
  * Resource sealed class handles UI states (Success, Error, Loading) for detail operations.  
* **UI Layer:**  
  * **ViewModels:** Handle business logic and expose StateFlow to Composables. Use a robust onEvent pattern for user interactions.  
  * **Composables:** Stateless UI components that react to state changes.

## **🚀 Getting Started**

1. **Clone the repository:** 
2. **Open in Android Studio:**  
   Ensure you are using the latest version of Android Studio (Koala or newer recommended).  
3. **Configure API:**  
   * Navigate to Constant.  
   * Update the IMAGE\_BASE\_URL constant if you are hosting the images locally or on a different server.

private const val IMAGE\_BASE\_URL \= "\[https://your-api-host.com/images\](https://your-api-host.com/images)"

4. **Run the App:**  
   Connect your device or start an emulator and run the application.

## **📂 Project Structure**

com.yourpackage  
├── data  
│   ├── local         \# Room Entities, DAOs, TypeConverters  
│   └── remote        \# Retrofit Service, RemoteMediator  
├── di                \# Hilt Modules  
├── domain            \# Models, Repository Interface, Mappers  
├── ui  
│   ├── components    \# Shared UI (AnimatedNavBar, etc.)  
│   ├── detail        \# Detail Screen & ViewModel  
│   ├── favorites     \# Favorites Screen & ViewModel  
│   ├── home          \# Home Screen & ViewModel  
│   ├── navigation    \# NavHost & Scaffold Wrapper  
│   └── theme         \# Compose Theme  
└── util              \# Helpers (TTS, NetworkObserver, Resource)

## **📸 Screenshots**

*(Add your screenshots here)*

## **🤝 Contribution**

Contributions are welcome\! Please fork the repository and submit a pull request for any bug fixes or feature enhancements.

## **📄 License**

This project is open-source and available under the [MIT License](https://www.google.com/search?q=LICENSE).
