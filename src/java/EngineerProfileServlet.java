import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.*;

@WebServlet("/EngineerProfileServlet")
public class EngineerProfileServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("engineerId") != null) {
            // User is logged in
            String EngId = (String) session.getAttribute("engineerId");
            try {
                // Fetch user information from the database
                Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM engineer WHERE engineer_id = ?");
                stmt.setString(1, EngId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    // Retrieve user information
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    String engineerId = rs.getString("engineer_id");
                    // Send user information as JSON response
                    response.setContentType("application/json");
                    PrintWriter out = response.getWriter();
                    out.println("{\"name\": \"" + name + "\", \"email\": \"" + email + "\", \"engineerId\": \"" + engineerId + "\"}");
                }
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else {
            // User is not logged in, or session is invalid
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("engineerId") != null) {
            // User is logged in
            String engineerId = (String) session.getAttribute("engineerId");
            String action = request.getParameter("action");
            if (action != null && !action.isEmpty()) {
                if (action.equals("changeEmail")) {
                    // Handle change email action
                    String newEmail = request.getParameter("new-email");
                    if (newEmail != null && !newEmail.isEmpty()) {
                        // Perform email update in the database
                        try {
                            Connection conn = DatabaseManager.getConnection();
                            PreparedStatement stmt = conn.prepareStatement("UPDATE engineer SET email = ? WHERE engineer_id = ?");
                            stmt.setString(1, newEmail);
                            stmt.setString(2, engineerId);
                            int rowsAffected = stmt.executeUpdate();
                            if (rowsAffected > 0) {
                                // Email updated successfully
                                response.getWriter().println("Email updated successfully.");
                            } else {
                                // Failed to update email
                                response.getWriter().println("Failed to update email.");
                            }
                            conn.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        }
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    }
                } else if (action.equals("changePassword")){
                    // Handle change password action
                    String currentPassword = request.getParameter("current-password");
                    String newPassword = request.getParameter("new-password");
                    if (currentPassword != null && newPassword != null && !currentPassword.isEmpty() && !newPassword.isEmpty()) {
                        // Perform password update in the database
                        try {
                            Connection conn = DatabaseManager.getConnection();
                            PreparedStatement stmt = conn.prepareStatement("UPDATE engineer SET engineerid_password = ? WHERE engineer_id = ? AND engineerid_password = ?");
                            stmt.setString(1, newPassword);
                            stmt.setString(2, engineerId);
                            stmt.setString(3, currentPassword);
                            int rowsAffected = stmt.executeUpdate();
                            if (rowsAffected > 0) {
                                // Password updated successfully
                                response.getWriter().println("Password updated successfully.");
                            } else {
                                // Failed to update password
                                response.getWriter().println("Failed to update password. Make sure your current password is correct.");
                            }
                            conn.close();
                        } catch (SQLException e) {
                            e.printStackTrace();
                            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        }
                    } else {
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    }
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        } else {
            // User is not logged in, or session is invalid
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
