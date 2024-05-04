import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;

public class EngineerForgotPasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        String email = request.getParameter("email");
        
        try {
            Connection con = DatabaseManager.getConnection();
            
            PreparedStatement statement = con.prepareStatement("SELECT * FROM engineer WHERE email = ?");
            statement.setString(1, email);
            ResultSet rs = statement.executeQuery();
            
            if (rs.next()) {
                String username = rs.getString("engineer_id");
                String password = rs.getString("engineerid_password");
                
                // Send username and password to the provided email (implement email sending here)
                // Example: sendEmail(username, password, email);
                
                
                out.println("<script>alert('Username and password sent to your email.');</script>");
                RequestDispatcher rd = request.getRequestDispatcher("engineerlogin.html");
                rd.include(request, response);
            } else {
                // Email not registered
                out.println("<script>alert('Email not registered.');</script>");
                RequestDispatcher rd = request.getRequestDispatcher("EngineerForgotPassword.html");
                rd.include(request, response);
            }
            
            DatabaseManager.closeResultSet(rs);
            DatabaseManager.closeStatement(statement);
            DatabaseManager.closeConnection(con);
        } catch (SQLException e) {
            System.out.println("Exception Caught: " + e);
        }
    }
}
