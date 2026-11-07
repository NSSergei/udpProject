package dataBase;

import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class DatabaseInitializerTest {
    @Test
    void createsSequencesTablesAndIndexes() throws Exception {
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        when(connection.createStatement()).thenReturn(statement);

        DatabaseInitializer.initialize(connection);

        verify(statement, times(12)).executeUpdate(anyString());
        verify(statement).executeUpdate(contains("CREATE TABLE IF NOT EXISTS users"));
        verify(statement).executeUpdate(contains("study_group_user_id_idx"));
        verify(statement).executeUpdate(contains("study_group_user_name_idx"));
        verify(statement).close();
    }

    @Test
    void propagatesSchemaInitializationErrors() throws Exception {
        Connection connection = mock(Connection.class);
        Statement statement = mock(Statement.class);
        when(connection.createStatement()).thenReturn(statement);
        doThrow(new SQLException("database unavailable")).when(statement).executeUpdate(anyString());

        assertThrows(SQLException.class, () -> DatabaseInitializer.initialize(connection));
        verify(statement).close();
    }
}
