import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;

public class AdminResolvedComplaintsServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        try {
            Connection con = DatabaseManager.getConnection();

            // Query to retrieve all resolved complaints with their details and task assignment
            String query = "SELECT complaints.*, task_assignment.*, task_assignment.engineer_id AS engineer_id FROM complaints INNER JOIN task_assignment ON complaints.token = task_assignment.complaint_token WHERE complaints.status = 'resolved'";
            Statement statement = con.createStatement();
            ResultSet resultSet = statement.executeQuery(query);

            // Display complaint and task assignment details
           out.println("<html><head><title>Resolved Complaints</title>");
            out.println("<style>body{ background-color:rgb(181 172 172);} ,table { border-collapse: collapse; width: 100%; } th, td { border: 1px solid black; padding: 8px; text-align: left; } th { background-color: #f2f2f2; } </style>");
            out.println("</head><body>");
            out.println("<h2>Resolved Complaints</h2>");
            out.println("<table ><tr><th>Complaint Token</th><th>Name</th><th>Email</th><th>Institute ID</th><th>Complaint Type</th><th>Priority</th><th>Location</th><th>Room No</th><th>Subject</th><th>Message</th><th>Task Assigned Date</th><th>Task Deadline Date</th><th>Task Status</th><th>Engineer ID</th></tr>");

            while (resultSet.next()) {
                String token = resultSet.getString("token");
                String name = resultSet.getString("name");
                String email = resultSet.getString("email");
                String instituteId = resultSet.getString("institute_id");
                String complaintType = resultSet.getString("Complaint_Type");
                String priority = resultSet.getString("priority");
                String location = resultSet.getString("location");
                String roomNo = resultSet.getString("room_no");
                String subject = resultSet.getString("subject");
                String message = resultSet.getString("message");
                String assignedDate = resultSet.getString("assigned_date");
                String deadlineDate = resultSet.getString("deadline_date");
                String taskStatus = resultSet.getString("status");
                String engineerId = resultSet.getString("engineer_id");

                out.println("<tr><td>" + token + "</td><td>" + name + "</td><td>" + email + "</td><td>" + instituteId + "</td><td>" + complaintType + "</td><td>" + priority + "</td><td>" + location + "</td><td>" + roomNo + "</td><td>" + subject + "</td><td>" + message + "</td><td>" + assignedDate + "</td><td>" + deadlineDate + "</td><td>" + taskStatus + "</td><td>" + engineerId + "</td></tr>");
            }

            out.println("</table></body></html>");

            DatabaseManager.closeConnection(con);
        } catch (SQLException e) {
            System.out.println("Exception Caught: " + e);
        }
    }
}
