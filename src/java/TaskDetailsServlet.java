import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;

public class TaskDetailsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(false);        
        if (session != null) {
            String engineerId = (String) session.getAttribute("engineerId");

            try {
                Connection con = DatabaseManager.getConnection();

                // Query to retrieve tasks assigned to the engineer with pending complaint status
                String query = "SELECT task_assignment.id AS task_id, task_assignment.complaint_token, task_assignment.engineer_id, task_assignment.assigned_date, task_assignment.deadline_date, task_assignment.status, complaints.*, complaints.status AS complaint_status FROM task_assignment INNER JOIN complaints ON task_assignment.complaint_token = complaints.token WHERE task_assignment.engineer_id = ? AND complaints.status = 'Pending' AND task_assignment.status<>'Reject' ORDER BY task_assignment.id DESC";
                PreparedStatement statement = con.prepareStatement(query);
                statement.setString(1, engineerId);

                ResultSet resultSet = statement.executeQuery();

                // Display task details
                out.println("<html><head ><title>Task Details</title></head><body style=\" background-color: #c2bebe;\">");
                out.println("<h2>Task Details</h2>");

                while (resultSet.next()) {
                    String taskId = resultSet.getString("task_id");
                    String complaintToken = resultSet.getString("complaint_token");
                    Timestamp assignedDate = resultSet.getTimestamp("assigned_date");
                    Timestamp deadlineDate = resultSet.getTimestamp("deadline_date");
                    String taskStatus = resultSet.getString("status");
                    String complaintStatus = resultSet.getString("complaint_status");

                    // Retrieve complaint details
                    String complaintName = resultSet.getString("name");
                    String email = resultSet.getString("email");
                    String instituteId = resultSet.getString("institute_id");
                    String complaintType = resultSet.getString("Complaint_Type");
                    String priority = resultSet.getString("priority");
                    String location = resultSet.getString("location");
                    String roomNo = resultSet.getString("room_no");
                    String subject = resultSet.getString("subject");
                    String message = resultSet.getString("message");
                    // Assuming attachment and attachment_format are not displayed here

                    out.println("<h3>Task ID: " + taskId + "</h3>");
                    out.println("<h4>Complaint Details:</h4>");
                    out.println("<p><strong>Name:</strong> " + complaintName + "</p>");
                    out.println("<p><strong>Email:</strong> " + email + "</p>");
                    out.println("<p><strong>Institute ID:</strong> " + instituteId + "</p>");
                    out.println("<p><strong>Complaint Type:</strong> " + complaintType + "</p>");
                    out.println("<p><strong>Priority:</strong> " + priority + "</p>");
                    out.println("<p><strong>Location:</strong> " + location + "</p>");
                    out.println("<p><strong>Room No:</strong> " + roomNo + "</p>");
                    out.println("<p><strong>Subject:</strong> " + subject + "</p>");
                    out.println("<p><strong>Message:</strong> " + message + "</p>");
                    out.println("<p><strong>Complaint Status:</strong> " + complaintStatus + "</p>");

                    out.println("<h4>Task Details:</h4>");
                    out.println("<p><strong>Engineer ID:</strong> " + engineerId + "</p>");
                    out.println("<p><strong>Assigned Date:</strong> " + assignedDate + "</p>");
                    out.println("<p><strong>Deadline Date:</strong> " + deadlineDate + "</p>");
                    out.println("<p><strong>Status:</strong> " + taskStatus + "</p>");

                    // Add accept and reject buttons for task status
                    out.println("<form action='UpdateTaskStatusServlet' method='post'>");
                    out.println("<input type='hidden' name='task_id' value='" + taskId + "'>");
                    out.println("<input type='submit' name='action' value='Accept'>");
                    out.println("<input type='submit' name='action' value='Reject'>");
                    out.println("</form>");

                    // Add button to update complaint status to "resolved"
                    if (complaintStatus.equals("Pending")) {
                        out.println("<form action='UpdateComplaintStatusServlet' method='post'>");
                        out.println("<input type='hidden' name='complaint_token' value='" + complaintToken + "'>");
                        out.println("<input type='submit' name='action' value='Resolve Complaint'>");
                        out.println("</form>");
                    }

                    out.println("<hr>");
                }

                out.println("</body></html>");

                DatabaseManager.closeConnection(con);
            } catch (SQLException e) {
                System.out.println("Exception Caught: " + e);
            }
        } else {
            // Session not found, redirect to login page
            response.sendRedirect("login.html");
        }
    }
}
