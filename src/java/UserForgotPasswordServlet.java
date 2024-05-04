import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;

public class UserForgotPasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        String email = request.getParameter("email");
        
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        
        try {
            con = DatabaseManager.getConnection();
            statement = con.prepareStatement("SELECT * FROM member WHERE email = ?");
            statement.setString(1, email);
            rs = statement.executeQuery();
            
            if (rs.next()) {
                String username = rs.getString("institute_id");
                String password = rs.getString("instituteid_password");
                
                // Send username and password to the provided email (implement email sending here)
                // Example: sendEmail(username, password, email);
                
                out.println("<script>alert('Username and password sent to your email.');</script>");
                RequestDispatcher rd = request.getRequestDispatcher("userlogin.html");
                rd.include(request, response);
            } else {
                // Email not registered
                out.println("<script>alert('Email not registered.');</script>");
                RequestDispatcher rd = request.getRequestDispatcher("UserForgotPassword.html");
                rd.include(request, response);
            }
        } catch (SQLException e) {
            System.out.println("Exception Caught: " + e);
        } finally {
            try {
                if (rs != null) rs.close();
                if (statement != null) statement.close();
                if (con != null) con.close();
            } catch (SQLException e) {
                System.out.println("Exception while closing resources: " + e);
            }
        }
    }
}
