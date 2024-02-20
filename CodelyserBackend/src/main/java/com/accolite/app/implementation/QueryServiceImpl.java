package com.accolite.app.implementation;

import com.accolite.app.dto.CodeDTO;
import com.accolite.app.entity.Question;
import com.accolite.app.entity.TestSubmission;
import com.accolite.app.exception.QuestionIDNotFoundException;
import com.accolite.app.exception.SQLSyntaxErrorException;
import com.accolite.app.repository.QuestionRepository;
import com.accolite.app.repository.TestSubmissionRepository;
import com.accolite.app.service.QueryService;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.sql.*;
import java.util.*;


@Service
public class QueryServiceImpl implements QueryService {
    @Autowired
    TestSubmissionRepository testSubmissionRepository;
    @Autowired
    QuestionRepository questionRepository;
    @Autowired
    private EntityManager entityManager;

    List<Object> resultList;

    private Set<String> createdTables = new HashSet<>();
    private static final String JDBC_URL = "jdbc:hsqldb:mem:testdb";
    private static final String JDBC_USER = "sa";
    private static final String JDBC_PASSWORD = "";
    @Override
    public List<Object> submitSQlQuery(CodeDTO code) throws IOException, InterruptedException {
        resultList=new ArrayList<>();
        if (code.isSubmitStatus()) {
            Optional<Question> questionOptional = questionRepository.findById(code.getQuestionId());
            if (questionOptional == null) {
                throw new QuestionIDNotFoundException("Question id is not found");
            }
            try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
                Question sqlQuestion = questionOptional.get();
                executeHSQLCommands(sqlQuestion.getCommands(), connection);
                // Execute the first query
                ResultSet resultSet1 = executeQuery(connection, code.getSubmittedCode());

                // Execute the second query
                ResultSet resultSet2 = executeQuery(connection, sqlQuestion.getQuery());

                // Compare the results
                boolean areEqual = compareResultSets(resultSet1, resultSet2);
                testSubmissionRepository.save(new TestSubmission(code.getCandidateId(), code.getQuestionId(), code.getSubmittedCode(), areEqual));

                // Output the result of the comparison
                System.out.println(areEqual);
                if (areEqual) {
                    resultList.add("Result Passed");
                } else {
                    resultList.add("Result Failed");
                }

            } catch (SQLException e) {

                throw new SQLSyntaxErrorException("Error executing SQL query: " + e.getMessage());
            }

        } else {
            try {
                Optional<Question> questionOptional = questionRepository.findById(code.getQuestionId());
                if (questionOptional == null) {
                    throw new QuestionIDNotFoundException("Question id is not found");
                }
                try (Connection connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD)) {
                    Question sqlQuestion = questionOptional.get();

                    executeHSQLCommands(sqlQuestion.getCommands(), connection);
                    // Execute the first query
                    ResultSet resultSet = executeQuery(connection, code.getSubmittedCode());
                    // Process the ResultSet and convert it to a List<Object>

                    ResultSetMetaData metaData = resultSet.getMetaData();
                    int columnCount = metaData.getColumnCount();

                    while (resultSet.next()) {
                        Map<String, Object> row = new HashMap<>();
                        for (int i = 1; i <= columnCount; i++) {
                            String columnName = metaData.getColumnName(i);
                            Object value = resultSet.getObject(i);
                            row.put(columnName, value);
                        }
                        resultList.add(row);
                    }


                } catch (SQLException e) {
                    //resultList.add(e.getMessage());
                    throw new SQLSyntaxErrorException("Error executing SQL query: " + e.getMessage());
                }
            } catch (Exception e) {
                //resultList.add(e.getMessage());
                // Handle the exception or log it as needed
                throw new SQLSyntaxErrorException("Error executing SQL query: " + e.getMessage());
            }
        }
        return resultList;

    }

    private static ResultSet executeQuery(Connection connection, String query) throws SQLException {
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            //e.printStackTrace(); // Add proper logging in a real application
            throw new SQLSyntaxErrorException("Error executing SQL query: " + e.getMessage());
        }
    }

    private static boolean compareResultSets(ResultSet resultSet1, ResultSet resultSet2) throws SQLException {

        while (resultSet1.next() && resultSet2.next()) {
            for (int i = 1; i <= resultSet1.getMetaData().getColumnCount(); i++) {
                Object value1 = resultSet1.getObject(i);
                Object value2 = resultSet2.getObject(i);

                // Compare values, modify this part based on your comparison logic
                if (!value1.equals(value2)) {
                    return false;
                }
            }
        }
        resultSet2.next();
        // If one result set has more rows than the other, they are not equal
        return resultSet1.isAfterLast() && resultSet2.isAfterLast();
    }

    public void executeHSQLCommands(String commands, Connection connection) throws SQLException {
        System.out.println(createdTables);
        Set<String> currentlyCreatedTables= new HashSet<>();
        Statement statement = connection.createStatement();
        String[] sqlCommands = commands.split("\n");
        for(String s : sqlCommands ){
            System.out.println(s);
        }

        for (String sqlCommand : sqlCommands) {
            System.out.println(sqlCommand);
            if (!sqlCommand.trim().isEmpty()) {
                if (isCreateTableCommand(sqlCommand)) {
                    String tableName = getTableNameFromCreateStatement(sqlCommand);
                    if (doesTableExist(connection.getMetaData(), tableName)) {
                        System.out.println("Table '" + tableName + "' already exists. Skipping table creation.");
                    } else {
                        currentlyCreatedTables.add(tableName);
                        statement.execute(sqlCommand);
                    }
                } else if (isInsertStatement(sqlCommand)) {
                    String tableName = getTableNameFromInsertStatement(sqlCommand);
                    if (currentlyCreatedTables.contains(tableName)) {
                        try {
                            boolean result = statement.execute(sqlCommand);
                            System.out.println("Insert statement executed: " + result);
                        } catch (SQLException e) {
                            System.out.println("Error executing insert statement: " + e.getMessage());
                        }
                        System.out.println(sqlCommand);
                    } else {
                        System.out.println("Skipping insert statement for table '" + tableName + "'. Table does already exist.");
                    }
                } else {
                    statement.executeUpdate(sqlCommand);
                }
            }
        }
        List<String> tables = getTableNames(connection.getMetaData());
        System.out.println(tables);
    }

    private boolean isInsertStatement(String sqlCommand) {
        // Check if the command contains the keyword "INSERT"
        return sqlCommand.trim().toUpperCase().startsWith("INSERT");
    }

    private String getTableNameFromInsertStatement(String sqlCommand) {
        // Extract table name from the VALUES clause of the INSERT statement
        String[] parts = sqlCommand.split(" ");
        for (int i = 0; i < parts.length; i++) {
            if ("INTO".equalsIgnoreCase(parts[i])) {
                // The table name is usually the next part after "INTO"
                return parts[i + 1].trim();
            }
        }
        throw new IllegalArgumentException("Unable to extract table name from INSERT statement: " + sqlCommand);
    }

    private List<String> getTableNames(DatabaseMetaData metaData) throws SQLException {
        List<String> tables = new ArrayList<>();
        try (ResultSet resultSet = metaData.getTables(null, null, "%", null)) {
            while (resultSet.next()) {
                tables.add(resultSet.getString("TABLE_NAME"));
            }
        }
        return tables;
    }

    private boolean isCreateTableCommand(String sqlCommand) {
        // Simple check for CREATE TABLE statement
        return sqlCommand.trim().toUpperCase().startsWith("CREATE TABLE");
    }

    private String getTableNameFromCreateStatement(String createStatement) {
        // Extract table name from CREATE TABLE statement
        String[] parts = createStatement.split(" ");
        return parts[2].trim(); // Assuming the table name is the third part in the statement
    }

    private boolean doesTableExist(DatabaseMetaData metaData, String tableName) throws SQLException {
        try (ResultSet tables = metaData.getTables(null, null, tableName.toUpperCase(), null)) {

            return tables.next();
        }
    }










}



