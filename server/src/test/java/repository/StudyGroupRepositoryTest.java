package repository;

import enums.FormOfEducation;
import model.Coordinates;
import model.Location;
import model.Person;
import model.StudyGroup;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class StudyGroupRepositoryTest {
    @Test
    void returnsCollectionSize() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(true);
        when(result.getLong("studyGroup_size")).thenReturn(4L);

        assertEquals("4", new StudyGroupRepository(connection).showInfo(9L));
        verify(statement).setLong(1, 9L);
        verify(result).close();
    }

    @Test
    void formatsCollectionRows() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(true, false);
        when(result.getLong("id")).thenReturn(11L);
        when(result.getString("name")).thenReturn("Algorithms");
        when(result.getLong("coordinates_id")).thenReturn(3L);
        when(result.getTimestamp("creationDate")).thenReturn(Timestamp.valueOf(LocalDateTime.of(2026, 2, 3, 4, 5)));
        when(result.getLong("studentsCount")).thenReturn(40L);
        when(result.getInt("transferredStudents")).thenReturn(2);
        when(result.getFloat("averageMark")).thenReturn(4.5f);
        when(result.getString("formOfEducation")).thenReturn("FULL_TIME_EDUCATION");
        when(result.getLong("person_id")).thenReturn(8L);
        when(result.getLong("user_id")).thenReturn(9L);

        String collection = new StudyGroupRepository(connection).showAllCollection(9L);

        assertTrue(collection.contains("id: 11"));
        assertTrue(collection.contains("name: Algorithms"));
        assertTrue(collection.contains("averageMark: 4.5"));
        assertTrue(collection.contains("user_id:9"));
        verify(result).close();
    }

    @Test
    void queriesFirstElementAndAverage() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement firstStatement = mock(PreparedStatement.class);
        PreparedStatement averageStatement = mock(PreparedStatement.class);
        ResultSet firstResult = mock(ResultSet.class);
        ResultSet averageResult = mock(ResultSet.class);
        when(connection.prepareStatement(anyString())).thenReturn(firstStatement, averageStatement);
        when(firstStatement.executeQuery()).thenReturn(firstResult);
        when(firstResult.next()).thenReturn(true);
        when(firstResult.getLong("id")).thenReturn(5L);
        when(firstResult.getString("name")).thenReturn("Biology");
        when(averageStatement.executeQuery()).thenReturn(averageResult);
        when(averageResult.next()).thenReturn(true);
        when(averageResult.getDouble("average_mark")).thenReturn(3.75);
        StudyGroupRepository repository = new StudyGroupRepository(connection);

        assertEquals("id: 5, name Biology", repository.showFirstElement(2L));
        assertEquals("3.75", repository.showAverageMark(2L));
        assertTrue(repository.getInitializationDate().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void returnsEmptyValuesForQueriesWithoutMatches() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        ResultSet result = mock(ResultSet.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        when(statement.executeQuery()).thenReturn(result);
        when(result.next()).thenReturn(false);
        StudyGroupRepository repository = new StudyGroupRepository(connection);

        assertEquals("0", repository.showInfo(4L));
        assertEquals("", repository.showAllCollection(4L));
        assertEquals("Объект не найден / null", repository.showFirstElement(4L));
        assertEquals("0 / Оценки не найдены", repository.showAverageMark(4L));
        assertEquals("", repository.showContainsName("absent", 4L));
        assertEquals("", repository.showGreaterThanAverageMark(4.0, 4L));
    }

    @Test
    void filtersByNameAndAverage() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement nameStatement = mock(PreparedStatement.class);
        PreparedStatement averageStatement = mock(PreparedStatement.class);
        ResultSet nameResult = mock(ResultSet.class);
        ResultSet averageResult = mock(ResultSet.class);
        when(connection.prepareStatement(anyString())).thenReturn(nameStatement, averageStatement);
        when(nameStatement.executeQuery()).thenReturn(nameResult);
        when(nameResult.next()).thenReturn(true, false);
        when(nameResult.getLong("id")).thenReturn(6L);
        when(nameResult.getString("name")).thenReturn("Java basics");
        when(averageStatement.executeQuery()).thenReturn(averageResult);
        when(averageResult.next()).thenReturn(true, false);
        when(averageResult.getLong("id")).thenReturn(7L);
        when(averageResult.getString("name")).thenReturn("Math");
        StudyGroupRepository repository = new StudyGroupRepository(connection);

        assertTrue(repository.showContainsName("Java", 3L).contains("Java basics"));
        assertTrue(repository.showGreaterThanAverageMark(4.0, 3L).contains("Math"));
        verify(nameStatement).setString(1, "%Java%");
        verify(nameStatement).setLong(2, 3L);
        verify(averageStatement).setDouble(1, 4.0);
    }

    @Test
    void executesUserScopedDeletes() throws Exception {
        Connection connection = mock(Connection.class);
        PreparedStatement statement = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(statement);
        StudyGroupRepository repository = new StudyGroupRepository(connection);

        repository.deleteById(12L, 7L);
        repository.deleteAll(7L);
        repository.deleteFirst(7L);
        repository.removeGreater(12L, 7L);

        verify(statement, times(4)).executeUpdate();
        verify(statement, times(2)).setLong(1, 12L);
        verify(statement, times(2)).setLong(1, 7L);
        verify(statement, times(2)).setLong(2, 7L);
    }

    @Test
    void createsGroupAndCommitsTheTransaction() throws Exception {
        Connection connection = mock(Connection.class);
        when(connection.getAutoCommit()).thenReturn(true);
        PreparedStatement coordinates = mock(PreparedStatement.class);
        PreparedStatement person = mock(PreparedStatement.class);
        PreparedStatement group = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(coordinates, person, group);
        ResultSet coordinatesResult = resultWithId(3L);
        ResultSet personResult = resultWithId(4L);
        ResultSet groupResult = resultWithId(5L);
        when(coordinates.executeQuery()).thenReturn(coordinatesResult);
        when(person.executeQuery()).thenReturn(personResult);
        when(group.executeQuery()).thenReturn(groupResult);
        StudyGroup studyGroup = validGroup();

        StudyGroup created = new StudyGroupRepository(connection).addStudyGroup(studyGroup, 9L);

        assertSame(studyGroup, created);
        assertEquals(5L, created.getId());
        verify(connection).setAutoCommit(false);
        verify(connection).commit();
        verify(connection).setAutoCommit(true);
        verify(group).setLong(9, 9L);
    }

    @Test
    void rollsBackGroupCreationOnSqlFailure() throws Exception {
        Connection connection = mock(Connection.class);
        when(connection.getAutoCommit()).thenReturn(true);
        PreparedStatement coordinates = mock(PreparedStatement.class);
        PreparedStatement person = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(coordinates, person);
        ResultSet coordinatesResult = resultWithId(3L);
        when(coordinates.executeQuery()).thenReturn(coordinatesResult);
        when(person.executeQuery()).thenThrow(new SQLException("write failed"));

        assertThrows(SQLException.class, () -> new StudyGroupRepository(connection).addStudyGroup(validGroup(), 9L));

        verify(connection).rollback();
        verify(connection).setAutoCommit(true);
        verify(connection, never()).commit();
    }

    @Test
    void updatesAllGroupRowsAndCommitsAtomically() throws Exception {
        Connection connection = mock(Connection.class);
        when(connection.getAutoCommit()).thenReturn(true);
        PreparedStatement find = mock(PreparedStatement.class);
        PreparedStatement coordinates = mock(PreparedStatement.class);
        PreparedStatement location = mock(PreparedStatement.class);
        PreparedStatement person = mock(PreparedStatement.class);
        PreparedStatement group = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(find, coordinates, location, person, group);
        ResultSet ids = mock(ResultSet.class);
        when(find.executeQuery()).thenReturn(ids);
        when(ids.next()).thenReturn(true);
        when(ids.getLong("coordinates_id")).thenReturn(3L);
        when(ids.getLong("person_id")).thenReturn(4L);
        when(ids.getLong("location_id")).thenReturn(5L);
        StudyGroup updatedGroup = new StudyGroup("Java", new Coordinates(1, 2.0),
                LocalDateTime.of(2026, 1, 1, 0, 0), 20, 3, 4.2,
                FormOfEducation.FULL_TIME_EDUCATION,
                new Person("Admin", 180, null, null, null, new Location(1.0f, 2, 3.0f)));

        assertSame(updatedGroup, new StudyGroupRepository(connection).updateStudyGroup(15L, updatedGroup, 9L));

        verify(coordinates).executeUpdate();
        verify(location).executeUpdate();
        verify(person).executeUpdate();
        verify(group).executeUpdate();
        verify(connection).commit();
        verify(connection).setAutoCommit(true);
    }

    @Test
    void rollsBackUpdateWhenTargetDoesNotExist() throws Exception {
        Connection connection = mock(Connection.class);
        when(connection.getAutoCommit()).thenReturn(true);
        PreparedStatement find = mock(PreparedStatement.class);
        ResultSet ids = mock(ResultSet.class);
        when(connection.prepareStatement(anyString())).thenReturn(find);
        when(find.executeQuery()).thenReturn(ids);
        when(ids.next()).thenReturn(false);

        assertThrows(RuntimeException.class,
                () -> new StudyGroupRepository(connection).updateStudyGroup(15L, validGroup(), 9L));

        verify(connection).rollback();
        verify(connection).setAutoCommit(true);
        verify(connection, never()).commit();
    }

    @Test
    void createsLocationWhenUpdatingGroupThatDidNotHaveOne() throws Exception {
        Connection connection = mock(Connection.class);
        when(connection.getAutoCommit()).thenReturn(true);
        PreparedStatement find = mock(PreparedStatement.class);
        PreparedStatement coordinates = mock(PreparedStatement.class);
        PreparedStatement insertLocation = mock(PreparedStatement.class);
        PreparedStatement person = mock(PreparedStatement.class);
        PreparedStatement group = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(find, coordinates, insertLocation, person, group);
        ResultSet ids = mock(ResultSet.class);
        when(find.executeQuery()).thenReturn(ids);
        when(ids.next()).thenReturn(true);
        when(ids.getLong("coordinates_id")).thenReturn(3L);
        when(ids.getLong("person_id")).thenReturn(4L);
        when(ids.getLong("location_id")).thenReturn(0L);
        ResultSet locationResult = resultWithId(8L);
        when(insertLocation.executeQuery()).thenReturn(locationResult);
        StudyGroup groupWithLocation = new StudyGroup("Java", new Coordinates(1, 2.0),
                LocalDateTime.of(2026, 1, 1, 0, 0), 20, 3, 4.2,
                FormOfEducation.FULL_TIME_EDUCATION,
                new Person("Admin", 180, null, null, null, new Location(1.0f, 2, 3.0f)));

        new StudyGroupRepository(connection).updateStudyGroup(15L, groupWithLocation, 9L);

        verify(insertLocation).executeQuery();
        verify(person).setLong(6, 8L);
        verify(connection).commit();
    }

    @Test
    void clearsAndDeletesOptionalLocationWhenUpdatingGroup() throws Exception {
        Connection connection = mock(Connection.class);
        when(connection.getAutoCommit()).thenReturn(true);
        PreparedStatement find = mock(PreparedStatement.class);
        PreparedStatement coordinates = mock(PreparedStatement.class);
        PreparedStatement person = mock(PreparedStatement.class);
        PreparedStatement deleteLocation = mock(PreparedStatement.class);
        PreparedStatement group = mock(PreparedStatement.class);
        when(connection.prepareStatement(anyString())).thenReturn(find, coordinates, person, deleteLocation, group);
        ResultSet ids = mock(ResultSet.class);
        when(find.executeQuery()).thenReturn(ids);
        when(ids.next()).thenReturn(true);
        when(ids.getLong("coordinates_id")).thenReturn(3L);
        when(ids.getLong("person_id")).thenReturn(4L);
        when(ids.getLong("location_id")).thenReturn(5L);

        new StudyGroupRepository(connection).updateStudyGroup(15L, validGroup(), 9L);

        verify(person).setNull(6, java.sql.Types.BIGINT);
        verify(deleteLocation).setLong(1, 5L);
        verify(deleteLocation).executeUpdate();
        verify(connection).commit();
    }

    private static ResultSet resultWithId(long id) throws Exception {
        ResultSet result = mock(ResultSet.class);
        when(result.next()).thenReturn(true);
        when(result.getLong("id")).thenReturn(id);
        return result;
    }

    private static StudyGroup validGroup() {
        return new StudyGroup("Java", new Coordinates(1, 2.0), LocalDateTime.of(2026, 1, 1, 0, 0),
                20, 3, 4.2, FormOfEducation.FULL_TIME_EDUCATION,
                new Person("Admin", 180, null, null, null, null));
    }
}
