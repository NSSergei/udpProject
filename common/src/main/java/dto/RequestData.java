package dto;

import model.StudyGroup;
import model.User;


public class RequestData {
    private String command;
    private StudyGroup studyGroup;
    private User user;
    private Long id;
    private Long userId;
    private Double average;
    private String name;


    public RequestData() {
    }

    //register and login
    public RequestData(String command,User user) {
        this.command = command;
        this.user = user;
    }

    public RequestData(String command, long id, long userId) {
        this.command = command;
        this.id = id;
        this.userId = userId;
    }

    public RequestData(String command, StudyGroup studyGroup, long id, long userId) {
        this.command = command;
        this.studyGroup = studyGroup;
        this.id = id;
        this.userId = userId;
    }

    public RequestData(String command, String name, long userId) {
        this.command = command;
        this.name = name;
        this.userId = userId;
    }

    public RequestData(String command, double average, long userId) {
        this.command = command;
        this.average = average;
        this.userId = userId;
    }

    public RequestData(String command, StudyGroup studyGroup, long userId) {
        this.command = command;
        this.studyGroup = studyGroup;
        this.userId = userId;
    }

    public RequestData(String command, long userId) {
        this.command = command;
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }

    public String getCommand() {
        return command;
    }

    public Double getAverage() {
        return average;
    }

    public StudyGroup getStudyGroup() {
        return studyGroup;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public User getUser() {
        return user;
    }
}
