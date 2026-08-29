package command;

import repository.StudyGroupRepository;

public class AverageOfAverageMark implements  Command {
    private final StudyGroupRepository studyGroupRepository;
    private String averageResult;

    public AverageOfAverageMark(StudyGroupRepository studyGroupRepository) {
        this.studyGroupRepository = studyGroupRepository;
    }

    @Override
    public void execute() {
        averageResult = studyGroupRepository.showAverageMark();
    }

    public String getAverageResult() {
        return averageResult;
    }
}
