import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class SubmitFeedbackServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");

        // Insert data into the database
        boolean inserted = insertFeedback(name, email, subject, message);

        // Redirect to appropriate page based on insertion result
        if (inserted) {
            response.sendRedirect("thank_you.html");
        } else {
            response.sendRedirect("error.html");
        }
    }

    private boolean insertFeedback(String name, String email, String subject, String message) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            // Get a connection from the DatabaseManager
            connection = DatabaseManager.getConnection();
            String query = "INSERT INTO feedback (name, email, subject, message, created_at) VALUES (?, ?, ?, ?, ?)";
            statement = connection.prepareStatement(query);
            statement.setString(1, name);
            statement.setString(2, email);
            statement.setString(3, subject);
            statement.setString(4, message);
            statement.setTimestamp(5, new Timestamp(System.currentTimeMillis())); // Set current timestamp
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            // Close resources
            DatabaseManager.closeStatement(statement);
            DatabaseManager.closeConnection(connection);
        }
    }
}
