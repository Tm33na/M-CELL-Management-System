import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.*;

@WebServlet("/AdminProfileServlet")
public class AdminProfileServlet extends HttpServlet {

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("adminId") != null) {
            // User is logged in
            String adminId = (String) session.getAttribute("adminId");
            try {
                // Fetch user information from the database
                Connection conn = DatabaseManager.getConnection();
                PreparedStatement stmt = conn.prepareStatement("SELECT * FROM admin WHERE admin_id = ?");
                stmt.setString(1, adminId);
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    // Retrieve user information
                    String name = rs.getString("name");
                    String email = rs.getString("email");
                    String AD_Id = rs.getString("admin_id");
                    // Send user information as JSON response
                    response.setContentType("application/json");
                    PrintWriter out = response.getWriter();
                    out.println("{\"name\": \"" + name + "\", \"email\": \"" + email + "\", \"AD_Id\": \"" + AD_Id + "\"}");
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
        if (session != null && session.getAttribute("adminId") != null) {
            // User is logged in
            String adminId = (String) session.getAttribute("adminId");
            String action = request.getParameter("action");
            if (action != null && !action.isEmpty()) {
                if (action.equals("changeEmail")) {
                    // Handle change email action
                    String newEmail = request.getParameter("new-email");
                    if (newEmail != null && !newEmail.isEmpty()) {
                        // Perform email update in the database
                        try {
                            Connection conn = DatabaseManager.getConnection();
                            PreparedStatement stmt = conn.prepareStatement("UPDATE admin SET email = ? WHERE admin_id = ?");
                            stmt.setString(1, newEmail);
                            stmt.setString(2, adminId);
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
                            PreparedStatement stmt = conn.prepareStatement("UPDATE admin SET admin_password = ? WHERE admin_id = ? AND admin_password = ?");
                            stmt.setString(1, newPassword);
                            stmt.setString(2, adminId);
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
