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


@WebServlet("/AdminComplaintsDataServlet")
public class AdminComplaintsDataServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try (Connection con = DatabaseManager.getConnection()) {
            // Query to get total and resolved complaints count
            String query = "SELECT COUNT(*) AS totalComplaints, SUM(CASE WHEN Status = 'resolved' THEN 1 ELSE 0 END) AS resolvedComplaints FROM complaints";
            PreparedStatement statement = con.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            int totalComplaints = 0;
            int resolvedComplaints = 0;

            // Retrieve data from the result set
            if (resultSet.next()) {
                totalComplaints = resultSet.getInt("totalComplaints");
                resolvedComplaints = resultSet.getInt("resolvedComplaints");
            }

            // Send the data as JSON response
            out.println("{\"totalComplaints\": " + totalComplaints + ", \"resolvedComplaints\": " + resolvedComplaints + "}");
        } catch (SQLException e) {
            // Handle database error
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("{\"error\": \"Failed to fetch complaints data from the database\"}");
            e.printStackTrace();
        }
    }
}
