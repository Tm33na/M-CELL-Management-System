import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.*;

@WebServlet("/GetUnreadNotificationCountServlet")
public class GetUnreadNotificationCountServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Get the logged-in engineer's ID from session
        String engineerId = (String) request.getSession().getAttribute("engineerId");
        if (engineerId == null) {
            // If engineerId is null, return error response
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401 Unauthorized
            out.println("{\"error\": \"Engineer not logged in.\"}");
            return;
        }

        try {
            Connection con = DatabaseManager.getConnection();

            // Query to count unread notifications for the engineer
            String query = "SELECT COUNT(*) AS unread_count FROM tasknotification WHERE receiver_id = ? AND status = 'Unread'";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, engineerId);
            ResultSet resultSet = statement.executeQuery();

            int unreadCount = 0;
            if (resultSet.next()) {
                unreadCount = resultSet.getInt("unread_count");
            }

            // Write the JSON response with the unread count
            out.println("{\"count\": " + unreadCount + "}");

            DatabaseManager.closeConnection(con);
        } catch (SQLException e) {
            // Handle database error
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
            out.println("{\"error\": \"Failed to retrieve unread count.\"}");
            e.printStackTrace(); // For debugging purposes
        }
    }
}
