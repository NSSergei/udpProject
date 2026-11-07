package service;

import command.*;

import dto.ResponseData;
import model.StudyGroup;
import model.User;
import repository.StudyGroupRepository;
import repository.UserRepository;
import dto.RequestData;

import java.io.FileNotFoundException;
import java.sql.SQLException;

public class CommandService {
    private final StudyGroupRepository repository;
    private final UserRepository userRepository;

    public CommandService(StudyGroupRepository repository,
                          UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public synchronized ResponseData run(RequestData requestData) throws SQLException, FileNotFoundException {

        if (requestData == null || requestData.getCommand() == null || requestData.getCommand().isBlank()) {
            throw new exception.ValidationException("Command is required");
        }

        String command = requestData.getCommand();
        if (!command.equals("login") && !command.equals("register")
                && (requestData.getUserId() == null || requestData.getUserId() <= 0)) {
            throw new exception.ValidationException("A valid user ID is required");
        }

        switch (command) {

            case "info":
                 Info info = new Info(repository, requestData.getUserId());
                 info.execute();
                 return new ResponseData(true,
                         info.getInfo(),
                         null);
            case "show":
                 Show show = new Show(repository, requestData.getUserId());
                 show.execute();
                 return new ResponseData(true,
                         show.getCollectionResult(),
                         null);

            case "add":
                 StudyGroup studyGroup = requestData.getStudyGroup();
                 new Add(repository, studyGroup, requestData.getUserId()).execute();
                 return new ResponseData(true,
                         "Объект добавлен",
                         requestData.getUserId());

            case "update":
                 StudyGroup studyGroup1 = requestData.getStudyGroup();
                 long id = requestData.getId();
                 new Update(repository,id,studyGroup1,requestData.getUserId()).execute();
                 return new ResponseData(true,
                         "Объект обнавлен",
                         null);

            case "remove_by_id":
                 long removeId = requestData.getId();
                 new Remove(repository, removeId, requestData.getUserId()).execute();
                 return new ResponseData(true,
                         "Объект по id: " + removeId + " удален",
                         null);

            case "clear":
                new Clear(repository, requestData.getUserId()).execute();
                return new ResponseData(true,
                        "Коллекция очищена",
                        null);

            case "remove_first":
                new RemoveFirst(repository, requestData.getUserId()).execute();
                return new ResponseData(true,
                        "первый объект удален",
                        null);

            case "head":
                Head head = new Head(repository, requestData.getUserId());
                head.execute();
                return new ResponseData(true,
                        head.getHead(),
                        null);

            case "remove_greater":
                long greaterId = requestData.getId();
                new RemoveGreater(repository, greaterId, requestData.getUserId()).execute();
                return new ResponseData(true,
                        "Все элементы, превышающие заданный удалены",
                        null);

            case "average_of_average_mark":
                AverageOfAverageMark average = new AverageOfAverageMark(repository, requestData.getUserId());
                average.execute();
                return new ResponseData(true,
                        average.getAverageResult(),
                        null);

            case "filter_contains_name":
                String name = requestData.getName();
                FilterContainsName filter = new FilterContainsName(repository, name, requestData.getUserId());
                filter.execute();
                return new ResponseData(true,
                        filter.getResult(),
                        null);

            case "filter_greater_than_average_mark":
                double averageMark = requestData.getAverage();
                FilterGreaterThanAverageMark filterGreaterThanAverageMark =
                        new FilterGreaterThanAverageMark(repository,averageMark, requestData.getUserId());
                filterGreaterThanAverageMark.execute();
                return new ResponseData(true,
                        filterGreaterThanAverageMark.getResult(),
                        null);

            case "register":
                if (requestData.getUser() == null) {
                    throw new exception.ValidationException("User credentials are required");
                }
                User user = new User(requestData.getUser().getLogin(),
                        requestData.getUser().getPasswordHash());
                Registrate registrate = new Registrate(userRepository,user);
                registrate.execute();

                return new ResponseData(true,
                        "User зарегистрирован",
                        null);

            case "login":
                if (requestData.getUser() == null) {
                    throw new exception.ValidationException("User credentials are required");
                }
                User loginUser = new User(requestData.getUser().getLogin(), requestData.getUser().getPasswordHash());
                Login login = new Login(userRepository, loginUser);
                login.execute();
                return new ResponseData(true,
                        login.getResultId().toString(),
                        login.getResultId());
            default:
                return new ResponseData(false,
                        "Unknown command: " + command,
                        null);
            }
    }
}

