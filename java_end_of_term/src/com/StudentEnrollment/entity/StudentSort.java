package com.StudentEnrollment.entity;


public class StudentSort {
    private String studentId;
    private String name;
    private String major;
    private int totalScore;
    private int rank;

    public StudentSort(String studentId, String name, String major, int totalScore) {
        this.studentId = studentId;
        this.name = name;
        this.major = major;
        this.totalScore = totalScore;
    }

    // Getters and Setters
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public int getTotalScore() { return totalScore; }
    public void setTotalScore(int totalScore) { this.totalScore = totalScore; }
    public int getRank() { return rank; }
    public void setRank(int rank) { this.rank = rank; }
}
