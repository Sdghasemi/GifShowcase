# GIF Showcase
An application to view and search GIFs from [Giphy API](https://developers.giphy.com/docs/). The first screen displays a random GIF every 10 seconds with title, URL, and a rating badge. Clicking on the search box above the screen enables you to search for your desired GIFs and selecting one from that screen takes you to the GIF details with its title, URL, and rating badge.
    
## Architecture and patterns
- MVI    
- Dependency Injection    
- Repository Pattern    
    
## Principles
- Clean Code    
- SOLID    
    
## Libraries
- Basic test and AppCompat libraries such as: JUnit, Espresso, Google Material, ConstraintLayout, SwipeRefreshLayout, Lifecycle (LiveData, ViewModels, and SavedState), Coroutines, etc.    
- Retrofit with OkHttp (Coroutines friendly, up to date, and simple REST API call management)
- Koin for DI (Kotlin friendly, lightweight, and less boilerplate)
- Glide (Handles both GIFs and image loading, caching, focused on smooth scrolling)
    
## Docs    
The application is written in pure Kotlin and using MVI architecture with unidirectional data flows, since it fits the scale of the application, improves testability due to separation of concerns, and performs better in lifecycle changes and maintaining the app state. It also helps to better understand the clear and consistent flow of data through the application.

## Tests    
I've included some unit tests as well as UI tests making sure the view states and timer working as they should and the items appear on the screen with correct data.    
    
## Build    
The project is built upon AGP 8.1.1. I'm using Kotlin 1.9.10 and Gradle 8.3 via wrapper. [A pre-built APK](app-debug.apk) is placed in the root of the project for your convenience.
    
### Contact developer    
    
If there's ***anything*** you'd like to discuss, feel free to contact me at [Sd.ghasemi1@gmail.com](mailto:Sd.ghasemi1@gmail.com).    
    
Cheers🍻