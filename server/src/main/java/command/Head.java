package command;

import repository.StudyGroupRepository;

public class Head implements Command {
    private final StudyGroupRepository studyGroupRepository;
    private String head;

    public Head(StudyGroupRepository studyGroupRepository) {
        this.studyGroupRepository = studyGroupRepository;
    }

    @Override
    public void execute() {
        head = studyGroupRepository.showFirstElement();
    }

    public String getHead() {
        return head;
    }
}
