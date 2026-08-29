package command;

import repository.StudyGroupRepository;

public class Remove implements Command {
    private final Long removeId;
    private final StudyGroupRepository studyGroupRepository;

    public Remove(StudyGroupRepository studyGroupRepository, Long removeId) {
        this.removeId = removeId;
        this.studyGroupRepository = studyGroupRepository;
    }

    @Override
    public void execute() {
        studyGroupRepository.deleteById(removeId);

    }
}
