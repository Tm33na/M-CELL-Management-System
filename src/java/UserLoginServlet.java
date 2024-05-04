import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class UserLoginServlet extends HttpServlet {

    public static final Map<String, HttpSession> loggedInUsers = new HashMap<>();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        try {
            Connection con = DatabaseManager.getConnection();
            
            PreparedStatement statement = con.prepareStatement("SELECT * FROM member WHERE institute_id = ?");
            statement.setString(1, username);
            ResultSet rs = statement.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("instituteid_password");
                if (password.equals(storedPassword)) {
                    // Check if the user is already logged in
                    if (loggedInUsers.containsKey(username)) {
                        HttpSession previousSession = loggedInUsers.get(username);
                        
                        
                           try{
                        previousSession.invalidate(); // Invalidate previous session
                        loggedInUsers.remove(username); // Remove from the map
}
                        catch(IllegalStateException e){
                        
                        loggedInUsers.remove(username); // Remove from the map
                    }
                        
                        
                    }
                    
                    // Create a new session for the user
                    HttpSession session = request.getSession();
                    session.setAttribute("userId", username);
                    session.setMaxInactiveInterval(30 * 60); // Session expires after 30 minutes
                    
                    // Add the user to the map of logged-in users
                    loggedInUsers.put(username, session);
                    
                    // Redirect to dashboard or home page
                    response.sendRedirect("UserHome.html");
                } else {
                    // Wrong password
                    out.println("<script>alert('Wrong password.');</script>");
                    RequestDispatcher rd = request.getRequestDispatcher("userlogin.html");
                    rd.include(request, response);
                }
            } else {
                // User not registered
                out.println("<script>alert('User not registered.');</script>");
                RequestDispatcher rd = request.getRequestDispatcher("userlogin.html");
                rd.include(request, response);
            }
            
            DatabaseManager.closeConnection(con);
        } catch (SQLException e) {
            System.out.println("Exception Caught: " + e);
        }
    }
}
