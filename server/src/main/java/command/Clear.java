package command;

import repository.StudyGroupRepository;

public class Clear implements Command {
    private final StudyGroupRepository studyGroupRepository;
    private final Long userId;
    public Clear(StudyGroupRepository studyGroupRepository,
                 Long userId) {
        this.studyGroupRepository = studyGroupRepository;
        this.userId = userId;
    }

    @Override
    public void execute() {
        System.out.println("Метод очистить всю коллекцию");
        studyGroupRepository.deleteAll(userId);
    }
}
