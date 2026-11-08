# 🏠 CampusNest - Student Accommodation Management System

CampusNest is a robust Android mobile application designed to bridge the gap between students and off-campus accommodation providers. Built using modern Android development practices, the app offers a seamless experience for browsing, applying for, signing lease agreements, and managing student housing listings.

---

## 🚀 Project Overview

This repository contains the **Android Frontend** for the CampusNest platform. The system facilitates a two-sided marketplace where students can find verified housing, digitally sign lease agreements, and administrators can manage property listings.

*   **Platform:** Android (Min SDK 24)
*   **Language:** Kotlin
*   **Architecture:** MVVM (Model-View-ViewModel)
*   **Networking:** Retrofit 2 & OkHttp 3 (with Bearer Token Authentication & GSON serialization)
*   **Authentication:** Firebase (Google SSO) & Custom PHP/MySQL API on AWS
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
*   **📜 Digital Lease Signing:** Review and sign lease agreements for approved applications with interactive Material Dialog confirmation.
*   **📄 Print & View Lease:** View or print official PDF Lease Agreements (`lease_agreement.php`) directly in the mobile browser.
*   **🧾 Receipt Generation:** View and print official payment receipts (`receipt.php`) for confirmed bookings.
*   **Profile Management:** Update personal information and account settings.
*   **Dark Mode Support:** Full support for system-wide Dark Mode for improved accessibility.

### 🛡️ Admin Features
*   **Verification Queue:** A specialized dashboard to view listings submitted by landlords that are awaiting approval.
*   **Listing Management:** Approve or reject property listings to maintain platform integrity.

### ⚙️ DevOps & CI/CD
*   **GitHub Actions:** Automated pipeline that executes unit tests and builds the debug APK on every push to the repository.

---

## 🌐 Backend Configuration

The backend API is hosted on AWS at:
`http://18.133.240.99/api/`

The app is pre-configured in `RetrofitClient.kt` to connect to this endpoint directly:
```kotlin
private const val BASE_URL = "http://18.133.240.99/api/"
```

### Endpoints Integration
*   `POST /api/login.php` - Authenticate user & retrieve Bearer Token
*   `GET /api/listings.php` - Fetch verified property listings
*   `POST /api/apply.php` - Submit application for a listing
*   `GET /api/my_applications.php` - Retrieve student applications, lease status (`bookingID`, `studentAccepted`), and payment details (`paymentID`)
*   `POST /api/accept_lease.php` - Sign and accept a lease agreement
*   `GET /lease_agreement.php?bookingID={id}` - Print-friendly PDF lease agreement
*   `GET /receipt.php?paymentID={id}` - Print-friendly payment receipt

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
*   **API:** Retrofit 2 with GSON converter (`@SerializedName` supporting snake_case & camelCase alternate mapping) and OkHttp logging interceptors.
*   **Auth:** Google Identity Services (`googleid`) & Firebase Auth.

---

**Developed for CampusNest**
