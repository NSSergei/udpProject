package command;

import repository.StudyGroupRepository;

public class FilterGreaterThanAverageMark implements Command {
    private final StudyGroupRepository studyGroupRepository;
    private final double mark;
    private String result;

    public FilterGreaterThanAverageMark(StudyGroupRepository studyGroupRepository, double mark) {
        this.mark = mark;
        this.studyGroupRepository = studyGroupRepository;
    }

    @Override
    public void execute() {
        result = studyGroupRepository.showGreaterThanAverageMark(mark);
    }

    public String getResult() {
        return result;
    }
}
