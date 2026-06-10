package org.example.studentgradingsystem.model;

public class Teacher {
    private int teacherId;
    private int userId;
    private String fullName;
    private String email;
    private String department;

    public Teacher(int teacherId, int userId, String fullName, String email, String department) {
        this.teacherId = teacherId;
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.department = department;
    }

    public int getTeacherId() { return teacherId; }
    public int getUserId() { return userId; }
    public String getFullName() { return fullName; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }
}