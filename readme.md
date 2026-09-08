# SLinguist

**SLinguist** is a fully local Android application for learning foreign words and expressions using customizable flashcards.

Create your own cards, organize them by language, word type, and difficulty, and practice with training sessions based on your selected criteria.

> This project was developed with the help of AI tools.

## Features

- Fully local data storage
- Works completely offline
- Create custom flashcards
- Support for words, phrases, idioms, adjectives, and other word types
- Support for **30 languages**
- Edit cards after creation
- Change the difficulty of existing cards
- Search by word or translation
- Filter cards by:
    - Language
    - Difficulty
    - Word type
- Training sessions based on:
    - Language
    - Difficulty
    - Word type
- Favorite cards
- Learning statistics
- Local user profile
- Light and dark themes
- Fully Russian interface

## Supported Languages

SLinguist uses 30 languages in word cards:

- English
- Spanish
- French
- German
- Italian
- Portuguese
- Chinese
- Japanese
- Korean
- Arabic
- Turkish
- Polish
- Dutch
- Swedish
- Norwegian
- Danish
- Finnish
- Czech
- Slovak
- Hungarian
- Romanian
- Ukrainian
- Belarusian
- Kazakh
- Greek
- Hebrew
- Hindi
- Indonesian
- Vietnamese
- Thai

## Technology

SLinguist is built with **Kotlin** using a modern native Android stack:

- Kotlin
- Jetpack Compose
- Material 3
- Room
- DataStore
- ViewModel
- StateFlow
- Navigation Compose
- Gradle

The project separates UI, application state, business logic, and local data storage.

## Offline & Privacy

SLinguist does not require a backend server to store user data.

Cards, settings, statistics, and other user data are stored locally on the device.

The application is designed to work without an internet connection.

## Installation

Pre-built APK files are available in the **Releases** section.

To install SLinguist:

1. Open the latest release.
2. Download the APK.
3. Install it on your Android device.

You can also build the application yourself from the source code.

## Building

Clone the repository:

```bash
git clone <repository-url>
```

Open the project in Android Studio and allow Gradle to synchronize.

To build a debug APK:

```bash
./gradlew assembleDebug
```

The APK will be generated at:

```text
app/build/outputs/apk/debug/app-debug.apk
```
Or find a releases in the releases folder:
```agsl
slinguist-releases/
```

## Design

The application uses a visual palette based on:

- Violet
- White
- Gray
- Light blue
- Black

Both light and dark themes are supported.

## Project Status

SLinguist is currently under active development.

The current version is a fully functional application, but features and UI may continue to evolve.

## License

Check License.txt file