import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;

public class AdminMessageServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);        
        if (session != null) {
            String adminId = (String) session.getAttribute("adminId");

            try {
                Connection con = DatabaseManager.getConnection();

                // Update status to 'Read' for notifications of the logged-in user
                PreparedStatement updateStatement = con.prepareStatement("UPDATE adminnotifications SET status = 'Read' ");
                updateStatement.executeUpdate();

                // Retrieve messages for the logged-in user ordered by latest date
                PreparedStatement statement = con.prepareStatement("SELECT * FROM adminnotifications ORDER BY date_received DESC");
                ResultSet resultSet = statement.executeQuery();

                
                out.println("<html><head><title>Admin Messages</title>");
            out.println("<style>body{ background-color:rgb(181 172 172);} ,table { border-collapse: collapse; width: 100%; } th, td { border: 1px solid black; padding: 8px; text-align: left; } th { background-color: #f2f2f2; } </style>");
            out.println("</head><body>");
                
                out.println("<h2>User Messages</h2>");
                out.println("<table border='1'><tr><th>ID</th><th>Message</th><th>Date</th><th>Status</th></tr>");

                while (resultSet.next()) {
                    int id = resultSet.getInt("id");
                    String message = resultSet.getString("message");
                    Timestamp notificationDate = resultSet.getTimestamp("date_received");
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
            response.sendRedirect("index.html");
        }
    }
}
