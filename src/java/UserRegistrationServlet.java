import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;

public class UserRegistrationServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        
        try {
            // Get a connection from the database manager
            Connection con = DatabaseManager.getConnection();
            
            // Check if Institute ID already exists
            PreparedStatement checkStatement = con.prepareStatement("SELECT * FROM member WHERE institute_id = ?");
            checkStatement.setString(1, username);
            ResultSet rs = checkStatement.executeQuery();
            
            if (rs.next()) {
                // Username already exists
                out.println("<script>alert('Username already registered. Please login.');</script>");
                RequestDispatcher rd = request.getRequestDispatcher("userlogin.html");
                rd.include(request, response);
            } else {
                // Username doesn't exist, proceed with registration
                PreparedStatement insertStatement = con.prepareStatement("INSERT INTO member (institute_id, instituteid_password, name, email) VALUES (?, ?, ?, ?)");
                insertStatement.setString(1, username);
                insertStatement.setString(2, password);
                insertStatement.setString(3, name);
                insertStatement.setString(4, email);
                insertStatement.executeUpdate();
                
                out.println("<script>alert('Registration successful. Please login.');</script>");
                RequestDispatcher rd = request.getRequestDispatcher("user_registration.html");
                rd.include(request, response);
            }
            
            con.close();
        } catch (SQLException e) {
            System.out.println("Exception Caught: " + e);
        }
    }
}
