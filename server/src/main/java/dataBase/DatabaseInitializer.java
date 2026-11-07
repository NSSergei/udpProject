package dataBase;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {

    static String coordinatesSequence = """
            CREATE SEQUENCE IF NOT EXISTS coordinates_id_seq
            START WITH 1
            INCREMENT BY 1
            """;

    static String locationSequence = """
            CREATE SEQUENCE IF NOT EXISTS location_id_seq
            START WITH 1 
            INCREMENT BY 1
            """;

    static String personSequence = """
            CREATE SEQUENCE IF NOT EXISTS person_id_seq
            START WITH 1 
            INCREMENT BY 1
            """;

    static String studyGroupSequence = """
            CREATE SEQUENCE IF NOT EXISTS study_group_id_seq
            START WITH 1 
            INCREMENT BY 1
            """;

    static String usersSequence = """
            CREATE SEQUENCE IF NOT EXISTS users_id_seq
            START WITH 1
            INCREMENT BY 1 
            """;

    static String coordinatesSql = """
            CREATE TABLE IF NOT EXISTS coordinates(
                id BIGINT PRIMARY KEY DEFAULT nextval('coordinates_id_seq'), 
                x INTEGER NOT NULL,
                y DOUBLE PRECISION NOT NULL 
            )
            """;

    static String locationSql = """
            CREATE TABLE IF NOT EXISTS location(
                id BIGINT PRIMARY KEY DEFAULT nextval('location_id_seq'),
                x REAL NOT NULL,
                y INTEGER NOT NULL,
                z REAL NOT NULL
            )
            """;

    static String usersSql = """
            CREATE TABLE IF NOT EXISTS users(
                id BIGINT PRIMARY KEY DEFAULT nextval('users_id_seq'),
                login VARCHAR(255) UNIQUE NOT NULL,
                password VARCHAR(255) NOT NULL
                )
            """;

    static String personSql = """
            CREATE TABLE IF NOT EXISTS person(
                id BIGINT PRIMARY KEY DEFAULT nextval('person_id_seq'),
                name VARCHAR(255) NOT NULL,
                height INTEGER NOT NULL,
                eyeColor TEXT ,
                hairColor TEXT,
                nationality TEXT,
                location_id BIGINT REFERENCES location(id)
            )
            """;

    static String studyGroupSql = """
            CREATE TABLE IF NOT EXISTS studyGroup(
                id BIGINT PRIMARY KEY DEFAULT nextval('study_group_id_seq'),
                name VARCHAR(255) NOT NULL,
                coordinates_id BIGINT REFERENCES coordinates(id),
                creationDate TIMESTAMP NOT NULL,
                studentsCount BIGINT NOT NULL,
                transferredStudents INTEGER NOT NULL,
                averageMark REAL NOT NULL,
                formOfEducation TEXT NOT NULL,
                person_id BIGINT REFERENCES person(id),
                user_id BIGINT REFERENCES users(id)
            )
            """;

    static String studyGroupUserIndexSql = "CREATE INDEX IF NOT EXISTS study_group_user_id_idx ON studyGroup(user_id)";
    static String studyGroupUserNameIndexSql = "CREATE INDEX IF NOT EXISTS study_group_user_name_idx ON studyGroup(user_id, name)";

    public static void initialize(Connection connection) throws SQLException {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(coordinatesSequence);
            statement.executeUpdate(locationSequence);
            statement.executeUpdate(usersSequence);
            statement.executeUpdate(personSequence);
            statement.executeUpdate(studyGroupSequence);

            statement.executeUpdate(coordinatesSql);
            statement.executeUpdate(locationSql);
            statement.executeUpdate(usersSql);
            statement.executeUpdate(personSql);
            statement.executeUpdate(studyGroupSql);
            statement.executeUpdate(studyGroupUserIndexSql);
            statement.executeUpdate(studyGroupUserNameIndexSql);
        }
    }
}
