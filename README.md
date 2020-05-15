## Screenshots:
![Get Started](https://drive.google.com/uc?export=view&id=1_XgozYN3qI2M8ZhF9NnUUcrwSCZb9kcV)


![Main Activity](https://drive.google.com/uc?export=view&id=1UF5IwdxIyblhAJkoU9Sqc25JvGhA_D3q)


![Covid Data](https://drive.google.com/uc?export=view&id=1jOFQEZk_lcx_viIurLqPyQ3BuSl_YD_m)

## Features:
  - Working: When two people come close then this contact would be capture, in future if one of them comes in contact of COVID-19 then other wiil get notified.
  - Data: data of all India level and the current state would be shown.
  - Emergency services in the city would be shown based on the current location.
  - General instructions to avoid are depicted in form of images.
  - Data throuh graph is also displayed for better understanding.
  - Data is updated in every 6 hrs

## Technical Details:
  - Mobile auth is used
  - Firebase is used as backend
  - Firebase services used: Realtime database, FCM, Authentication
  - Nearby services are used to detect contact
  - Local db caching is provided
  - Best practices has been enforced
    - MVI architecture, dagger2 as dependency injection  is used
	- Coroutines are used for better performance
	- Navigation is used
	- Live Data used for reactive results
	- Room Persistance library is used
	- Retrofit2 for making requests
  - Api Used for fetching data: https://api.covid19india.org/
  - Create you own project on firebase to get google-services.json file or request ours.

## Firebase Setup:
  - Create firebase project and add your SHA1 key and package name
  - Enable Phone Auth in Authentication section firebase
  - Download `google-services.json` file and add to your project.
  - Realtime Database used:
    ```bash
    |-- users
    |  |-- uid
    |    |-- name: String
    |    |-- phone: Number
    |    |-- isPositive: Boolean
    |-- cross_location
      |-- uid1
        |-- uid2
          |-- timestamp: Timestamp(Server TImestamp)
          |-- location
            |--lat: Double
            |-- lang: Double
    ```
