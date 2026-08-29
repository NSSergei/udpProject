package command;

import repository.StudyGroupRepository;
import storage.XmlStorage;

import java.io.IOException;

public class Save implements Command {
    private final XmlStorage xmlStorage;
    private final StudyGroupRepository studyGroupRepository;

    public Save(XmlStorage xmlStorage,
                StudyGroupRepository studyGroupRepository) {
        this.xmlStorage = xmlStorage;
        this.studyGroupRepository = studyGroupRepository;
    }

    @Override
    public void execute() {
        System.out.println("Сохранение данных в файл");
        try {
            xmlStorage.save(studyGroupRepository.getCollection());
            System.out.println("Запись сохранена");
        } catch (IOException e) {
            System.out.println("ошибка сохранения " + e.getMessage());
        }
    }
}
