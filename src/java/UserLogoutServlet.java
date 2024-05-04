import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/UserLogoutServlet")
public class UserLogoutServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response); // Call doPost() method to handle the logout action
    }
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false);        
        if (session != null) {
              String username=(String) session.getAttribute("userId");
            session.invalidate();
           UserLoginServlet.loggedInUsers.remove(username); // Remove from the map
// Invalidate the session upon logout
        }
        // Redirect to the login page after logout
        response.sendRedirect("userlogin.html");
    }
}
