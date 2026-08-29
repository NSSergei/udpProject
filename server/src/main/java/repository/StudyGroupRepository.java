package repository;
import exception.ValidationException;
import model.StudyGroup;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.PriorityQueue;
import java.util.stream.Collectors;

public class StudyGroupRepository {
    private final PriorityQueue<StudyGroup> collection;
    private final LocalDateTime initializationDate;
    private long maxId = 1;


    public PriorityQueue<StudyGroup> getCollection() {
        return collection;
    }


    public StudyGroupRepository() {
        collection = new PriorityQueue<>(Comparator.comparingLong(StudyGroup::getId)
        );
        initializationDate = LocalDateTime.now();
    }



    public LocalDateTime getInitializationDate() {
        return initializationDate;
    }

    public String showAllCollection() {
        return collection
                .stream()
                .map(StudyGroup::toString)
                .collect(Collectors.joining("\n"));
    }

    public StudyGroup addStudyGroup(StudyGroup studyGroup) {
        studyGroup.setId(++maxId);
        collection.add(studyGroup);
        return studyGroup;
    }

    public StudyGroup updateStudyGroup(long id, StudyGroup studyGroup) {
        StudyGroup group = findById(id)
                .orElseThrow(() -> new ValidationException("null"));

        group.setName(studyGroup.getName());
        group.setCoordinates(studyGroup.getCoordinates());
        group.setStudentsCount(studyGroup.getStudentsCount());
        group.setTransferredStudents(studyGroup.getTransferredStudents());
        group.setAverageMark(studyGroup.getAverageMark());
        group.setFormOfEducation(studyGroup.getFormOfEducation());
        group.setGroupAdmin(studyGroup.getGroupAdmin());

        return group;
    }

    public String showFirstElement() {
        if (collection.peek() != null) {
            return collection.peek().toString();
        }
        return "Объект не найден / null";
    }

    public String showAverageMark() {
        double average = collection
                .stream()
                .mapToDouble(StudyGroup::getAverageMark)
                .average()
                .orElse(0);

        return String.valueOf(average);
    }

    public String showContainsName(String name) {
        return collection
                .stream()
                .filter(group -> group.getName().contains(name))
                .map(StudyGroup::toString)
                .collect(Collectors.joining("\n"));
    }

    public String showGreaterThanAverageMark(double averageMark) {
        return collection.stream()
                .filter(group -> group.getAverageMark() > averageMark)
                .map(StudyGroup::toString)
                .collect(Collectors.joining("\n"));
    }

    public void deleteById(long id ) {
        StudyGroup group = findById(id)
                .orElseThrow(() -> new ValidationException("Объект не найден"));

        collection.remove(group);
    }

    public void deleteAll() {
        collection.clear();
    }

    public void deleteFirst() {
        collection.poll();
    }

    public void removeGreater(long element) {
        collection.removeIf(group -> group.getId() > element);
    }

    public Optional<StudyGroup> findById(long id) {
        return collection
                .stream()
                .filter(group -> group.getId() == id)
                .findFirst();
    }

    public long maxId() {
        return maxId = collection
                .stream()
                .mapToLong(StudyGroup::getId)
                .max()
                .orElse(1);
    }
}
