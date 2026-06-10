-- 1. Create the Database
CREATE DATABASE IF NOT EXISTS student_grading_db;
USE student_grading_db;

-- 2. Users Table (Main Authentication Table)
-- Roles: 'ADMIN', 'TEACHER', 'STUDENT'
CREATE TABLE IF NOT EXISTS users (
                                     user_id INT PRIMARY KEY AUTO_INCREMENT,
                                     username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('ADMIN', 'TEACHER', 'STUDENT') NOT NULL,
    full_name VARCHAR(100) NOT NULL
    );

-- 3. Teachers Table
CREATE TABLE IF NOT EXISTS teachers (
                                        teacher_id INT PRIMARY KEY AUTO_INCREMENT,
                                        user_id INT NOT NULL,
                                        department VARCHAR(100),
    email VARCHAR(100) UNIQUE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
    );

-- 4. Students Table
CREATE TABLE IF NOT EXISTS students (
                                        student_id VARCHAR(20) PRIMARY KEY,
    user_id INT NOT NULL,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE,
    gender ENUM('Male', 'Female') NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
    );

-- 5. Subjects Table
CREATE TABLE IF NOT EXISTS subjects (
                                        subject_id INT PRIMARY KEY AUTO_INCREMENT,
                                        subject_code VARCHAR(20) NOT NULL UNIQUE,
    subject_name VARCHAR(100) NOT NULL,
    teacher_id INT,
    FOREIGN KEY (teacher_id) REFERENCES teachers(teacher_id) ON DELETE SET NULL
    );

-- 6. Results Table
CREATE TABLE IF NOT EXISTS results (
                                       result_id INT PRIMARY KEY AUTO_INCREMENT,
                                       student_id VARCHAR(20) NOT NULL,
    subject_id INT NOT NULL,
    marks DOUBLE NOT NULL CHECK (marks >= 0 AND marks <= 100),
    grade CHAR(2),
    status VARCHAR(10),
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) ON DELETE CASCADE
    );

-- ==========================================================
-- INSERT SAMPLE DATA FOR TESTING
-- ==========================================================

-- A. Insert Admin (Default Login: admin / admin123)
INSERT INTO users (username, password, role, full_name)
VALUES ('admin', 'admin123', 'ADMIN', 'System Administrator');

-- B. Insert a Teacher (Default Login: teacher1 / teach123)
INSERT INTO users (username, password, role, full_name)
VALUES ('teacher1', 'teach123', 'TEACHER', 'Mr. Sisay Alemu');

INSERT INTO teachers (user_id, department, email)
VALUES (LAST_INSERT_ID(), 'Computer Science', 'sisay@school.com');

-- C. Insert a Student (Default Login: student1 / stud123)
INSERT INTO users (username, password, role, full_name)
VALUES ('student1', 'stud123', 'STUDENT', 'Aster Demeke');

INSERT INTO students (student_id, user_id, first_name, last_name, email, gender)
VALUES ('DTU16R1232', LAST_INSERT_ID(), 'Aster', 'Demeke', 'aster@gmail.com', 'Female');

-- D. Insert a Sample Subject and assign it to the teacher
INSERT INTO subjects (subject_code, subject_name, teacher_id)
VALUES ('COS1221', 'Database Management', 1);

-- E. Insert a Sample Result
INSERT INTO results (student_id, subject_id, marks, grade, status)
VALUES ('DTU16R1232', 1, 85.5, 'B', 'Pass');