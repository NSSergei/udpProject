package command;

import repository.StudyGroupRepository;

public class Remove implements Command {
    private final Long removeId;
    private final StudyGroupRepository studyGroupRepository;
    private final Long userId;

    public Remove(StudyGroupRepository studyGroupRepository, Long removeId, Long userId) {
        this.removeId = removeId;
        this.studyGroupRepository = studyGroupRepository;
        this.userId = userId;
    }

    @Override
    public void execute() {
        studyGroupRepository.deleteById(removeId,userId);

    }
}
