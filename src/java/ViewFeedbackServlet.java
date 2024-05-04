import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.*;

public class ViewFeedbackServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        out.println("<title>Feedback List</title>");
        out.println("<link rel='stylesheet' href='styles.css'>");
        out.println("<style>body{ padding: 8px;  } </style>");

        out.println("</head>");
        out.println("<body style=\" background-color: #c2bebe;\" class= \"bgnone\">");
        out.println("<h2>Feedback List</h2>");
        out.println("<table >");
        out.println("<thead>");
        out.println("<tr>");
        out.println("<th>ID</th>");
        out.println("<th>Name</th>");
        out.println("<th>Email</th>");
        out.println("<th>Subject</th>");
        out.println("<th>Message</th>");
        out.println("<th>Created At</th>");
        out.println("</tr>");
        out.println("</thead>");
        out.println("<tbody>");
        
        try {
            Connection con = DatabaseManager.getConnection();
            
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM feedback ORDER BY id DESC");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getInt("id") + "</td>");
                out.println("<td>" + rs.getString("name") + "</td>");
                out.println("<td>" + rs.getString("email") + "</td>");
                out.println("<td>" + rs.getString("subject") + "</td>");
                out.println("<td>" + rs.getString("message") + "</td>");
                out.println("<td>" + rs.getTimestamp("created_at") + "</td>");
                out.println("</tr>");
            }
            
            DatabaseManager.closeConnection(con);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        out.println("</tbody>");
        out.println("</table>");
        out.println("</body>");
        out.println("</html>");
    }
}
