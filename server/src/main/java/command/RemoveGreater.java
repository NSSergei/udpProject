package command;

import model.StudyGroup;
import repository.StudyGroupRepository;


public class RemoveGreater implements Command {
    private final StudyGroupRepository studyGroupRepository;
    private final long element;
    private final Long userId;

    public RemoveGreater(StudyGroupRepository studyGroupRepository,
                         long element,
                         Long userId) {
        this.studyGroupRepository = studyGroupRepository;
        this.element = element;
        this.userId = userId;
    }

    @Override
    public void execute() {
        studyGroupRepository.removeGreater(element, userId);
    }
}
