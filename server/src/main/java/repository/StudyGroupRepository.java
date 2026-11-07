package repository;
import model.StudyGroup;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.concurrent.locks.ReentrantLock;

public class StudyGroupRepository {
    private final LocalDateTime initializationDate;
    private  final Connection connection;
    private final ReentrantLock lock = new ReentrantLock();

    public StudyGroupRepository(Connection connection) {
        this.initializationDate = LocalDateTime.now();
        this.connection = connection;
    }

    public String showInfo(Long userId) {
        lock.lock();

        try {
            String collectionSize;
            String sqlShowType = """
                    SELECT COUNT(*) AS studyGroup_size
                    FROM studyGroup
                    WHERE user_id = ?
                    """;

            try (PreparedStatement pr = connection.prepareStatement(sqlShowType)) {
                pr.setLong(1, userId);

                try (ResultSet rs = pr.executeQuery()) {


                    if (rs.next()) {
                        collectionSize = String.valueOf(rs.getLong("studyGroup_size"));
                    } else {
                        collectionSize = "0";
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return collectionSize;
        } finally {
            lock.unlock();
        }
    }


    public LocalDateTime getInitializationDate() {
        return initializationDate;
    }

    public String showAllCollection(Long userId) {
        lock.lock();

        try {
            String showCollection = """
                    SELECT * 
                    FROM studyGroup
                    WHERE user_id = ?
                    ORDER BY id ASC 
                    """;

            StringBuilder result = new StringBuilder();

            try (PreparedStatement pr = connection.prepareStatement(showCollection)) {
                pr.setLong(1, userId);
                ResultSet resultSet = pr.executeQuery();

                while (resultSet.next()) {
                    result.append("id: ")
                            .append(resultSet.getLong("id"))
                            .append("\n");

                    result.append("name: ")
                            .append(resultSet.getString("name"))
                            .append("\n");

                    result.append("coordinates_id: ")
                            .append(resultSet.getLong("coordinates_id"))
                            .append("\n");

                    result.append("creationDate: ")
                            .append(resultSet.getTimestamp("creationDate"))
                            .append("\n");

                    result.append("studentsCount: ")
                            .append(resultSet.getLong("studentsCount"))
                            .append("\n");

                    result.append("transferredStudents: ")
                            .append(resultSet.getInt("transferredStudents"))
                            .append("\n");

                    result.append("averageMark: ")
                            .append(resultSet.getFloat("averageMark"))
                            .append("\n");

                    result.append("formOfEducation: ")
                            .append(resultSet.getString("formOfEducation"))
                            .append("\n");

                    result.append("person_id: ")
                            .append(resultSet.getLong("person_id"))
                            .append("\n");

                    result.append("user_id:")
                            .append(resultSet.getLong("user_id"))
                            .append("\n");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return result.toString();
        } finally {
            lock.unlock();
        }
    }

    public StudyGroup addStudyGroup(StudyGroup studyGroup, Long userId) throws  SQLException {
        lock.lock();

        boolean previousAutoCommit = true;
        boolean transactionStarted = false;
        try {
            previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            transactionStarted = true;
            long locationIdForPerson;
            long coordinatesIdForStudyGroup;
            long personIdForStudyGroup;

            String sqlCoordinates = """
                    INSERT INTO coordinates(
                        x,
                        y
                    )
                    VALUES(?,?)
                    RETURNING id
                    """;

            try (PreparedStatement ps = connection.prepareStatement(sqlCoordinates)) {
                ps.setInt(1, studyGroup.getCoordinates().getX());
                ps.setDouble(2, studyGroup.getCoordinates().getY());


                try (ResultSet resultSet = ps.executeQuery()) {
                    if (resultSet.next()) {
                        long coordinatesId =
                                resultSet.getLong("id");

                        coordinatesIdForStudyGroup = coordinatesId;
                    } else {
                        throw new SQLException("Не удалось получить coordinates id");
                    }
                }
            }

            String sqlLocation = """
                    INSERT INTO location(
                        x,
                        y,
                        z
                    )
                    VALUES(?,?,?)
                    RETURNING id
                    """;

            if (studyGroup.getGroupAdmin().getLocation() == null) {
                locationIdForPerson = 0;
            } else {
                try (PreparedStatement statement = connection.prepareStatement(sqlLocation)) {
                    statement.setFloat(1, studyGroup.getGroupAdmin().getLocation().getX());
                    statement.setInt(2, studyGroup.getGroupAdmin().getLocation().getY());
                    statement.setFloat(3, studyGroup.getGroupAdmin().getLocation().getZ());

                    try (ResultSet resultSet = statement.executeQuery()) {
                        if (resultSet.next()) {
                            locationIdForPerson = resultSet.getLong("id");
                        } else {
                            throw new SQLException("Unable to create location");
                        }
                    }
                }
            }

            String sqlPerson = """
                    INSERT INTO person(
                        name,
                        height,
                        eyeColor,
                        hairColor,
                        nationality,
                        location_id
                    )
                    VALUES(?,?,?,?,?,?)
                    RETURNING id
                    """;
            try (PreparedStatement pr = connection.prepareStatement(sqlPerson)) {
                pr.setString(1, studyGroup.getGroupAdmin().getName());
                pr.setInt(2, studyGroup.getGroupAdmin().getHeight());
                pr.setString(3, studyGroup.getGroupAdmin().getEyeColor() == null ? null : studyGroup.getGroupAdmin().getEyeColor().name());
                pr.setString(4, studyGroup.getGroupAdmin().getHairColor() == null ? null : studyGroup.getGroupAdmin().getHairColor().name());
                pr.setString(5, studyGroup.getGroupAdmin().getNationality() == null ? null : studyGroup.getGroupAdmin().getNationality().name());
                if (locationIdForPerson == 0) {
                    pr.setNull(6, Types.BIGINT);
                } else {
                    pr.setLong(6, locationIdForPerson);
                }

                try (ResultSet resultSet = pr.executeQuery()) {
                    if (resultSet.next()) {
                        long personId =
                                resultSet.getLong("id");

                        personIdForStudyGroup = personId;
                    } else {
                        throw new SQLException("Не удалось получить location id");
                    }
                }
            }

            String sqlStudyGroup = """
                    INSERT INTO studyGroup(
                        name,
                        coordinates_id,
                        creationDate,
                        studentsCount,
                        transferredStudents,
                        averageMark,
                        formOfEducation,
                        person_id,
                        user_id
                    )
                    VALUES(?,?,?,?,?,?,?,?,?)
                    RETURNING id 
                    """;

            try (PreparedStatement pr = connection.prepareStatement(sqlStudyGroup)) {
                pr.setString(1, studyGroup.getName());
                pr.setLong(2, coordinatesIdForStudyGroup);
                pr.setTimestamp(3, Timestamp.valueOf(studyGroup.getCreationDate()));
                pr.setLong(4, studyGroup.getStudentsCount());
                pr.setInt(5, studyGroup.getTransferredStudents());
                pr.setDouble(6, studyGroup.getAverageMark());
                pr.setString(7, studyGroup.getFormOfEducation().toString());
                pr.setLong(8, personIdForStudyGroup);
                pr.setLong(9, userId);

                try (ResultSet resultSet = pr.executeQuery()) {
                    if (resultSet.next()) {
                        long studyGroupId = resultSet.getLong("id");

                        studyGroup.setId(studyGroupId);
                    } else {
                        throw new SQLException("Не удалось получить StudyGroup id");
                    }
                }
            }

            connection.commit();
            return studyGroup;
        } catch (SQLException | RuntimeException e) {
            if (transactionStarted) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    e.addSuppressed(rollbackException);
                }
            }
            throw e;
        } finally {
            if (transactionStarted) {
                try {
                    connection.setAutoCommit(previousAutoCommit);
                } catch (SQLException ignored) {
                }
            }
            lock.unlock();
        }
    }

    public StudyGroup updateStudyGroup(long id, StudyGroup studyGroup, Long userId) {
        lock.lock();

        boolean previousAutoCommit = true;
        boolean transactionStarted = false;
        try {
            previousAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);
            transactionStarted = true;
            long coordinatesId;
            long personId;
            long locationId;

            String sqlFindIds = """
                    SELECT sg.coordinates_id,
                           sg.person_id,
                           p.location_id
                    FROM studyGroup sg
                    JOIN person p ON sg.person_id = p.id
                    WHERE sg.id = ?
                      AND sg.user_id = ?
                    """;

            try (PreparedStatement pr = connection.prepareStatement(sqlFindIds)) {

                pr.setLong(1, id);
                pr.setLong(2, userId);

                try (ResultSet rs = pr.executeQuery()) {

                    if (!rs.next()) {
                        throw new RuntimeException(
                                "StudyGroup не найден или не принадлежит пользователю"
                        );
                    }

                    coordinatesId = rs.getLong("coordinates_id");
                    personId = rs.getLong("person_id");
                    locationId = rs.getLong("location_id");
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            String sqlUpdateCoordinates = """
                        UPDATE coordinates
                        SET x = ?, y = ?
                        WHERE id = ?
                    """;

            String sqlUpdateLocation = """
                        UPDATE location 
                        SET x = ?, y = ?, z = ?
                        WHERE id = ?
                    """;

            String sqlUpdatePerson = """
                       UPDATE person
                       SET name = ?, height = ?, eyeColor = ?, hairColor = ?, nationality = ?
                       WHERE id = ?
                    """;

            String sqlUpdateStudyGroup = """         
                    UPDATE studyGroup
                    SET name = ?, 
                        creationDate = ?, 
                        studentsCount = ?, 
                        transferredStudents = ?, 
                        averageMark = ?,
                        formOfEducation = ?  
                    WHERE id = ? 
                    AND user_id = ?  
                    """;

            try (PreparedStatement pr = connection.prepareStatement(sqlUpdateCoordinates)) {
                pr.setInt(1, studyGroup.getCoordinates().getX());
                pr.setDouble(2, studyGroup.getCoordinates().getY());

                pr.setLong(3, coordinatesId);

                pr.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            try (PreparedStatement pr = connection.prepareStatement(sqlUpdateLocation)) {
                pr.setFloat(1, studyGroup.getGroupAdmin().getLocation().getX());
                pr.setFloat(2, studyGroup.getGroupAdmin().getLocation().getY());
                pr.setFloat(3, studyGroup.getGroupAdmin().getLocation().getZ());
                pr.setLong(4, locationId);

                pr.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            try (PreparedStatement pr = connection.prepareStatement(sqlUpdatePerson)) {
                pr.setString(1, studyGroup.getGroupAdmin().getName());
                pr.setInt(2, studyGroup.getGroupAdmin().getHeight());
                pr.setString(3, studyGroup.getGroupAdmin().getEyeColor().name());
                pr.setString(4, studyGroup.getGroupAdmin().getHairColor().name());
                pr.setString(5, studyGroup.getGroupAdmin().getNationality().name());
                pr.setLong(6, personId);

                pr.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            try (PreparedStatement pr = connection.prepareStatement(sqlUpdateStudyGroup)) {
                pr.setString(1, studyGroup.getName());
                pr.setTimestamp(2, Timestamp.valueOf(studyGroup.getCreationDate()));
                pr.setLong(3, studyGroup.getStudentsCount());
                pr.setInt(4, studyGroup.getTransferredStudents());
                pr.setDouble(5, studyGroup.getAverageMark());
                pr.setString(6, studyGroup.getFormOfEducation().name());
                pr.setLong(7, id);
                pr.setLong(8, userId);

                pr.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            connection.commit();
            return studyGroup;
        } catch (RuntimeException | SQLException e) {
            if (transactionStarted) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    e.addSuppressed(rollbackException);
                }
            }
            throw new RuntimeException(e);
        } finally {
            if (transactionStarted) {
                try {
                    connection.setAutoCommit(previousAutoCommit);
                } catch (SQLException ignored) {
                }
            }
            lock.unlock();
        }
    }

    public String showFirstElement(Long userId) {
        lock.lock();

        try {
            String sqlShowFirst = """
                    SELECT * 
                    FROM studyGroup 
                    WHERE user_id = ?
                    ORDER BY id ASC 
                    LIMIT 1
                    """;

            try (PreparedStatement pr = connection.prepareStatement(sqlShowFirst)) {
                pr.setLong(1, userId);
                try (ResultSet rs = pr.executeQuery()) {

                    while (rs.next()) {
                        return "id: " + rs.getLong("id")
                                + ", name " + rs.getString("name");
                    }
                    return "Объект не найден / null";
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } finally {
            lock.unlock();
        }
    }

    public String showAverageMark(Long userId) {
        lock.lock();

        try {
            String avgResult;
            String sqlAverageMark = """
                    SELECT AVG(averageMark) as average_mark
                    FROM studyGroup
                    WHERE user_id = ?
                    """;

            try (PreparedStatement pr = connection.prepareStatement(sqlAverageMark)) {
                pr.setLong(1, userId);
                try (ResultSet rs = pr.executeQuery()) {
                    if (rs.next()) {
                        return avgResult = String.valueOf(rs.getDouble("average_mark"));
                    }
                    return "0 / Оценки не найдены";
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } finally {
            lock.unlock();
        }
    }

    public String showContainsName(String name, Long userId) {
        lock.lock();

        try {
            String sqlContainsName = """
                    SELECT * 
                    FROM studyGroup
                    WHERE name LIKE ? 
                    AND user_id = ?
                    ORDER BY id DESC
                    """;

            StringBuilder result = new StringBuilder();

            try (PreparedStatement pr = connection.prepareStatement(sqlContainsName)) {
                pr.setString(1, "%" + name + "%");
                pr.setLong(2, userId);

                try (ResultSet rs = pr.executeQuery()) {
                    while (rs.next()) {
                        result.append("id :")
                                .append(rs.getLong("id"))
                                .append("\n");

                        result.append("name :")
                                .append(rs.getString("name"))
                                .append("\n");
                    }
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            return result.toString();
        } finally {
            lock.unlock();
        }
    }

    public String showGreaterThanAverageMark(double averageMark, Long userId) {
        lock.lock();

        try {
            String sqlGreaterThan = """
                    SELECT *
                    FROM studyGroup
                    WHERE averageMark > ?
                    AND user_id = ?
                    """;

            StringBuilder stringBuilder = new StringBuilder();

            try (PreparedStatement pr = connection.prepareStatement(sqlGreaterThan)) {
                pr.setDouble(1, averageMark);
                pr.setLong(2, userId);
                try (ResultSet rs = pr.executeQuery()) {
                    while (rs.next()) {
                        stringBuilder
                                .append("id: ")
                                .append(rs.getLong("id"))
                                .append("name: ")
                                .append(rs.getString("name"));
                    }

                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return stringBuilder.toString();
        } finally {
            lock.unlock();
        }
    }

    //DELETE в случае конкретного id
    public void deleteById(long id, Long userId ) {
        lock.lock();

        try {
            String sqlDeleteById = """
                    DELETE FROM studyGroup
                    WHERE id = ?
                    AND user_id = ?
                    """;

            try (PreparedStatement ps = connection.prepareStatement(sqlDeleteById)) {
                ps.setLong(1, id);
                ps.setLong(2, userId);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } finally {
            lock.unlock();
        }
    }

    //TRUNCATE в случае обычного очищения всех записей в таблице
    public void deleteAll(Long userId) {
        lock.lock();

        try {
            String sqlDeleteAll = """
                    DELETE FROM studyGroup
                    WHERE user_id = ?
                    """;
            try (PreparedStatement ps = connection.prepareStatement(sqlDeleteAll)) {
                ps.setLong(1, userId);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } finally {
            lock.unlock();
        }
    }

    public void deleteFirst(Long userId) {
        lock.lock();

        try {
            String sqlDeleteFirst = """
                    DELETE FROM studyGroup
                    WHERE id = (
                               SELECT MIN(id)
                               FROM studyGroup
                               WHERE user_id = ?
                    )      
                    """;
            try (PreparedStatement ps = connection.prepareStatement(sqlDeleteFirst)) {
                ps.setLong(1, userId);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } finally {
            lock.unlock();
        }
    }

    public void removeGreater(long element, Long userId) {
        lock.lock();

        try {
            String removeGreater = """
                    DELETE FROM studyGroup
                    WHERE id > ? 
                    AND user_id = ?
                    """;
            try (PreparedStatement ps = connection.prepareStatement(removeGreater)) {
                ps.setLong(1, element);
                ps.setLong(2, userId);
                ps.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        } finally {
            lock.unlock();
        }
    }

    /*public Optional<StudyGroup> findById(long id) {
        return collection
                .stream()
                .filter(group -> group.getId() == id)
                .findFirst();
    }*/
}
