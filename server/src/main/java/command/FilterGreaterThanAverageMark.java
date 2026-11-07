package command;

import repository.StudyGroupRepository;

public class FilterGreaterThanAverageMark implements Command {
    private final StudyGroupRepository studyGroupRepository;
    private final double mark;
    private String result;
    private final Long userId;

    public FilterGreaterThanAverageMark(StudyGroupRepository studyGroupRepository, double mark, Long userId) {
        this.mark = mark;
        this.studyGroupRepository = studyGroupRepository;
        this.userId = userId;
    }

    @Override
    public void execute() {
        result = studyGroupRepository.showGreaterThanAverageMark(mark, userId);
    }

    public String getResult() {
        return result;
    }
}
