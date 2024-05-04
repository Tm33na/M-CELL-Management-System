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

@WebServlet("/UserComplaintsDataServlet")
public class UserComplaintsDataServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // Retrieve userId from session attribute
        String userId = (String) request.getSession().getAttribute("userId");

        // Log the userId received by the servlet
        System.out.println("User ID received: " + userId);

        // Initialize variables to store total and resolved complaints count
        int totalComplaints = 0;
        int resolvedComplaints = 0;

        // Fetch data from the database
        try (Connection con = DatabaseManager.getConnection()) {
            // Query to get total complaints assigned to the user
            String totalComplaintsQuery = "SELECT COUNT(*) FROM complaints WHERE institute_id = ?";
            PreparedStatement totalComplaintsStatement = con.prepareStatement(totalComplaintsQuery);
            totalComplaintsStatement.setString(1, userId);
            ResultSet totalComplaintsResult = totalComplaintsStatement.executeQuery();
            if (totalComplaintsResult.next()) {
                totalComplaints = totalComplaintsResult.getInt(1);
            }

            // Query to get resolved complaints count assigned to the user
            String resolvedComplaintsQuery = "SELECT COUNT(*) FROM complaints WHERE institute_id = ? AND Status = 'resolved'";
            PreparedStatement resolvedComplaintsStatement = con.prepareStatement(resolvedComplaintsQuery);
            resolvedComplaintsStatement.setString(1, userId);
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
