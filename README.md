# StoriTechnicalTest

## Challenge to Meet

A banking application must be made that allows user registration, user login, and bank information such as balance, list of movements made, and details of the movements.

### The application must comply with the following points:

#### Registration [✓] 
An Onboarding process must be carried out that consists of the following screens:
- **User Personal Data**: name, surname, email, and password.
- **Take Identification Photo**
- **Success Screen**

#### User Login [✓]
A user login screen must be created with the following options:
- **Email**
- **Password**
- **Login Button**
- **Registration Button**: which will take the user to the beginning of the Onboarding process.

#### Home [✓] 
This will be the screen that shows the information of the user's bank details:
- **Balance Information**
- **List of Movements Made**
- **Transaction Details**

When clicking on a movement, the user must be sent to the details of the movement.

#### Architecture
Used MVVM Architecture usign DataSource, Repository, ViewModel, Compose UI.

#### Frameworks
- DI: Hilt
- Database and Storage: Firestore and Firebase Storage(upload ID photo)
- Auth with FirebaseAuth
- Compose for UI
- Kotlin Coroutines
- junit/mockito/coroutines test for Unit test(Repo Layer)

#### TODO/To improve
- Add more Unit and UI tests
- Handle errors between layer with generic Result class to propagate the correct error message for example when user already created, no internet etc(time consuming for a take home project)

## Demo

https://github.com/bivach/StoriTechnicalTest/assets/20892810/929b74f7-2b29-4ab4-9bb0-6b6b23a8872f




