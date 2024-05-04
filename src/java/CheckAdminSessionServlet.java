import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/CheckAdminSessionServlet")
public class CheckAdminSessionServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(false); // Do not create a new session if it doesn't exist
        if (session != null && session.getAttribute("adminId") != null) {
            // Session and userId attribute exist
            response.setStatus(HttpServletResponse.SC_OK);
        } else {
            // Session or userId attribute does not exist
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
