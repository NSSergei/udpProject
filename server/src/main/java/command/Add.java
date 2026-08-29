package command;

import model.StudyGroup;
import repository.StudyGroupRepository;
import java.util.Scanner;

public class Add implements  Command{
    private final StudyGroupRepository studyGroupRepository;
    private final StudyGroup studyGroup;

    public Add(StudyGroupRepository studyGroupRepository,
               StudyGroup studyGroup) {
        this.studyGroupRepository = studyGroupRepository;
        this.studyGroup = studyGroup;
    }

    @Override
    public void execute() {
        if (StudyGroup.validateGroup(studyGroup)) {
            studyGroupRepository.addStudyGroup(studyGroup);
        }
    }
}
