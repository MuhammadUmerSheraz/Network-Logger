
# Network Logger

Network Logger is a comprehensive yet lightweight debugging tool for testing team(QA). Developed in Kotlin, this library captures critical information for every network request, including headers, content type, HTTP method, API name, parameters, API response, HTTP code, and a ready-to-copy curl request. It empowers users with the ability to:
* Copy Complete Requests & Responses: Easily share or analyze network calls with copy-to-clipboard functionality.
* View Beautiful JSON Trees: Automatically format JSON responses into an intuitive tree view, enhancing readability.
* Toggle Dark/Light Modes: Seamlessly switch between dark and light themes to match your app's design.
* Access Logs with a Shake Gesture: Instantly bring up the network logs screen by simply shaking your device.



## Installation

Step 1. Add the JitPack repository to your build file

Add it in your root build.gradle at the end of repositories:

```bash
 dependencyResolutionManagement {
		repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
		repositories {
			mavenCentral()
			maven { url 'https://jitpack.io' }
		}
	}
```
Step 2. Add the dependency

```bash
 	dependencies {
	        implementation 'com.github.MuhammadUmerSheraz:Network-Logger:1.0.9'
	}
```

## Usage/Examples
Step 1. Initialize it in the App.class
```Koltin
 NetworkLogger.initialize(this) 
```
Full Example
```Koltin
 package com.umer.networklogger

import android.app.Application
import umer.sheraz.shakelibrary.NetworkLogger

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        NetworkLogger.initialize(this) // Initialize it in the Application class
    }
}
```

Step 2. Add "ApiLoggingInterceptor" in tne Retrofit Cilent
```Koltin
  .addInterceptor(ApiLoggingInterceptor())
```
Full Example
```Koltin
  fun getRetrofit(): Retrofit {
        val client = OkHttpClient.Builder()
            .addInterceptor(ApiLoggingInterceptor())
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

```


## Tech Stack

**Front End:** XML

**Backend:** Koltin


## Screenshots

![App Screenshot](https://drive.google.com/uc?export=view&id=18ToUnc15aQEjba_pOIOla0vrnI5fbBT8)
![App Screenshot](https://drive.google.com/uc?export=view&id=1hrZEsFoFr-mYZhN-rUXNfT6DZoZzrV_V)


## Demo

https://drive.google.com/file/d/1V9Om75_nEPRfD3eL370NB4002RFBs5cw/view?usp=sharing


## 🚀 About Me
I am senior software engineer have 8+ year experience and developed 100+ android application with additional experience of backend API, database, server management , team management and project management.
Skills:
Android | Kotlin I Clean Architecture | MVVM | Jetpack I
Java | Payments Gateway | Firebase | Fintech | Web Services Integration | Google Maps | Compose | KMM | MongoDB | Ads | Play Store | Sockot.IO


## 🔗 Links
[![portfolio](https://img.shields.io/badge/my_portfolio-000?style=for-the-badge&logo=ko-fi&logoColor=white)](http://muhammadumersheraz.com)

[![linkedin](https://img.shields.io/badge/linkedin-0A66C2?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/muhammad-umer-sheraz)



## Support

For support, email muhammadumersheraz@gmail.com


## License

[MIT](https://choosealicense.com/licenses/mit/)

