package org.suai.zabik.chat.database;

public interface Database {
    String getAllGrades(String name);
    String getAllStudents();
    void addStudent(String studentName);
    void setExamGrade(String userName,String examName, int grade);

}
