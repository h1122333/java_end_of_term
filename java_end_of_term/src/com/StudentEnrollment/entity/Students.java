package com.StudentEnrollment.entity;


public class Students {
    private Integer id; // 主键
    private String studentId; // 学号
    private String name; // 姓名
    private String gender; // 性别
    private Integer age; // 年龄 (与数据库匹配)
    private String major; // 专业 (与数据库匹配)
    private Integer course1; // 科目1
    private Integer course2; // 科目2
    private Integer course3; // 科目3

    public Students() {}

    // 构造用于插入数据
    public Students(String studentId, String name, String gender, Integer age, String major, Integer c1, Integer c2, Integer c3) {
        this.studentId = studentId;
        this.name = name;
        this.gender = gender;
        this.age = age;
        this.major = major;
        this.course1 = c1;
        this.course2 = c2;
        this.course3 = c3;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }
    public String getMajor() { return major; }
    public void setMajor(String major) { this.major = major; }
    public Integer getCourse1() { return course1; }
    public void setCourse1(Integer course1) { this.course1 = course1; }
    public Integer getCourse2() { return course2; }
    public void setCourse2(Integer course2) { this.course2 = course2; }
    public Integer getCourse3() { return course3; }
    public void setCourse3(Integer course3) { this.course3 = course3; }

    @Override
    public String toString() {
        return String.format("%-10s %-8s %-4s %-4d %-15s %-4d %-4d %-4d",
                studentId, name, gender, age, major, course1, course2, course3);
    }
}
