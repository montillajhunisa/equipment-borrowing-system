package Data_Objects;

import java.io.Serializable;

public class Faculty implements Serializable {
    private String courseCode;
    private String facultyName;

    public Faculty(String courseCode, String facultyName){
        this.courseCode = courseCode;
        this.facultyName = facultyName;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getFacultyName() {
        return facultyName;
    }

    public void setFacultyName(String facultyName) {
        this.facultyName = facultyName;
    }
}
