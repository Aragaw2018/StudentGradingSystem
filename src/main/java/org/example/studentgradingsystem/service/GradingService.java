package org.example.studentgradingsystem.service;

public class GradingService {
    public static String calculateGrade(double marks) {
        if (marks >= 90) return "A+";
        if (marks >= 85) return "A";
        if (marks >= 80) return "A-";
        if (marks >= 75) return "B+";
        if (marks >= 70) return "B";
        if (marks >= 65) return "B-";
        if (marks >= 60) return "C+";
        if (marks >= 55) return "C";
        if (marks >= 50) return "c-";
        if (marks >= 45) return "D";
        return "F";
    }

    public static String calculateStatus(double marks) {
        return marks >= 60 ? "Pass" : "Fail";
    }
}