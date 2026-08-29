package request;

import model.StudyGroup;

public class RequestData {
    private String command;
    private StudyGroup studyGroup;
    private Long id;
    private Double average;
    private String name;

    public RequestData() {
    }

    public RequestData(String command, long id) {
        this.command = command;
        this.id = id;
    }

    public RequestData(String command, StudyGroup studyGroup, long id) {
        this.command = command;
        this.studyGroup = studyGroup;
        this.id = id;
    }

    public RequestData(String command, String name) {
        this.command = command;
        this.name = name;
    }

    public RequestData(String command, double average) {
        this.command = command;
        this.average = average;
    }

    public RequestData(String command, StudyGroup studyGroup) {
        this.command = command;
        this.studyGroup = studyGroup;
    }

    public RequestData(String command) {
        this.command = command;
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
}
