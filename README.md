# AlmaNotes Mobile

AlmaNotes Mobile is the Android app for AlmaNotes, an application designed to facilitate the sharing, searching, and consulting of university notes. 

The app allows users to search for material, download PDF documents, review notes, and manage their profile, with the addition of a notification and badge system (gamification).

## Main Features

* **Authentication**: User login and registration.
* **Notes Management**: Note searching, detail viewing, and review system.
* **File Management**: PDF downloading with integrated viewer (`PdfViewer`). Tracking of uploaded and downloaded files.
* **Gamification and Notifications**: Badge system for users and integrated push notifications.
* **User Profile**: Management of personal data and app customization.
* **Theme Support**: Light and dark mode.

## Project Structure

The main source code is located under `app/src/main/java/com/example/almanotesmobile/`:

* `data/`: Contains the data models (`Note`, `Theme`, `AppNotification`), Room DAOs, and Repositories (`AuthRepository`, `NoteRepository`, etc.).
* `ui/`: Contains the entire visual part of the app.
  * `composables/`: Reusable UI components (Header, Footer, etc.).
  * `screens/`: The individual application screens (Home, Login, Profile, PdfViewer, etc.).
  * `navigation/`: Internal routing management via Jetpack Navigation.
  * `theme/`: Definition of Compose colors, typography, and themes.
  * `viewmodel/`: The ViewModels associated with the various screens for state and business logic management.
* `utils/`: Generic utility classes (e.g., `PdfDownloader`).
