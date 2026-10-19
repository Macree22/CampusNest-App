# 🏠 CampusNest - Student Accommodation Management System

CampusNest is a robust Android mobile application designed to bridge the gap between students and off-campus accommodation providers. Built using modern Android development practices, the app offers a seamless experience for browsing, applying for, and managing student housing listings.

---

## 🚀 Project Overview

This repository contains the **Android Frontend** for the CampusNest platform. The system facilitates a two-sided marketplace where students can find verified housing and administrators can manage property listings.

*   **Platform:** Android (Min SDK 24)
*   **Language:** Kotlin
*   **Architecture:** MVVM (Model-View-ViewModel)
*   **Networking:** Retrofit 2 & OkHttp 3 (with Bearer Token Authentication)
*   **Authentication:** Firebase (Google SSO) & PHP/MySQL Custom API
*   **Backend Repository:** [CampusNest PHP Backend](https://github.com/Macree22/CampusNest)

---

## 🛠️ Features

### 🔐 Authentication & Security
*   **Standard Login & Registration:** Secure email/password authentication via the PHP API.
*   **Google Single Sign-On:** Modern integration using `androidx.credentials` and Firebase for quick student access.
*   **Token-Based Security:** All protected endpoints are secured using Bearer tokens exchanged during login.
*   **Role-Based Routing:** The UI dynamically adapts its navigation and features based on whether the user is a **Student** or an **Admin**.

### 👨‍🎓 Student Features
*   **Verified Listings:** Browse a curated list of properties vetted by administrators.
*   **Detailed Views:** In-depth property information, including pricing, amenities, and location.
*   **Application System:** Submit accommodation applications directly through the app.
*   **Application Tracking:** A dedicated dashboard to monitor the real-time status of pending applications.
*   **Profile Management:** Update personal information and account settings.
*   **Dark Mode Support:** Full support for system-wide Dark Mode for improved accessibility.

### 🛡️ Admin Features
*   **Verification Queue:** A specialized dashboard to view listings submitted by landlords that are awaiting approval.
*   **Listing Management:** Approve or reject property listings to maintain platform integrity.

### ⚙️ DevOps & CI/CD
*   **GitHub Actions:** Automated pipeline that executes unit tests and builds the debug APK on every push to the repository.

---

## 🌐 How to Run (Using Ngrok)

Since the backend API is hosted locally (WAMP/XAMPP), the mobile application uses **Ngrok** to expose the local server to the internet so the device/emulator can communicate with it.

### 1. Backend Preparation
*   Ensure your **WAMP** or **XAMPP** server is running (Apache and MySQL).
*   Ensure the database is imported and seeded correctly.

### 2. Start the Ngrok Tunnel
Open your terminal and execute:
```bash
ngrok http 80
```
*   Copy the generated **HTTPS Forwarding URL** (e.g., `https://a1b2-c3d4.ngrok-free.dev`).

### 3. Configure the Android App
*   In Android Studio, open the file: `app/src/main/java/com/example/campusnestapp/RetrofitClient.kt`.
*   Replace the `BASE_URL` constant with your unique Ngrok URL, ensuring it ends with the API path:
    ```kotlin
    private const val BASE_URL = "https://your-unique-id.ngrok-free.dev/campusnest/api/"
    ```

### 4. Build and Run
*   Sync Gradle and click **Run** to deploy the app to your emulator or physical device.

---

## 🔑 Test Credentials

Use these credentials to evaluate the different user roles within the system:

### 🎓 Student Login
*   **Email:** `siyabonga.mkhize@mycampus.ac.za`
*   **Password:** `CampusNest2026!`

### 👮 Admin Login
*   **Email:** `admin@campusnest.co.za`
*   **Password:** `CampusNest2026!`

---

## 📦 Technical Stack

*   **UI:** Material Design 3, Glide (Image Loading), ViewBinding.
*   **Async:** Kotlin Coroutines for non-blocking network calls.
*   **API:** Retrofit 2 with GSON converter and OkHttp logging interceptors.
*   **Auth:** Google Identity Services (`googleid`) & Firebase Auth.

---

**Developed for CampusNest - Task 2 Submission.**
**Author:** [Your Name / Student Number]
