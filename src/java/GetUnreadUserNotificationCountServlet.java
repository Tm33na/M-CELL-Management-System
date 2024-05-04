import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/GetUnreadUserNotificationCountServlet")
public class GetUnreadUserNotificationCountServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Get the logged-in user's ID from session
        String userId = (String) request.getSession().getAttribute("userId");
        if (userId == null) {
            // If userId is null, return error response
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            out.println("{\"error\": \"User not logged in.\"}");
            return;
        }

        try {
            // Establish database connection
            Connection con = DatabaseManager.getConnection();

            // Query to count unread notifications for the user
            String query = "SELECT COUNT(*) AS unread_count FROM Usernotification WHERE receiver_id = ? AND status = 'Unread'";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();

            int unreadCount = 0;
            if (resultSet.next()) {
                unreadCount = resultSet.getInt("unread_count");
            }

            // Write the JSON response with the unread count
            out.println("{\"count\": " + unreadCount + "}");

            // Close database connection
            DatabaseManager.closeConnection(con);
        } catch (SQLException e) {
            // Handle database error
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
            out.println("{\"error\": \"Failed to retrieve unread count.\"}");
            e.printStackTrace(); // For debugging purposes
        }
    }
}
