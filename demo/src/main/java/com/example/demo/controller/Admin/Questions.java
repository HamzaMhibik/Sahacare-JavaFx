package com.example.demo.controller.Admin;

import com.example.demo.database.DatabaseConnection;
import com.example.demo.model.Question;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class Questions implements Initializable {

    @FXML
    private TableView<Question> questionsTable;

    @FXML
    private TableColumn<Question, Integer> idColumn;

    @FXML
    private TableColumn<Question, String> questionColumn;

    @FXML
    private TableColumn<Question, String> usernameColumn;

    @FXML
    private TextField questionFilter;

    @FXML
    private TextField usernameFilter;

    private ObservableList<Question> questionsList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        questionColumn.setCellValueFactory(new PropertyValueFactory<>("question"));
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        loadQuestions(); // Initial load of all questions
        questionsTable.setItems(questionsList);

        addDeleteButtonColumn(); // Add delete button column
    }

    private void loadQuestions() {
        String query = "SELECT q.id, q.question, q.answer, q.user_id, u.username " +
                "FROM questions q " +
                "JOIN users u ON q.user_id = u.id"; // Retrieve the 'answer' field from the database

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            // Loop through the result set and create a Question object for each record
            while (resultSet.next()) {
                Question question = new Question(
                        resultSet.getInt("id"),
                        resultSet.getString("question"),
                        resultSet.getString("answer"), // Retrieve 'answer' from the result set
                        resultSet.getInt("user_id"),
                        resultSet.getString("username")
                );
                questionsList.add(question); // Add the question object to the list
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    private void addDeleteButtonColumn() {
        TableColumn<Question, Void> actionsColumn = new TableColumn<>("Actions");
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteButton = new Button("Supprimer");

            {
                deleteButton.setOnAction(event -> {
                    Question question = getTableView().getItems().get(getIndex());
                    deleteQuestion(question.getId());
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(deleteButton);
                }
            }
        });

        questionsTable.getColumns().add(actionsColumn);
    }

    private void deleteQuestion(int id) {
        String query = "DELETE FROM questions WHERE id = ?";
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setInt(1, id);
            statement.executeUpdate();
            questionsList.removeIf(question -> question.getId() == id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void applyFilters() {
        String questionFilterText = questionFilter.getText().toLowerCase();
        String usernameFilterText = usernameFilter.getText().toLowerCase();

        ObservableList<Question> filteredList = FXCollections.observableArrayList();

        for (Question question : questionsList) {
            boolean matchesQuestion = question.getQuestion().toLowerCase().contains(questionFilterText);
            boolean matchesUsername = question.getUsername().toLowerCase().contains(usernameFilterText);

            if (matchesQuestion && matchesUsername) {
                filteredList.add(question);
            }
        }

        questionsTable.setItems(filteredList);
    }

    // Optionally, you can also add key press event to live filter.
    @FXML
    private void onFilterKeyReleased(KeyEvent event) {
        applyFilters();
    }
}
