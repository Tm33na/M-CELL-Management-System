import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;

public class AssignTaskServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String engineer_id = request.getParameter("engineer_id");
        String complaint_token = request.getParameter("complaint_token");
        String deadline_date = request.getParameter("deadline_date");

        try {
            Connection con = DatabaseManager.getConnection();

            // Insert task assignment into task_assignment table
            PreparedStatement insertStatement = con.prepareStatement("INSERT INTO task_assignment (complaint_token, engineer_id, deadline_date) VALUES (?, ?, ?)");
            insertStatement.setString(1, complaint_token);
            insertStatement.setString(2, engineer_id);
            insertStatement.setString(3, deadline_date);
            insertStatement.executeUpdate();

            // Insert notification into tasknotification table
            String message = "You have been assigned a new task with a deadline: " + deadline_date;
            PreparedStatement notificationStatement = con.prepareStatement("INSERT INTO tasknotification (receiver_id, message) VALUES (?, ?)");
            notificationStatement.setString(1, engineer_id);
            notificationStatement.setString(2, message);
            notificationStatement.executeUpdate();

            out.println("<script>alert('Task assigned successfully.');</script>");
            RequestDispatcher rd = request.getRequestDispatcher("assign_task.html");
            rd.include(request, response);

            DatabaseManager.closeConnection(con);
        } catch (SQLException e) {
            System.out.println("Exception Caught: " + e);
        }
    }
}
