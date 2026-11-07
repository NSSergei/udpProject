package command;

import repository.StudyGroupRepository;

public class Show implements Command{
    private final StudyGroupRepository studyGroupRepository;
    private String collectionResult;
    private final Long userId;

    public Show(StudyGroupRepository studyGroupRepository,
                Long userId) {
        this.studyGroupRepository = studyGroupRepository;
        this.userId = userId;
    }

    @Override
    public void execute() {
        collectionResult = studyGroupRepository.showAllCollection(userId);
    }

    public String getCollectionResult() {
        return collectionResult;
    }
}
