package command;

import model.StudyGroup;
import repository.StudyGroupRepository;

import java.util.Scanner;

public class Update implements Command {
    private final StudyGroupRepository studyGroupRepository;
    private final StudyGroup studyGroup;
    private final long id;

    public Update(StudyGroupRepository studyGroupRepository, long id, StudyGroup studyGroup) {
        this.studyGroupRepository = studyGroupRepository;
        this.studyGroup = studyGroup;
        this.id = id;
    }

    @Override
    public void execute() {
        studyGroupRepository.updateStudyGroup(id, studyGroup);
    }
}
