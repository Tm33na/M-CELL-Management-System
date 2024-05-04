import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@WebListener
public class DeadlineNotificationScheduler implements ServletContextListener {

    private static final long INTERVAL_MILLIS = 60 * 60 * 1000; // 30 minutes in milliseconds

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        new Thread(() -> {
            while (true) {
                try {
                    generateDeadlineNotifications();
                    Thread.sleep(INTERVAL_MILLIS);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Cleanup resources if needed
    }

    private void generateDeadlineNotifications() {
        try (Connection con = DatabaseManager.getConnection()) {
            LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime deadlineThreshold = currentDateTime.plusHours(24);

            
            // Query to retrieve complaints not resolved, with status "Accept" in task_assignment, and with deadline within 24 hours
            String query = "SELECT c.token, t.engineer_id, t.deadline_date " +
                           "FROM complaints c " +
                           "JOIN task_assignment t ON c.token = t.complaint_token " +
                           "WHERE c.Status != 'resolved' AND t.status = 'Accept' AND t.deadline_date BETWEEN ? AND ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setTimestamp(1, Timestamp.valueOf(currentDateTime));
            statement.setTimestamp(2, Timestamp.valueOf(deadlineThreshold));

            ResultSet resultSet = statement.executeQuery();

            // Generate and insert notifications
            while (resultSet.next()) {
                String complaintToken = resultSet.getString("token");
                String engineerId = resultSet.getString("engineer_id");
                LocalDateTime deadlineDateTime = resultSet.getTimestamp("deadline_date").toLocalDateTime();

                long hoursRemaining = ChronoUnit.HOURS.between(currentDateTime, deadlineDateTime);
                String message = "Complaint with token " + complaintToken + " is still not resolved. " +
                                 "Only " + hoursRemaining + " hour(s) left until the deadline.";
                insertNotification(con, engineerId, message);
            }
        } catch (SQLException e) {
            // Handle database error
            e.printStackTrace();
        }
    }

    private void insertNotification(Connection con, String engineerId, String message) throws SQLException {
        String insertQuery = "INSERT INTO tasknotification (receiver_id, message) VALUES (?, ?)";
        try (PreparedStatement insertStatement = con.prepareStatement(insertQuery)) {
            insertStatement.setString(1, engineerId);
            insertStatement.setString(2, message);
            insertStatement.executeUpdate();
        }
    }
}
