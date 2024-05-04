import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@WebServlet("/EngineerComplaintsDataServlet")
public class EngineerComplaintsDataServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Retrieve engineer ID from session attribute
        String engineerId = (String) request.getSession().getAttribute("engineerId");
        
        // Log the engineer ID received by the servlet
        System.out.println("Engineer ID received: " + engineerId);

        // Initialize variables to store total and resolved complaints count
        int totalComplaints = 0;
        int resolvedComplaints = 0;

        // Fetch data from the database
        try (Connection con = DatabaseManager.getConnection()) {
            // Query to get total complaints assigned to the engineer where status is 'accept'
            String totalComplaintsQuery = "SELECT COUNT(*) FROM task_assignment WHERE engineer_id = ? AND status = 'Accept'";
            PreparedStatement totalComplaintsStatement = con.prepareStatement(totalComplaintsQuery);
            totalComplaintsStatement.setString(1, engineerId);
            ResultSet totalComplaintsResult = totalComplaintsStatement.executeQuery();
            if (totalComplaintsResult.next()) {
                totalComplaints = totalComplaintsResult.getInt(1);
            }

            // Query to get resolved complaints count assigned to the engineer
            String resolvedComplaintsQuery = "SELECT COUNT(*) FROM task_assignment ta " +
                    "INNER JOIN complaints c ON ta.complaint_token = c.token " +
                    "WHERE ta.engineer_id = ? AND c.Status = 'resolved'";
            PreparedStatement resolvedComplaintsStatement = con.prepareStatement(resolvedComplaintsQuery);
            resolvedComplaintsStatement.setString(1, engineerId);
            ResultSet resolvedComplaintsResult = resolvedComplaintsStatement.executeQuery();
            if (resolvedComplaintsResult.next()) {
                resolvedComplaints = resolvedComplaintsResult.getInt(1);
            }

            // Send the data as JSON response
            out.println("{\"totalComplaints\": " + totalComplaints + ", \"resolvedComplaints\": " + resolvedComplaints + "}");
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database error
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
