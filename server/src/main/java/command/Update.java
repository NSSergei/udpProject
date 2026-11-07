package command;

import model.StudyGroup;
import repository.StudyGroupRepository;

import java.util.Scanner;

public class Update implements Command {
    private final StudyGroupRepository studyGroupRepository;
    private final StudyGroup studyGroup;
    private final long id;
    private final Long userId;

    public Update(StudyGroupRepository studyGroupRepository, long id, StudyGroup studyGroup, Long userId) {
        this.studyGroupRepository = studyGroupRepository;
        this.studyGroup = studyGroup;
        this.id = id;
        this.userId = userId;
    }

    @Override
    public void execute() {
        if (StudyGroup.validateGroup(studyGroup)) {
            studyGroupRepository.updateStudyGroup(id, studyGroup, userId);
        }
    }
}
