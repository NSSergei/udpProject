package command;

import repository.StudyGroupRepository;

public class FilterContainsName implements Command {
    private final StudyGroupRepository studyGroupRepository;
    private final String name;
    private String result;
    private final Long userId;

    public FilterContainsName(StudyGroupRepository studyGroupRepository,
                              String name,
                              Long userId) {
        this.studyGroupRepository = studyGroupRepository;
        this.name = name;
        this.userId = userId;
    }

    @Override
    public void execute() {
        result = studyGroupRepository.showContainsName(name, userId);
    }

    public String getResult() {
        return result;
    }
}
