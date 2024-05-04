import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/RetrieveLinkServlet")
public class RetrieveLinkServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set content type
        response.setContentType("text/html");

        // Get PrintWriter object
        PrintWriter out = response.getWriter();

        // Write welcome message
        out.println("<h3>Announcements</h3>");
        out.println("<ul>");

        // Fetch links from database and format into HTML
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            con = DatabaseManager.getConnection(); // Get connection using DatabaseManager
            ps = con.prepareStatement("SELECT id, title, url FROM Notification ORDER BY id DESC LIMIT 45");
            rs = ps.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String title = rs.getString("title");
                String url = rs.getString("url");
                out.println("<li><a  href='" + url + "' class='notifications' target='_blank' style='text-decoration:none;color:#caba2c;'>" + title + "</a></li>");
            }
        } catch (Exception e) {
            out.println("Error: " + e.getMessage());
        } finally {
            DatabaseManager.closeResultSet(rs); // Close ResultSet using DatabaseManager
            DatabaseManager.closeStatement(ps); // Close PreparedStatement using DatabaseManager
            DatabaseManager.closeConnection(con); // Close Connection using DatabaseManager
        }

        // Write HTML footer
        out.println("</ul>");
    }
}
