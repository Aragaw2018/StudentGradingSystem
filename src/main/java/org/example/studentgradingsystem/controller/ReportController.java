package org.example.studentgradingsystem.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.example.studentgradingsystem.database.DBConnection;
import org.example.studentgradingsystem.model.Result;
import org.example.studentgradingsystem.util.UserSession;

import java.sql.*;

public class ReportController {
    @FXML private TextField searchField;
    @FXML private TableView<Result> reportTable;
    @FXML private TableColumn<Result, String> colStudent, colSubject, colGrade, colStatus;
    @FXML private TableColumn<Result, Double> colMarks;

    private ObservableList<Result> allResults = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colStudent.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        colSubject.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        colMarks.setCellValueFactory(new PropertyValueFactory<>("marks"));
        colGrade.setCellValueFactory(new PropertyValueFactory<>("grade"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadTeacherReports();
    }

    private void loadTeacherReports() {
        allResults.clear();
        String query = "SELECT r.result_id, s.first_name, s.last_name, sub.subject_name, r.marks, r.grade, r.status, s.student_id " +
                "FROM results r JOIN students s ON r.student_id = s.student_id " +
                "JOIN subjects sub ON r.subject_id = sub.subject_id " +
                "JOIN teachers t ON sub.teacher_id = t.teacher_id " +
                "WHERE t.user_id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, UserSession.getUserId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                allResults.add(new Result(rs.getInt("result_id"), rs.getString("student_id"),
                        rs.getString("first_name") + " " + rs.getString("last_name"),
                        rs.getString("subject_name"), rs.getDouble("marks"),
                        rs.getString("grade"), rs.getString("status")));
            }
            reportTable.setItems(allResults);
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        FilteredList<Result> filteredData = new FilteredList<>(allResults, p -> true);

        filteredData.setPredicate(result -> {
            if (searchText == null || searchText.isEmpty()) return true;
            return result.getStudentName().toLowerCase().contains(searchText);
        });

        reportTable.setItems(filteredData);
    }

    @FXML
    private void handleExportCSV() {
        // Professional Export Logic: Creating a simple CSV output in the console for now
        System.out.println("Exporting Student Reports to CSV...");
        System.out.println("Student Name, Subject, Marks, Grade, Status");
        for (Result r : allResults) {
            System.out.println(r.getStudentName() + ", " + r.getSubjectName() + ", " +
                    r.getMarks() + ", " + r.getGrade() + ", " + r.getStatus());
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Export Success");
        alert.setHeaderText(null);
        alert.setContentText("Report exported successfully to System Console!");
        alert.showAndWait();
    }
}