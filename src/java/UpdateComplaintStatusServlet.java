import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;

public class UpdateComplaintStatusServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        // Get the complaint token from the form
        String complaintToken = request.getParameter("complaint_token");

        try {
            // Update the complaint status to "resolved" in the database
            Connection con = DatabaseManager.getConnection();
            String updateQuery = "UPDATE complaints SET status = 'resolved' WHERE token = ?";
            PreparedStatement updateStatement = con.prepareStatement(updateQuery);
            updateStatement.setString(1, complaintToken);
            int rowsAffected = updateStatement.executeUpdate();

            if (rowsAffected > 0) {
                // Get the receiver ID (institute_id) from the complaints table using the complaint token
                String receiverId = getReceiverId(con, complaintToken);

                // Insert a notification into Usernotification table
                String notificationMessage = "Complaint with token " + complaintToken + " is resolved.";
                String insertQuery = "INSERT INTO Usernotification (receiver_id, message) VALUES (?, ?)";
                PreparedStatement insertStatement = con.prepareStatement(insertQuery);
                insertStatement.setString(1, receiverId);
                insertStatement.setString(2, notificationMessage);
                insertStatement.executeUpdate();

                out.println("<html><body><h3>Complaint status updated to resolved successfully.</h3></body></html>");
            } else {
                out.println("<html><body><h3>Error: Complaint status could not be updated.</h3></body></html>");
            }

            DatabaseManager.closeConnection(con);
        } catch (SQLException e) {
            System.out.println("Exception Caught: " + e);
        }
    }

    // Method to get receiver ID (institute_id) from complaints table based on complaint token
    private String getReceiverId(Connection con, String complaintToken) throws SQLException {
        String receiverId = null;
        String query = "SELECT institute_id FROM complaints WHERE token = ?";
        PreparedStatement statement = con.prepareStatement(query);
        statement.setString(1, complaintToken);
        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            receiverId = resultSet.getString("institute_id");
        }
        return receiverId;
    }
}
