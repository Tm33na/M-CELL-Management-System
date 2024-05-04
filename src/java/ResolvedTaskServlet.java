import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.sql.*;

@WebServlet("/ResolvedTasksServlet")
public class ResolvedTaskServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Get the logged-in engineer's ID from session
        String engineerId = (String) request.getSession().getAttribute("engineerId");
        if (engineerId == null) {
            // If engineerId is null, redirect to login page
            response.sendRedirect("engineerlogin.html");
            return;
        }

        try {
            Connection con = DatabaseManager.getConnection();

            // Query to retrieve resolved tasks for the engineer
            String query = "SELECT task_assignment.id AS task_id, task_assignment.complaint_token, task_assignment.engineer_id, task_assignment.assigned_date, task_assignment.deadline_date, task_assignment.status, complaints.Status AS complaint_status FROM task_assignment LEFT JOIN complaints ON task_assignment.complaint_token = complaints.token WHERE task_assignment.engineer_id = ? AND complaints.status = 'resolved' ORDER BY task_assignment.id DESC";
            PreparedStatement statement = con.prepareStatement(query);
            statement.setString(1, engineerId);
            ResultSet resultSet = statement.executeQuery();

            // Display task details
            out.println("<html><head><title>Resolved Tasks</title></head><body style=\" background-color: #c2bebe;\">");
            out.println("<h2>Resolved Tasks</h2>");
            out.println("<table border='1'><tr><th>ID</th><th>Complaint Token</th><th>Assigned Date</th><th>Deadline Date</th><th>Complaint Status</th></tr>");

            while (resultSet.next()) {
                int taskId = resultSet.getInt("task_id");
                String complaintToken = resultSet.getString("complaint_token");
                Timestamp assignedDate = resultSet.getTimestamp("assigned_date");
                Timestamp deadlineDate = resultSet.getTimestamp("deadline_date");
                String complaintStatus = resultSet.getString("complaint_status");

                out.println("<tr><td>" + taskId + "</td><td>" + complaintToken + "</td><td>" + assignedDate + "</td><td>" + deadlineDate + "</td><td>" + complaintStatus + "</td></tr>");
            }

            out.println("</table></body></html>");

            DatabaseManager.closeConnection(con);
        } catch (SQLException e) {
            System.out.println("Exception Caught: " + e);
            // Handle database error
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 Internal Server Error
            out.println("<html><body><h3>Failed to retrieve resolved tasks.</h3></body></html>");
        }
    }
}
