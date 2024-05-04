import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;

public class UpdateTaskStatusServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Retrieve task ID and action from the form
        String taskId = request.getParameter("task_id");
        String action = request.getParameter("action");

        try {
            Connection con = DatabaseManager.getConnection();

            // Update task status based on the action
            String updateQuery = "UPDATE task_assignment SET status = ? WHERE id = ?";
            PreparedStatement updateStatement = con.prepareStatement(updateQuery);
            updateStatement.setString(1, action);
            updateStatement.setString(2, taskId);
            
            
            
                
                
                
            int rowsAffected = updateStatement.executeUpdate();

            

            if (rowsAffected > 0) {
                out.println("<html><body><h3>Task status updated successfully!</h3></body></html>");

                // Insert notification into adminnotification table
                String adminMessage = "Engineer "  + action +"ed the task Id "  + taskId;
                PreparedStatement adminNotificationStatement = con.prepareStatement("INSERT INTO adminnotifications (message) VALUES (?)");
                adminNotificationStatement.setString(1, adminMessage);
                
                 String deleteQuery = "DELETE FROM task_assignment WHERE id = ? and status='Reject'" ;
                PreparedStatement deleteStatement = con.prepareStatement(deleteQuery);
                deleteStatement.setString(1, taskId);
                
                
             
                adminNotificationStatement.executeUpdate();
                deleteStatement.executeUpdate();
                
                
                
               
                
            } else {
                out.println("<html><body><h3>Failed to update task status!</h3></body></html>");
            }
            
            
            
            

            DatabaseManager.closeConnection(con);
        } catch (SQLException e) {
            System.out.println("Exception Caught: " + e);
        }
    }
}
