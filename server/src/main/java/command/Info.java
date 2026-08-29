package command;

import repository.StudyGroupRepository;

public class Info implements Command {
    private final StudyGroupRepository studyGroupRepository;
    private String info;
    public Info(StudyGroupRepository studyGroupRepository) {
        this.studyGroupRepository = studyGroupRepository;
    }


    @Override
    public void execute() {
        info = "Тип коллекции " + studyGroupRepository.getCollection().getClass().getSimpleName() +
                " Дата создания " +   studyGroupRepository.getInitializationDate() +
                " Размер коллекции : " + studyGroupRepository.getCollection().size();
    }

    public String getInfo() {
        return info;
    }
}
