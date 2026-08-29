package command;

import repository.StudyGroupRepository;

public class FilterContainsName implements Command {
    private final StudyGroupRepository studyGroupRepository;
    private final String name;
    private String result;

    public FilterContainsName(StudyGroupRepository studyGroupRepository,
                              String name) {
        this.studyGroupRepository = studyGroupRepository;
        this.name = name;
    }

    @Override
    public void execute() {
        result = studyGroupRepository.showContainsName(name);
    }

    public String getResult() {
        return result;
    }
}
