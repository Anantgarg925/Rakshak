Rakshak: Your Personal Safety Companion
Rakshak is a smart personal safety application designed to provide peace of mind on the road. It transforms a smartphone into a powerful emergency alert system that automatically detects potential car accidents and ensures help is on the way, even when the user can't make the call themselves.

Overview
The project is a full-stack, real-time system composed of three main parts:

A native Android application that serves as the user-facing client.

A Java Spring Boot backend that manages users, alerts, and real-time communication.

A Python-based Machine Learning pipeline for training the accident detection model.

At its core, Rakshak uses a custom-trained Machine Learning model that analyzes the phone's accelerometer and gyroscope data in real-time. By learning from thousands of examples of both real-world activities and simulated crash events, the model can intelligently distinguish between a genuine accident and a simple false alarm, like dropping the phone.

Core Features
Intelligent Accident Detection: A TensorFlow Lite model running on-device analyzes sensor data to detect crash patterns.

Manual SOS Button: For any emergency, a simple long-press on the SOS button immediately triggers the alert protocol.

Safety Countdown Timer: After an alert is triggered, a 15-second countdown begins, giving the user a crucial window to cancel a false alarm.

Live Ambulance Tracking: Once an alert is confirmed, the system notifies the backend, which dispatches help. The user can then track the responding ambulance in real-time on a live map.

Persistent User Data: All user profiles, emergency contacts, and alert history are stored permanently in a PostgreSQL database.

Personalized Emergency Profile: Users can store and edit critical information for first responders, including their name, phone number, blood type, medical conditions, and allergies.

Emergency Contacts Management: Users can add and delete a list of emergency contacts.

Complete Alert History: The app keeps a log of all past incidents, which can be reviewed and filtered by date.

Technology Stack
Android Application:

Language: Java

Architecture: MVVM (Model-View-ViewModel)

UI: Android XML with Material Design 3

Networking: Retrofit, OkHttp

Real-time: Java-WebSocket client

Mapping: Google Maps SDK

Authentication: Firebase Authentication (Google Sign-In, Phone OTP)

ML: TensorFlow Lite

Backend Server:

Framework: Java Spring Boot

Language: Java 17

Database: PostgreSQL

API: RESTful API

Real-time: Spring WebSockets

Build Tool: Maven

Machine Learning Model:

Language: Python 3.9

Environment: Anaconda / Jupyter Notebook

Core Libraries: TensorFlow (Keras), Scikit-learn, Pandas, NumPy

Setup and Installation
To run the complete project, you need to set up the backend, the database, and the Android app.

1. Backend & Database Setup
Install PostgreSQL: Download and install PostgreSQL from the [official website](https://www.postgresql.org/download/). During installation, remember the password you set for the postgres user.

Create Database: Open the pgAdmin tool, connect to your local server, and create a new database named rakshak_db.

Configure Backend:

Open the backend project (spring folder).

Navigate to src/main/resources/application.properties.

Update the spring.datasource.url, spring.datasource.username, and spring.datasource.password properties to match your PostgreSQL setup.

Run the Backend: Open a terminal in the root of the spring folder and run the command:

mvn spring-boot:run

The server should start on port 8080 (or the port you configured).

2. Android App Setup
Open Project: Open the Android project (mobile-app folder) in Android Studio.

Firebase Setup:

Go to the [Firebase Console](https://console.firebase.google.com/) and create a new project.

Add an Android app to the project with the package name com.example.rakshak.

Download the google-services.json file and place it in the app directory of your Android project.

In the Firebase Console, go to Authentication > Sign-in method and enable Google and Phone as sign-in providers.

Google Maps API Key:

Go to the [Google Cloud Console](https://console.cloud.google.com/) and get a Maps SDK for Android API key.

Open the src/main/res/values/google_maps_api.xml file (you may need to create it) and add your key.

Update IP Address:

Find your computer's local IP address (e.g., 192.168.1.10).

In the Android project, open RetrofitClient.java and WebSocketManager.java and update the BASE_URL and WEBSOCKET_URL constants with your IP address and the backend port (e.g., http://192.168.1.10:8080/).

Build and Run: Build the project and run it on an Android emulator or a physical device.

3. Machine Learning Model (Optional)
If you wish to retrain the model:

Setup Environment: Use the Anaconda Prompt to create and activate the rakshak_env environment and install the required libraries (tensorflow, pandas, numpy, etc.).

Download Datasets: Download the "Simulated Falls", "UCI HAR", "HAPT", and "WISDM" datasets and place them in a known location.

Run Jupyter Notebook: Launch jupyter notebook or jupyter lab.

Update Paths: Open the training notebook and update the file paths in the Step 2 cell to point to your dataset locations.

Run Cells: Run the cells in order to process the data and train the model. This will generate a new model.tflite file.

Update App: Copy the new model.tflite file into the assets folder of your Android app, replacing the old one.

How to Run the System
Ensure your PostgreSQL server is running.

Start the Spring Boot backend server.

Run the Android app on a device connected to the same local network as your backend server.
