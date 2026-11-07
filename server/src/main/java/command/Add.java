package command;

import model.StudyGroup;
import repository.StudyGroupRepository;

import java.sql.SQLException;
import java.util.Scanner;

public class Add implements  Command{
    private final StudyGroupRepository studyGroupRepository;
    private final StudyGroup studyGroup;
    private final Long userId;

    public Add(StudyGroupRepository studyGroupRepository,
               StudyGroup studyGroup,
               Long userId) {
        this.studyGroupRepository = studyGroupRepository;
        this.studyGroup = studyGroup;
        this.userId = userId;
    }

    @Override
    public void execute() throws SQLException {
        if (StudyGroup.validateGroup(studyGroup)) {
            studyGroupRepository.addStudyGroup(studyGroup, userId);
        }
    }
}
