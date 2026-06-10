package org.example.studentgradingsystem.model;

public class Subject {
    private int subjectId;
    private String subjectCode;
    private String subjectName;
    private String teacherName;

    public Subject(int subjectId, String subjectCode, String subjectName, String teacherName) {
        this.subjectId = subjectId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.teacherName = teacherName;
    }

    public int getSubjectId() { return subjectId; }
    public String getSubjectCode() { return subjectCode; }
    public String getSubjectName() { return subjectName; }
    public String getTeacherName() { return teacherName; }
}