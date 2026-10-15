package model;

import java.time.LocalDateTime;

/** Represents a submitted suggestion and its student attribution. */
public class Suggestion {
    private final int id;
    private final int studentId;
    private final String studentName;
    private final String subject;
    private final String message;
    private final LocalDateTime dateSubmitted;

    public Suggestion(int id, int studentId, String studentName, String subject,
                      String message, LocalDateTime dateSubmitted) {
        this.id = id;
        this.studentId = studentId;
        this.studentName = studentName;
        this.subject = subject;
        this.message = message;
        this.dateSubmitted = dateSubmitted;
    }

    public int getId() {
        return id;
    }

    public int getStudentId() {
        return studentId;
    }

    public String getStudentName() {
        return studentName;
    }

    public String getSubject() {
        return subject;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getDateSubmitted() {
        return dateSubmitted;
    }

    public String getFormattedDate() {
        return dateSubmitted.toString().replace('T', ' ');
    }
}
