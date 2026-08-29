package command;

import repository.StudyGroupRepository;

public class Show implements Command{
    private final StudyGroupRepository studyGroupRepository;
    private String collectionResult;
    public Show(StudyGroupRepository studyGroupRepository) {
        this.studyGroupRepository = studyGroupRepository;
    }

    @Override
    public void execute() {
        collectionResult = studyGroupRepository.showAllCollection();
    }

    public String getCollectionResult() {
        return collectionResult;
    }
}
