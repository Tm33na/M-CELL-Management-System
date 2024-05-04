import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class AdminLoginServlet extends HttpServlet {

    public static final Map<String, HttpSession> loggedInAdmins = new HashMap<>();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        try {
            Connection con = DatabaseManager.getConnection();
            
            PreparedStatement statement = con.prepareStatement("SELECT * FROM admin WHERE admin_id = ?");
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("admin_password");
                if (password.equals(storedPassword)) {
                    // Check if the user is already logged in
                    if (loggedInAdmins.containsKey(username)) 
                    
                    {
                        HttpSession previousSession = loggedInAdmins.get(username);
                        try{
                        previousSession.invalidate(); // Invalidate previous session
                     loggedInAdmins.remove(username); // Remove from the map
}
                        catch(IllegalStateException e){
                        
                        loggedInAdmins.remove(username); // Remove from the map
                    }
                    }
                    
                    // Create a new session for the user
                    HttpSession session = request.getSession();
                    session.setAttribute("adminId", username);
                    session.setMaxInactiveInterval(30 * 60); // Session expires after 60 minutes
                    
                    // Add the user to the map of logged-in users
                    loggedInAdmins.put(username, session);
                    
                    // Redirect to dashboard or home page
                    response.sendRedirect("adminHome.html");
                } else {
                    // Wrong password
                    out.println("<script>alert('Wrong password.');</script>");
                    RequestDispatcher rd = request.getRequestDispatcher("adminlogin.html");
                    rd.include(request, response);
                }
            } else {
                // Admin not registered
                out.println("<script>alert('not a registered Admin.');</script>");
                RequestDispatcher rd = request.getRequestDispatcher("adminlogin.html");
                rd.include(request, response);
            }
            
            DatabaseManager.closeConnection(con);
        } catch (SQLException e) {
            System.out.println("Exception Caught: " + e);
        }
    }
}
