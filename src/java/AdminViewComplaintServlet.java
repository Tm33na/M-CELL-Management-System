import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.*;
import java.sql.*;

public class AdminViewComplaintServlet extends HttpServlet {

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
        out.println("<title>Complaints List</title>");
        out.println("<link rel='stylesheet' href='styles.css'>");
        out.println("</head>");
        out.println("<body class=\"bgnone\" style=\" background-color: #c2bebe;\">");
        out.println("<h2>Complaints List</h2>");
        out.println("<table>");
        out.println("<thead>");
        out.println("<tr>");
        out.println("<th>Token</th>");
        out.println("<th>Name</th>");
        out.println("<th>Email</th>");
        out.println("<th>Institute ID</th>");
        out.println("<th>Complaint Type</th>");
        out.println("<th>Priority</th>");
        out.println("<th>Location</th>");
        out.println("<th>Room No.</th>");
        out.println("<th>Subject</th>");
        out.println("<th>Message</th>");
        out.println("<th>Attachment</th>");
        out.println("</tr>");
        out.println("</thead>");
        out.println("<tbody>");
        
        try {
            Connection con = DatabaseManager.getConnection();
            
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM complaints WHERE status = 'Pending' ORDER BY id DESC");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                out.println("<tr>");
                out.println("<td>" + rs.getString("token") + "</td>");

                out.println("<td>" + rs.getString("name") + "</td>");
                out.println("<td>" + rs.getString("email") + "</td>");
                out.println("<td>" + rs.getString("institute_id") + "</td>");
                out.println("<td>" + rs.getString("complaint_type") + "</td>");
                out.println("<td>" + rs.getString("priority") + "</td>");
                out.println("<td>" + rs.getString("location") + "</td>");
                out.println("<td>" + rs.getString("room_no") + "</td>");
                out.println("<td>" + rs.getString("subject") + "</td>");
                out.println("<td>" + rs.getString("message") + "</td>");
                out.println("<td><a href='DownloadAttachmentServlet?token=" + rs.getString("token") + "'>Download</a></td>");
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
