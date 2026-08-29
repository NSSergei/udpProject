package command;

import repository.StudyGroupRepository;

public class Clear implements Command {
    private final StudyGroupRepository studyGroupRepository;

    public Clear(StudyGroupRepository studyGroupRepository) {
        this.studyGroupRepository = studyGroupRepository;
    }

    @Override
    public void execute() {
        System.out.println("Метод очистить всю коллекцию");
        studyGroupRepository.deleteAll();
    }
}
