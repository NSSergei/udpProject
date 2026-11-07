package command;

import repository.StudyGroupRepository;

public class Info implements Command {
    private final StudyGroupRepository studyGroupRepository;
    private String info;
    private final Long userId;
    public Info(StudyGroupRepository studyGroupRepository,
                Long userId) {
        this.studyGroupRepository = studyGroupRepository;
        this.userId = userId;
    }


    @Override
    public void execute() {
        info = "Тип хранилища: PostgreSQL " +
                " Дата создания " +   studyGroupRepository.getInitializationDate() +
                " Размер коллекции : " + studyGroupRepository.showInfo(userId);
    }

    public String getInfo() {
        return info;
    }
}
