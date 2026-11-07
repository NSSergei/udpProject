package command;

import repository.StudyGroupRepository;

public class Head implements Command {
    private final StudyGroupRepository studyGroupRepository;
    private String head;
    private final Long userId;

    public Head(StudyGroupRepository studyGroupRepository,
                Long userId) {
        this.studyGroupRepository = studyGroupRepository;
        this.userId = userId;
    }

    @Override
    public void execute() {
        head = studyGroupRepository.showFirstElement(userId);
    }

    public String getHead() {
        return head;
    }
}
