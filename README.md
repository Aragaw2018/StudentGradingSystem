# Professional Student Grading System

A modular Desktop Application built with Java and JavaFX to manage student records, subjects, and academic grading. The system follows the MVC architecture and uses MySQL for robust data management.

## Key Features

### 1. Multi-Role Dashboards
*   **Admin Dashboard:** Full control over students, teachers, and subjects registration (CRUD).
*   **Teacher Dashboard:** Manage assigned subjects, enter/update student marks, and view grade reports.
*   **Student Dashboard:** Private access for students to view their own results and performance.

### 2. Core Functionalities
*   **Automated Grading:** Automatic calculation of Grades (A, B, C, D, F) and Pass/Fail status based on marks.
*   **Session Management:** Secure login system with a dedicated `UserSession` to track active users.
*   **Data Validation:** Comprehensive input validation for emails, names, and numerical marks.
*   **Self-Service Password Reset:** Integrated "Forgot Password" functionality for users to verify identity and reset credentials.

---



## Tech Stack
*   **Language:** Java 21+
*   **UI Framework:** JavaFX
*   **Build Tool:** Maven
*   **Database:** MySQL (via XAMPP)
*   **Persistence:** JDBC

---

## Setup and Installation

### 1. Database Configuration
1. Open **XAMPP Control Panel** and start Apache and MySQL.
2. Go to `phpMyAdmin` and create a database named `student_grading_db`.
3. Import the provided `db_script.sql` file located in the root directory to set up tables and sample data.

