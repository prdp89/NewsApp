# NewsApp

This Android project is built with MVVM architecture using [Android Jetpack components](https://developer.android.com/jetpack) 

## Package-Structure:

This Project contains mainly three components :

	
        1. DataSource - This package provides data source to the App includes Repository, Network Layer and Room DB.
	
        2. DI - This package contains Dagger v2.0 Modules and Components used to provide dependency at runtime.
	
        3. View/ViewModel - This layer provides interaction between View and ViewModel using Data Binding approach.
		
		4. ImageCache : A custom ImageCache provider with Async ImageLoader.

## Dependencies:

To build this App following third party dependencies have been used:
	
		1. Retrofit 2.7 : Use to make rest Api call with Suspend function.
		
		2. Dagger 2.0: Use to Inject dependency of various components.
		
		3. Coroutine: A lightweight Worker Thread that helps in achieiving parallelism.

## Notes :

		1. Shimmer Layout has been used to show the loading state of App.
	
		2. Created 2 UI tests using Espresso Instrumentation.
		
##### About DEV :
[Linkedin](https://www.linkedin.com/in/pardeep-sharma-dev/)
[Email](mailto:Pardeepsharma.dev@gmail.com?subject=[NewsApp])
