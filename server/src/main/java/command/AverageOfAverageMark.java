package command;

import repository.StudyGroupRepository;

public class AverageOfAverageMark implements  Command {
    private final StudyGroupRepository studyGroupRepository;
    private String averageResult;
    private final Long userId;

    public AverageOfAverageMark(StudyGroupRepository studyGroupRepository,
                                Long userId) {
        this.studyGroupRepository = studyGroupRepository;
        this.userId = userId;
    }

    @Override
    public void execute() {
        averageResult = studyGroupRepository.showAverageMark(userId);
    }

    public String getAverageResult() {
        return averageResult;
    }
}
