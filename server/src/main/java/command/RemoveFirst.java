package command;

import repository.StudyGroupRepository;

public class RemoveFirst implements Command {
    private final StudyGroupRepository studyGroupRepository;

    public RemoveFirst(StudyGroupRepository studyGroupRepository) {
        this.studyGroupRepository = studyGroupRepository;
    }

    @Override
    public void execute() {
        studyGroupRepository.deleteFirst();
    }
}
