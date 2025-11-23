# 🤖 CSAT AI Study Room

## 📄 About This Project

This is a personalized AI learning application for students preparing for the CSAT (College Scholastic Ability Test). The UI is built entirely with Jetpack Compose. It allows students to chat with an AI tutor for different subjects to get instant answers and improve their learning efficiency.

## ✨ Key Features

- **AI Tutors by Subject**: Engage in real-time Q&A with a Gemini AI tutor for major subjects.
- **Grade Management**: A feature to record and manage mock exam scores (UI only).
- **Dark Mode Support**: Switch between light and dark themes for user comfort.
- **Simple Login**: Easily start the app through Kakao Login (UI only).

## 🚀 Getting Started

Follow these steps to clone and run the project on your local machine.

### 1. Clone the Project

```bash
git clone [Your Repository URL]
```

### 2. Add Your Gemini API Key

The AI tutor feature is powered by the Google Gemini API. You must add your own API key to use it.

1.  Create a file named `local.properties` in the **root directory** of the project.
2.  Add your Gemini API key to the file as follows. Replace `YOUR_API_KEY` with your actual key.

    ```properties
    GEMINI_API_KEY=YOUR_API_KEY
    ```

    **Note:** The `local.properties` file is included in `.gitignore`, so your API key will remain private.

### 3. Run the App

Open the project in Android Studio. After the Gradle sync is complete, you can build and run the application.

---
