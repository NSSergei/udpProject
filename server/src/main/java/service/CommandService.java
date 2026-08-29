package service;

import command.*;

import exception.ValidationException;
import model.StudyGroup;
import repository.StudyGroupRepository;
import request.RequestData;
import storage.XmlStorage;

public class CommandService {
    private final StudyGroupRepository repository;
    private final XmlStorage xml;

    public CommandService(StudyGroupRepository repository,
                          XmlStorage xml) {
        this.repository = repository;
        this.xml = xml;
    }

    public String run(RequestData requestData) {
        String command = requestData.getCommand();
        String result = "";

        switch (command) {
            case "info":
                 System.out.println("INFO 1");
                 Info info = new Info(repository);
                 System.out.println("INFO 2");
                 info.execute();
                 System.out.println("INFO 3");
                 System.out.println("INFO 4: " + result);
                 result = info.getInfo();
                 break;
            case "show":
                 Show show = new Show(repository);
                 show.execute();
                 result = show.getCollectionResult();
                 break;
            case "add":
                 StudyGroup studyGroup = requestData.getStudyGroup();
                 new Add(repository, studyGroup).execute();
                 result = "Объект добавлен";
                 break;
            case "update":
                 StudyGroup studyGroup1 = requestData.getStudyGroup();
                 long id = requestData.getId();
                 new Update(repository,id,studyGroup1).execute();
                 result = "Объект обнавлен";
                 break;
            case "remove_by_id":
                 long removeId = requestData.getId();
                 new Remove(repository, removeId).execute();
                 result = "Объект по id: " + removeId + " удален";
                 break;
            case "clear":
                new Clear(repository).execute();
                result = "Коллекция очищена";
                break;
            /*case "save":
                new Save(xml,repository).execute();
                break;*/
            case "remove_first":
                new RemoveFirst(repository).execute();
                result = "первый объект удален";
                break;
            case "head":
                Head head = new Head(repository);
                head.execute();
                result = head.getHead();
                break;
            case "remove_greater":
                long greaterId = requestData.getId();
                new RemoveGreater(repository, greaterId).execute();
                result = "Все элементы, превышающие заданный удалены";
                break;
            case "average_of_average_mark":
                AverageOfAverageMark average = new AverageOfAverageMark(repository);
                average.execute();
                //result заменить на ResponseData после тестирования
                result = average.getAverageResult();
                break;
            case "filter_contains_name":
                String name = requestData.getName();
                FilterContainsName filter = new FilterContainsName(repository, name);
                filter.execute();
                result = filter.getResult();
                break;
            case "filter_greater_than_average_mark":
                double averageMark = requestData.getAverage();
                FilterGreaterThanAverageMark filterGreaterThanAverageMark =
                        new FilterGreaterThanAverageMark(repository,averageMark);
                filterGreaterThanAverageMark.execute();
                result = filterGreaterThanAverageMark.getResult();
                break;
            default:
                result = "Метод не найден";
                break;
            }

        return result;
    }
}

