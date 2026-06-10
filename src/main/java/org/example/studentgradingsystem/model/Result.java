package org.example.studentgradingsystem.model;

public class Result {
    private int resultId;
    private String studentId;
    private String studentName;
    private String subjectName;
    private double marks;
    private String grade;
    private String status;

    public Result(int resultId, String studentId, String studentName, String subjectName, double marks, String grade, String status) {
        this.resultId = resultId;
        this.studentId = studentId;
        this.studentName = studentName;
        this.subjectName = subjectName;
        this.marks = marks;
        this.grade = grade;
        this.status = status;
    }

    public int getResultId() { return resultId; }
    public String getStudentId() { return studentId; }
    public String getStudentName() { return studentName; }
    public String getSubjectName() { return subjectName; }
    public double getMarks() { return marks; }
    public String getGrade() { return grade; }
    public String getStatus() { return status; }
}