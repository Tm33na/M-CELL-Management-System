import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class EngineerLoginServlet extends HttpServlet {
    public static final Map<String, HttpSession> loggedInEngineers = new HashMap<>();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        Connection con = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        
        try {
            con = DatabaseManager.getConnection();
            statement = con.prepareStatement("SELECT * FROM engineer WHERE engineer_id = ?");
            statement.setString(1, username);
            rs = statement.executeQuery();
            
            if (rs.next()) {
                String storedPassword = rs.getString("engineerid_password");
                if (password.equals(storedPassword)) {
                    
                    // Check if the user is already logged in
                    if (loggedInEngineers.containsKey(username)) {
                        HttpSession previousSession = loggedInEngineers.get(username);
                        
                        try{
                        previousSession.invalidate(); // Invalidate previous session
                        loggedInEngineers.remove(username); // Remove from the map
}
                        catch(IllegalStateException e){
                        
                        loggedInEngineers.remove(username); // Remove from the map
                    }
                        
                    }
                    
                    // Create a new session for the user
                    HttpSession session = request.getSession();
                    session.setAttribute("engineerId", username);
                    session.setMaxInactiveInterval(30 * 60); // Session expires after 30 minutes
                    
                    // Add the user to the map of logged-in users
                    loggedInEngineers.put(username, session);
                    
                    
                    // Password matched, redirect to dashboard or home page
                    response.sendRedirect("engineerHome.html");
                } else {
                    // Wrong password
                    out.println("<script>alert('Wrong password.');</script>");
                    RequestDispatcher rd = request.getRequestDispatcher("engineerlogin.html");
                    rd.include(request, response);
                }
            } else {
                // User not registered
                out.println("<script>alert('User not registered.');</script>");
                RequestDispatcher rd = request.getRequestDispatcher("engineerlogin.html");
                rd.include(request, response);
            }
        } catch (SQLException e) {
            System.out.println("Exception Caught: " + e);
        } finally {
            DatabaseManager.closeResultSet(rs);
            DatabaseManager.closeStatement(statement);
            DatabaseManager.closeConnection(con);
        }
    }
}
