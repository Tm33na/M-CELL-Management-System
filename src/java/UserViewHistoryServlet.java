import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.sql.*;
import jakarta.servlet.http.HttpSession;

@WebServlet("/UserViewHistoryServlet")
public class UserViewHistoryServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Forward GET requests to doPost
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Get the active session
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("userId") != null) {
            
            // User is logged in, fetch institute ID from the session
            String instituteID = (String) session.getAttribute("userId");

            // Establishing connection to the database using the database manager
            try {
                Connection con = DatabaseManager.getConnection();

                // Fetching user's history based on institute ID
                PreparedStatement historyStmt = con.prepareStatement("SELECT * FROM complaints WHERE institute_id=? ORDER BY id DESC");
                historyStmt.setString(1, instituteID);
                ResultSet historyResult = historyStmt.executeQuery();

                // Generate HTML response
                out.println("<!DOCTYPE html>");
                out.println("<html>");
                out.println("<head>");
                out.println("<meta charset='UTF-8'>");
                out.println("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
                out.println("<title>Your History</title>");
                out.println("<link rel='icon' type='image/x-icon' href='IIITALogo.png'>");
                out.println( "<link rel='stylesheet' href='https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css' integrity='sha512-DTOQO9RWCH3ppGqcWaEA1BIZOC6xxalwEsw9c2QQeAIftl+Vegovlnee1c9QX4TctnWMn13TZye+giMm8e2LwA==' crossorigin='anonymous' referrerpolicy='no-referrer' />"
);
                                


                out.println("<link rel='stylesheet' href='styles.css'>");
                out.println("</head>");
                out.println("<body class=\"bgnone\">");
                out.println("  <a href=\"UserHome.html\" class=\"back\"style=\"background:transparent;\"><i class=\"fa-solid fa-arrow-left\" style=\" position:relative; top:-15px; left:-20px; font-size:30px;font-weight:700;color:black; \"></i></a> <h2 style=\"margin-left:55px;\">     Your History</h2>");
                out.println("<table>");
                out.println("<thead>");
                out.println("<tr>");
                out.println("<th>Name</th>");
                out.println("<th>Email</th>");
                out.println("<th>Complaint Type</th>");
                out.println("<th>Priority</th>");
                out.println("<th>Location</th>");
                out.println("<th>Room No.</th>");
                out.println("<th>Subject</th>");
                out.println("<th>Message</th>");
                out.println("<th>Status</th>");
                out.println("<th>Attachment</th>");
                out.println("</tr>");
                out.println("</thead>");
                out.println("<tbody>");

                // Iterate through the history results and generate table rows
                while (historyResult.next()) {
                    out.println("<tr>");
                    out.println("<td>" + historyResult.getString("name") + "</td>");
                    out.println("<td>" + historyResult.getString("email") + "</td>");
                    out.println("<td>" + historyResult.getString("complaint_type") + "</td>");
                    out.println("<td>" + historyResult.getString("priority") + "</td>");
                    out.println("<td>" + historyResult.getString("location") + "</td>");
                    out.println("<td>" + historyResult.getString("room_no") + "</td>");
                    out.println("<td>" + historyResult.getString("subject") + "</td>");
                    out.println("<td>" + historyResult.getString("message") + "</td>");
                    out.println("<td>" + historyResult.getString("Status") + "</td>");
                    out.println("<td><a href='DownloadAttachmentServlet?token=" + historyResult.getString("token") + "'>Download</a></td>");
                    out.println("</tr>");
                }

                out.println("</tbody>");
                out.println("</table>");
                out.println("</body>");
                out.println("</html>");

                con.close();
            } catch (Exception e) {
                // Log the exception
                e.printStackTrace();
                // Print a generic error message
                out.println("<p>An error occurred. Please try again later.</p>");
            }
        } else {
            // User is not logged in, or session is invalid
            out.println("<p>Please <a href='userlogin.html'>login</a> to view your history.</p>");
        }
    }
}
