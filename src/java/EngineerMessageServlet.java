import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;

public class EngineerMessageServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);        
        if (session != null) {
            String engineerId = (String) session.getAttribute("engineerId");

            try {
                Connection con = DatabaseManager.getConnection();

                // Update status to 'read' for notifications of the logged-in engineer
                PreparedStatement updateStatement = con.prepareStatement("UPDATE tasknotification SET status = 'Read' WHERE receiver_id = ?");
                updateStatement.setString(1, engineerId);
                updateStatement.executeUpdate();

                // Retrieve messages for the logged-in engineer ordered by latest date
                PreparedStatement statement = con.prepareStatement("SELECT * FROM tasknotification WHERE receiver_id = ? ORDER BY notification_date DESC");
                statement.setString(1, engineerId);
                ResultSet resultSet = statement.executeQuery();

                out.println("<html><head><title>Engineer Messages</title></head><body style=\" background-color: #c2bebe;\">");
                out.println("<h2>Engineer Messages</h2>");
                out.println("<table border='1'><tr><th>ID</th><th>Message</th><th>Date</th><th>Status</th></tr>");

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String message = resultSet.getString("message");
                    Timestamp notificationDate = resultSet.getTimestamp("notification_date");
                    String status = resultSet.getString("status");

                    out.println("<tr><td>" + id + "</td><td>" + message + "</td><td>" + notificationDate + "</td><td>" + status + "</td></tr>");
                }

                out.println("</table></body></html>");

                DatabaseManager.closeConnection(con);
            } catch (SQLException e) {
                System.out.println("Exception Caught: " + e);
            }
        } else {
            // Redirect to login page if session is not found
            response.sendRedirect("login.html");
        }
    }
}
