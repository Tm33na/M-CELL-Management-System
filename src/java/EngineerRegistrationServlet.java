import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;

public class EngineerRegistrationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        
        Connection con = null;
        PreparedStatement checkStatement = null;
        
        try {
            con = DatabaseManager.getConnection();
            
            // Check if Institute ID already exists
            checkStatement = con.prepareStatement("SELECT * FROM engineer WHERE engineer_id = ?");
            checkStatement.setString(1, username);
            ResultSet rs = checkStatement.executeQuery();
            
            if (rs.next()) {
                // Username already exists
                out.println("<script>alert('Username already registered. Please login.');</script>");
                RequestDispatcher rd = request.getRequestDispatcher("engineerlogin.html");
                rd.include(request, response);
            } else {
                // Username doesn't exist, proceed with registration
                PreparedStatement insertStatement = con.prepareStatement("INSERT INTO engineer (engineer_id, engineerid_password, name, email) VALUES (?, ?, ?, ?)");
                insertStatement.setString(1, username);
                insertStatement.setString(2, password);
                insertStatement.setString(3, name);
                insertStatement.setString(4, email);
                insertStatement.executeUpdate();
                
                out.println("<script>alert('Registration successful. Please login.');</script>");
                RequestDispatcher rd = request.getRequestDispatcher("engineer_login.html");
                rd.include(request, response);
            }
        } catch (SQLException e) {
            System.out.println("Exception Caught: " + e);
        } finally {
            DatabaseManager.closeStatement(checkStatement);
            DatabaseManager.closeConnection(con);
        }
    }
}
