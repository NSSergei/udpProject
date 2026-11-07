package model;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import enums.FormOfEducation;
import exception.ValidationException;

import java.time.LocalDateTime;

@JacksonXmlRootElement(localName="StudyGroup")
public class StudyGroup implements Comparable<StudyGroup> {
    //затычка для создания
    private long id; //Значение поля должно быть больше 0, Значение этого поля должно быть
    //уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    //private final Coordinates coordinates; //Поле не может быть null
    private Coordinates coordinates; //Поле не может быть null
    private LocalDateTime creationDate; //Поле не может быть null, Значение этого
    //поля должно генерироваться автоматически
    private long studentsCount; //Значение поля должно быть больше 0
    private int transferredStudents; //Значение поля должно быть больше 0
    private double averageMark; //Значение поля должно быть больше 0
    private FormOfEducation formOfEducation; //Поле не может быть null
    //private final Person groupAdmin; //Поле не может быть null
    private  Person groupAdmin;
    private  User user;

    public StudyGroup(String name, Coordinates coordinates,
                      LocalDateTime creationDate, long studentsCount,
                      int transferredStudents, double averageMark,
                      FormOfEducation formOfEducation, Person groupAdmin) {
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.studentsCount = studentsCount;
        this.transferredStudents = transferredStudents;
        this.averageMark = averageMark;
        this.formOfEducation = formOfEducation;
        this.groupAdmin = groupAdmin;
    }

    public StudyGroup(long id, String name, Coordinates coordinates,
                      LocalDateTime creationDate, long studentsCount,
                      int transferredStudents, double averageMark,
                      FormOfEducation formOfEducation, Person groupAdmin) {
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.studentsCount = studentsCount;
        this.transferredStudents = transferredStudents;
        this.averageMark = averageMark;
        this.formOfEducation = formOfEducation;
        this.groupAdmin = groupAdmin;
    }

    public StudyGroup() {
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public long getStudentsCount() {
        return studentsCount;
    }

    public int getTransferredStudents() {
        return transferredStudents;
    }

    public double getAverageMark() {
        return averageMark;
    }

    public FormOfEducation getFormOfEducation() {
        return formOfEducation;
    }

    public Person getGroupAdmin() {
        return groupAdmin;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void setStudentsCount(long studentsCount) {
        this.studentsCount = studentsCount;
    }

    public void setTransferredStudents(int transferredStudents) {
        this.transferredStudents = transferredStudents;
    }

    public void setAverageMark(double averageMark) {
        this.averageMark = averageMark;
    }

    public void setFormOfEducation(FormOfEducation formOfEducation) {
        this.formOfEducation = formOfEducation;
    }

    public void setGroupAdmin(Person groupAdmin) {
        this.groupAdmin = groupAdmin;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public int compareTo(StudyGroup other) {
        return Long.compare(this.id, other.id);
    }

    public static boolean validateGroup(StudyGroup studyGroup) {
        if (studyGroup == null) {
            throw new ValidationException("Study group is required");
        }

        if (studyGroup.getName() == null || studyGroup.getName().isBlank()) {
            throw new ValidationException("Name must not be blank");
        }

        if (studyGroup.getCoordinates() == null) {
            throw new ValidationException("Coordinates are required");
        }

        if (!Coordinates.validateCoordinate(studyGroup.getCoordinates().getX(),
                studyGroup.getCoordinates().getY())) {
            throw new ValidationException("Invalid coordinates");
        }

        if (studyGroup.getCreationDate() == null) {
            throw new ValidationException("Creation date is required");
        }

        if (studyGroup.getStudentsCount() <= 0) {
            throw new ValidationException("Students count must be positive");
        }

        if (studyGroup.getTransferredStudents() <= 0) {
            throw new ValidationException("Transferred students must be positive");
        }

        if (!Double.isFinite(studyGroup.getAverageMark()) || studyGroup.getAverageMark() <= 0) {
            throw new ValidationException("Average mark must be a finite positive number");
        }

        if (studyGroup.getFormOfEducation() == null) {
            throw new ValidationException("Form of education is required");
        }

        if (studyGroup.getGroupAdmin() == null) {
            throw new ValidationException("Group administrator is required");
        }

        if (!Person.validatePerson(studyGroup.getGroupAdmin())) {
            throw new ValidationException("Invalid group administrator");
        }

        return true;
    }

    @Override
    public String toString() {
        return "StudyGroup{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordinates=" + coordinates +
                ", creationDate=" + creationDate +
                ", studentsCount=" + studentsCount +
                ", transferredStudents=" + transferredStudents +
                ", averageMark=" + averageMark +
                ", formOfEducation=" + formOfEducation +
                ", groupAdmin=" + groupAdmin +
                '}';
    }

}

