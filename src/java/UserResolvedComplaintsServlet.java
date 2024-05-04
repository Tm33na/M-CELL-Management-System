import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.sql.*;

@WebServlet("/UserResolvedComplaintsServlet")
public class UserResolvedComplaintsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Get the logged-in user's ID from session
        String userId = (String) request.getSession().getAttribute("userId");
        if (userId == null) {
            // If userId is null, redirect to login page
            response.sendRedirect("login.html");
            return;
        }

        try {
            Connection con = DatabaseManager.getConnection();

            // Query to retrieve resolved complaints for the user
            String query = "SELECT t.complaint_token, c.subject, e.name AS engineer_name, e.engineer_id, t.assigned_date, c.status AS complaint_status FROM task_assignment t INNER JOIN complaints c ON t.complaint_token = c.token INNER JOIN engineer e ON t.engineer_id = e.engineer_id WHERE c.status = 'resolved' AND c.institute_id = ?";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, userId);
            ResultSet resultSet = statement.executeQuery();

            // Display complaint details
            out.println("<html><head><title>Resolved Complaints</title>");
            out.println("<style>body{ background-color:rgb(181 172 172);} ,table { border-collapse: collapse; width: 100%; } th, td { border: 1px solid black; padding: 8px; text-align: left; } th { background-color: #f2f2f2; } </style>");
            out.println("</head><body>");
            out.println("<h2>Resolved Complaints</h2>");
            out.println("<table>");
            out.println("<tr><th>Complaint Token</th><th>Subject</th><th>Engineer Name</th><th>Engineer ID</th><th>Assigned Date</th><th>Status</th></tr>");

            while (resultSet.next()) {
                String complaintToken = resultSet.getString("complaint_token");
                String subject = resultSet.getString("subject");
                String engineerName = resultSet.getString("engineer_name");
                String engineerId = resultSet.getString("engineer_id");
                Timestamp assignedDate = resultSet.getTimestamp("assigned_date");
                String complaintStatus = resultSet.getString("complaint_status");

                out.println("<tr><td>" + complaintToken + "</td><td>" + subject + "</td><td>" + engineerName + "</td><td>" + engineerId + "</td><td>" + assignedDate + "</td><td>" + complaintStatus + "</td></tr>");
            }

            out.println("</table></body></html>");

            DatabaseManager.closeConnection(con);
        } catch (SQLException e) {
            System.out.println("Exception Caught: " + e);
            // Handle database error
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
            out.println("<html><body><h3>Failed to retrieve resolved complaints.</h3></body></html>");
        }
    }
}
