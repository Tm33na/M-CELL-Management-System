import java.io.*;
import java.util.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;

public class AdminShowAssignedTaskServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Connection con = DatabaseManager.getConnection();

            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT task_assignment.id, task_assignment.complaint_token, task_assignment.engineer_id, task_assignment.assigned_date, task_assignment.deadline_date, task_assignment.status, complaints.Status AS complaint_status FROM task_assignment LEFT JOIN complaints ON task_assignment.complaint_token = complaints.token ORDER BY task_assignment.id DESC");

            out.println("<html><head><title>Assigned Tasks</title>");
            out.println("<style>");
            out.println("table { border-collapse: collapse; width: 100%; }");
            out.println("th, td { border: 1px solid #dddddd; text-align: left; padding: 8px; }");
            out.println("th { background-color: #f2f2f2; }");
            out.println("</style>");
            out.println("</head><body style=\"background-color:rgb(181 172 172);\">");
            out.println("<h2>Assigned Tasks</h2>");
            out.println("<table>");
            out.println("<tr><th>ID</th><th>Complaint Token</th><th>Engineer ID</th><th>Assigned Date</th><th>Deadline Date</th><th>Status</th><th>Complaint Status</th></tr>");

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String complaintToken = resultSet.getString("complaint_token");
                String engineerId = resultSet.getString("engineer_id");
                Timestamp assignedDate = resultSet.getTimestamp("assigned_date");
                Timestamp deadlineDate = resultSet.getTimestamp("deadline_date");
                String status = resultSet.getString("status");
                String complaintStatus = resultSet.getString("complaint_status");

                out.println("<tr><td>" + id + "</td><td>" + complaintToken + "</td><td>" + engineerId + "</td><td>" + assignedDate + "</td><td>" + deadlineDate + "</td><td>" + status + "</td><td>" + complaintStatus + "</td></tr>");
            }

            out.println("</table></body></html>");

            DatabaseManager.closeConnection(con);
        } catch (SQLException e) {
            System.out.println("Exception Caught: " + e);
        }
    }
}
