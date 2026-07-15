# 🛒 E-Commerce Platform Android Application

![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android)
![Kotlin](https://img.shields.io/badge/Language-Kotlin-7F52FF?style=for-the-badge&logo=kotlin)
![Firebase](https://img.shields.io/badge/Firebase-Authentication%20%7C%20Firestore-orange?style=for-the-badge&logo=firebase)
![MVVM](https://img.shields.io/badge/Architecture-MVVM-blue?style=for-the-badge)
![Room](https://img.shields.io/badge/Database-Room-green?style=for-the-badge)
![Hilt](https://img.shields.io/badge/Dependency-Hilt-red?style=for-the-badge)

A modern Android E-Commerce application built using **Kotlin**, **MVVM Architecture**, **Firebase**, **Room Database**, and **Hilt Dependency Injection**. This project demonstrates modern Android development practices with user authentication, product management, favorites, and cloud integration.

---

# 📱 Features

## 🔐 Authentication
- Email & Password Login
- User Registration
- Firebase Authentication
- Error Handling
- User Session Management
- Logout Functionality

---

## 🏠 Home Screen
- Display all uploaded products
- Product Image
- Product Name
- Short Description
- Product Price
- RecyclerView / LazyColumn
- Click to view Product Details

---

## 📦 Product Details
- Large Product Images
- Full Description
- Product Price
- Seller Information
- Seller Contact Details
- Add to Favorites

---

## ⬆ Upload Product
Users can upload products with:

- Product Title
- Product Description
- Product Price
- Minimum 3 Images
- Image Upload to Firebase Storage
- Product Data stored in Firebase Firestore

---

## ❤️ Favorites
- Add Products to Favorites
- Remove Products
- Stored Offline using Room Database
- Fast Local Access

---

## 👤 User Profile
- View User Information
- Uploaded Products
- Logout

---

## ⭐ Recommended Products (Optional)
Fetched using **FakeStore API**

Features:

- Retrofit
- Gson Converter
- Network Calls
- Product Details
- Add to Favorites

---

# 🏗 Architecture

The project follows **MVVM (Model-View-ViewModel)** Architecture.

```
Presentation Layer
│
├── Activities
├── Fragments / Compose Screens
├── ViewModels
│
Domain Layer
│
├── Repository Interfaces
├── Use Cases
│
Data Layer
│
├── Firebase Firestore
├── Firebase Authentication
├── Firebase Storage
├── Room Database
├── Retrofit API
```

---

# 📂 Project Structure

```
app/
│
├── data/
│   ├── local/
│   ├── remote/
│   ├── repository/
│
├── di/
│
├── model/
│
├── repository/
│
├── ui/
│   ├── auth/
│   ├── home/
│   ├── details/
│   ├── upload/
│   ├── favorites/
│
├── viewmodel/
│
├── utils/
│
└── MainActivity.kt
```

---

# 🛠 Tech Stack

| Technology | Usage |
|------------|-------|
| Kotlin | Programming Language |
| MVVM | Architecture |
| Firebase Authentication | Login & Registration |
| Firebase Firestore | Cloud Database |
| Firebase Storage | Product Images |
| Room Database | Local Favorites |
| Hilt | Dependency Injection |
| Navigation Component | Navigation |
| Coroutines | Background Tasks |
| LiveData / StateFlow | UI State |
| Retrofit | Network Calls |
| Gson | JSON Parsing |
| Coil / Glide | Image Loading |
| Material Design 3 | UI Components |

---

# 📸 Screens

- Splash Screen
- Login
- Register
- Home
- Product Details
- Upload Product
- Favorites
- User Profile

> Add screenshots inside the **screenshots/** folder.

```
screenshots/
│
├── login.png
├── register.png
├── home.png
├── details.png
├── upload.png
├── favorites.png
└── profile.png
```

---

# 🔥 Firebase Setup

## 1. Create Firebase Project

Go to

https://console.firebase.google.com/

---

## 2. Enable

- Authentication
- Firestore Database
- Firebase Storage

---

## 3. Authentication

Enable

- Email/Password
- (Optional) Google Sign In
- (Optional) GitHub Authentication

---

## 4. Download

```
google-services.json
```

Place it inside

```
app/
```

---

# 🚀 Installation

Clone the repository

```bash
git clone https://github.com/yourusername/ECommerce-App.git
```

Open in Android Studio

Sync Gradle

Add

```
google-services.json
```

Run the project.

---

# 📦 Dependencies

### Firebase

- Firebase Authentication
- Firebase Firestore
- Firebase Storage

### Android Jetpack

- Navigation
- Lifecycle
- ViewModel
- LiveData
- Room

### Dependency Injection

- Hilt

### Networking

- Retrofit
- Gson Converter

### Image Loading

- Coil / Glide

---

# 💾 Database

## Firebase Firestore

Stores

- Users
- Products

Example

```
Users
│
└── UserID
      ├── Name
      ├── Email
      └── Phone
```

```
Products
│
└── ProductID
      ├── Title
      ├── Description
      ├── Price
      ├── Images
      ├── SellerID
      └── Timestamp
```

---

## Room Database

Stores

```
Favorites
```

```
Favorite
│
├── ProductID
├── Title
├── Price
└── Image
```

---

# 🔄 App Flow

```
Splash
   │
   ▼
Login/Register
   │
   ▼
Home Screen
   │
   ├──────────────┐
   ▼              ▼
Details        Upload
   │
   ▼
Favorites
```

---

# 📋 Assignment Requirements Covered

✅ Firebase Authentication

✅ Login

✅ Register

✅ Product Listing

✅ Product Details

✅ Upload Product

✅ Firebase Firestore

✅ Firebase Storage

✅ Room Database

✅ Favorites

✅ MVVM Architecture

✅ Hilt Dependency Injection

✅ Material UI

✅ RecyclerView / LazyColumn

✅ Navigation Component

---

# ⭐ Bonus Features

- Search Products
- Category Filters
- Recommended Products API
- Google Authentication
- GitHub Authentication
- Push Notifications
- Pagination
- Dark Mode
- Offline Support

---

# 📽 Demo

Add your screen recording here.

```
demo/demo.mp4
```

---

# 📥 APK

```
app-release.apk
```

---

# 📖 How to Run

1. Clone the repository.
2. Open in Android Studio.
3. Add your Firebase `google-services.json`.
4. Sync Gradle.
5. Build the project.
6. Run on an emulator or physical device.

---

# 👨‍💻 Author

**Ravi Ranjan**

- Android Developer
- Kotlin Developer
- Firebase Enthusiast
- Full Stack Developer

GitHub:
https://github.com/yourusername

LinkedIn:
https://linkedin.com/in/ravi-ranjan-ab1b6a347

---

# 📄 License

This project is developed for educational purposes as part of the **TuteDude Android Development Assignment**.

---

## ⭐ If you found this project helpful, don't forget to Star the repository!
```
