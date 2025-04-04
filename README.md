# SocietyQ - Society Management App

<div align="center">
  <img src="https://github.com/user-attachments/assets/be0b81ae-05a0-40e9-a04e-3c2352656d10" alt="SocietyQ App">
  <img src="https://github.com/user-attachments/assets/cfe6f2d3-ea4f-45a5-b22e-a2b5f50decba" alt="Lost and Found">
  <img src="https://github.com/user-attachments/assets/e8d4c7ac-3cfd-4a81-8805-a111130cd7ea" alt="Bills">

</div>

## Project Overview

SocietyQ is an Android application designed to streamline communication and management within a housing society. It aims to provide residents with a convenient platform to access important information, manage tasks, and interact with society administration.

The project solves the common challenges of fragmented communication and inefficient processes in housing societies by offering a centralized mobile solution.

**Key Features:**

* **User Authentication:** Secure login and logout using username and password, with username storage encrypted via Android Keystore.
* **Dashboard:** A central hub providing quick access to various modules.
* **Notice Board:** View general and personal society notices.
* **Complaint & Suggestion Box:** Submit complaints or suggestions with details and optional media attachments.
* **Delivery Tracking:** View delivery notifications (tracking might be simulated).
* **Bill Management:** View and manage society bills (e.g., maintenance) with payment status and options to pay (simulated) or download receipts.
* **Event Management:** View society events and RSVP.
* **Lost & Found:** Browse items reported as lost or found, including images and contact details.
* **Info QR Code:** Generate a personal QR code containing user details for identification or information sharing within the society.

## Dependencies

This project is built using Kotlin and utilizes various Android Jetpack libraries and other dependencies.

**Core Technologies:**

* **Kotlin:** Primary programming language.
* **Android Jetpack:**
    * **Compose:** For building the native UI.
    * **ViewModel:** For managing UI-related data lifecycle-awarely.
    * **Navigation Compose:** For handling navigation between screens.
    * **Lifecycle (collectAsStateWithLifecycle):** For observing StateFlows safely.
    * **Activity KTX:**.
    * **Core KTX:**.
    * **Security Crypto (Android Keystore):** For encrypting user preferences.
* **Kotlin Coroutines:** For managing background threads and asynchronous operations.
* **Coil:** For image loading (likely used in Lost & Found).
* **ZXing (via `com.google.zxing`):** For QR code generation.
* **Accompanist Permissions:** For handling runtime permissions (e.g., for media access in Complaints).

## Setup Instructions

There are two ways to install the app:

**Option 1: Build from Source (Recommended for Developers)**

1.  **Clone the Repository:**
    ```bash
    git clone https://github.com/War004/rwaManagement_CodeShark_CodeForge25
    cd rwaManagement_CodeShark_CodeForge25 
    ```
2.  **Open in Android Studio:** Open the cloned project directory in a recent version of Android Studio (e.g., Hedgehog or later recommended for latest Compose features).
3.  **Dependencies:** Android Studio should automatically detect the `build.gradle` files and prompt you to sync the project, which will download the required dependencies. If not, manually sync the project (`File > Sync Project with Gradle Files`).
4.  **Configuration:** `[Your Input Needed: Add any specific configuration steps if required, e.g., setting up API keys in local.properties, creating specific config files, etc. Based on the provided code, no external API keys seem necessary, but confirm this.]`
5.  **Build the Project:** Build the project using Android Studio (`Build > Make Project` or click the Hammer icon).
6.  **Run the Application:**
    * Connect an Android device (with USB debugging enabled) or start an Android Emulator.
    * Select the desired device/emulator from the dropdown menu in Android Studio.
    * Click the 'Run' button (green Play icon) or use `Run > Run 'app'`.
7.  **Basic Usage:**
    * The app will launch to the Login screen.
    * Enter any non-empty username (password validation is currently bypassed in the sample code) and tap LOGIN.
    * You will be navigated to the Dashboard.
    * Explore different features by tapping on the icons in the "Daily Essentials" grid. Most list screens currently display sample data.

**Option 2: Install Pre-built APK**

* You can also download the ready-to-install `.apk` file directly from the **[Releases](https://github.com/War004/rwaManagement_CodeShark_CodeForge25/releases)** section of the GitHub repository. Download the latest APK file to your Android device and install it (you might need to enable installation from unknown sources in your device settings).

**Basic Usage (After Installation):**

* The app will launch to the Login screen.
* Enter any non-empty username (password validation is currently bypassed in the sample code) and tap LOGIN.
* You will be navigated to the Dashboard.
* Explore different features by tapping on the icons in the "Daily Essentials" grid. Most list screens currently display sample data.

## Team Member Information

* **Rajat Kumar:** Backend
* **Riteshwar Poddar:** App development
* **Rajan Kumar Jha:** Backend, database
* **Suryanash Kumar Singh:** Frontend
