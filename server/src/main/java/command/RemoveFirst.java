package command;

import repository.StudyGroupRepository;

public class RemoveFirst implements Command {
    private final StudyGroupRepository studyGroupRepository;
    private final Long userId;

    public RemoveFirst(StudyGroupRepository studyGroupRepository,
                       Long userId) {
        this.studyGroupRepository = studyGroupRepository;
        this.userId = userId;
    }

    @Override
    public void execute() {
        studyGroupRepository.deleteFirst(userId);
    }
}
