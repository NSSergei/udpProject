package command;

import model.StudyGroup;
import repository.StudyGroupRepository;


public class RemoveGreater implements Command {
    private final StudyGroupRepository studyGroupRepository;
    private long element;

    public RemoveGreater(StudyGroupRepository studyGroupRepository,
                         long element) {
        this.studyGroupRepository = studyGroupRepository;
        this.element = element;
    }

    @Override
    public void execute() {
        studyGroupRepository.removeGreater(element);
    }
}
