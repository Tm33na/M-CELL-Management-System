import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FetchUserDetailsServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // Check if session exists
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            // Session doesn't exist or user not logged in, redirect to login page
            response.sendRedirect("LoginPage.html");
            return;
        }

        // User is logged in, fetch institute ID from the session
        String instituteId = (String) session.getAttribute("userId");

        // SQL query to fetch user details based on institute ID
        String query = "SELECT name, email FROM member WHERE institute_id = ?";

        try (Connection con = DatabaseManager.getConnection();
             PreparedStatement pstmt = con.prepareStatement(query)) {

            // Set the institute ID parameter in the SQL query
            pstmt.setString(1, instituteId);

            // Execute the query
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Fetch user details from the result set
                    String name = rs.getString("name");
                    String email = rs.getString("email");

                    // Print the HTML form with embedded user details
                    out.println("<!DOCTYPE html>");
                    out.println("<html lang=\"en\">");
                    out.println("<head>");
                    out.println("<meta charset=\"UTF-8\">");
                    out.println("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
                    out.println("<title>Complaint Submission</title>");
                    out.println("<link rel=\"icon\" type=\"image/x-icon\" href=\"IIITALogo.png\">");
                    out.println("<link rel=\"stylesheet\" href=\"style2.css\"/>");
                    out.println("</head>");
                    out.println("<body>");
                    out.println("<div id=\"complaint-form-container\">");
                    out.println("<div class=\"container\">");
                    out.println("<h2>Complaint Submission</h2>");
                    out.println("<div class=\"close\"><a><a href=\"UserHome.html\"><i class=\"fa-solid fa-xmark\"></i></a></a></div>");
                    out.println("<form id=\"complaint-form\" action=\"SubmitComplaintServlet\" method=\"post\" enctype=\"multipart/form-data\">");

                    // Dynamically generate input fields
                    out.println("<div class=\"row mb-3\">");
                    out.println("<div class=\"col\">");
                    out.println("<label for=\"name\" class=\"form-label\">Name:</label>");
                    out.println("<input type=\"text\" id=\"name\" name=\"name\" class=\"form-control\" value=\"" + name + "\" readonly>");
                    out.println("</div>");
                    out.println("<div class=\"col\">");
                    out.println("<label for=\"email\" class=\"form-label\">Email:</label>");
                    out.println("<input type=\"email\" id=\"email\" name=\"email\" class=\"form-control\" value=\"" + email + "\" readonly>");
                    out.println("</div>");
                    out.println("</div>");

                    out.println("<div class=\"row mb-3\">");
                    out.println("<div class=\"col\">");
                    out.println("<label for=\"instituteId\" class=\"form-label\">Institute ID:</label>");
                    out.println("<input type=\"text\" id=\"instituteId\" name=\"instituteId\" class=\"form-control\" value=\"" + instituteId + "\" readonly>");
                    out.println("</div>");
                    out.println("<div class=\"col\">");
                    out.println("<label for=\"complaintType\" class=\"form-label\">Complaint Type:</label>");
                    out.println("<select id=\"complaintType\" name=\"complaintType\" class=\"form-select\">");
                    out.println("<option value=\"Network\">Network</option>");
                    out.println("<option value=\"PCMaintenance\">PC Maintenance</option>");
                    out.println("<option value=\"Projectors\">Projectors</option>");
                    out.println("<option value=\"Cable\">Cable TV</option>");
                    out.println("<option value=\"Telephone\">Telephone</option>");
                    out.println("<option value=\"Email\">Email</option>");
                    out.println("<option value=\"select\" selected disabled>Select an Option</option>");
                    out.println("</select>");
                    out.println("</div>");
                    out.println("</div>");

                    out.println("<div class=\"row mb-3\">");
                    out.println("<div class=\"col\">");
                    out.println("<label for=\"priority\" class=\"form-label\">Priority:</label>");
                    out.println("<select id=\"priority\" name=\"priority\" class=\"form-select\">");
                    out.println("<option value=\"low\">Low</option>");
                    out.println("<option value=\"medium\">Medium</option>");
                    out.println("<option value=\"high\">High</option>");
                    out.println("</select>");
                    out.println("</div>");
                    out.println("<div class=\"col\">");
                    out.println("<label for=\"location\" class=\"form-label\">Location:</label>");
                    out.println("<select id=\"location\" name=\"location\" class=\"form-select\">");
                    out.println("<option value=\"library\">Library</option>");
                    out.println("<option value=\"cc1\">CC1</option>");
                    out.println("<option value=\"cc2\">CC2</option>");
                    out.println("<option value=\"cc3\">CC3</option>");
                    out.println("<option value=\"admin\">Admin Building</option>");
                    out.println("<option value=\"lt\">LT</option>");
                    out.println("<option value=\"healthcentre\">Health Centre</option>");
                    out.println("</select>");
                    out.println("</div>");
                    out.println("</div>");

                    out.println("<div class=\"row mb-3\">");
                    out.println("<div class=\"col\">");
                    out.println("<label for=\"roomNo\" class=\"form-label\">Room No.:</label>");
                    out.println("<input type=\"text\" id=\"roomNo\" name=\"roomNo\" class=\"form-control\">");
                    out.println("</div>");
                    out.println("<div class=\"col\">");
                    out.println("<label for=\"subject\" class=\"form-label\">Complaint Subject:</label>");
                    out.println("<input type=\"text\" id=\"subject\" name=\"subject\" class=\"form-control\" required>");
                    out.println("</div>");
                    out.println("</div>");

                    out.println("<div class=\"form-group mb-3\">");
                    out.println("<label for=\"message\" class=\"form-label\">Complaint Message:</label>");
                    out.println("<textarea id=\"message\" name=\"message\" class=\"form-control\" rows=\"4\" cols=\"50\" required></textarea>");
                    out.println("</div>");

                    out.println("<div class=\"input-group mb-3\">");
                    out.println("<input type=\"file\" class=\"form-control\" id=\"inputGroupFile02\" name=\"attachment\">");
                    out.println("</div>");

                    out.println("<input type=\"submit\" value=\"Submit\" class=\"btn-submit\">");
                    out.println("</form>");
                    out.println("</div>");
                    out.println("</div>");

                    // JavaScript to fetch user details and populate the form fields
                    out.println("<script>");
                    out.println("fetch('FetchUserDetailsServlet')");
                    out.println(".then(response => response.json())");
                    out.println(".then(data => {");
                    out.println("if (Object.keys(data).length !== 0) {");
                    out.println("document.getElementById('name').value = data.name || '';");
                    out.println("document.getElementById('email').value = data.email || '';");
                    out.println("document.getElementById('instituteId').value = data.instituteId || '';");
                    out.println("}");
                    out.println("})");
                    out.println(".catch(error => console.error('Error:', error));");
                    out.println("</script>");

                    out.println("</body>");
                    out.println("</html>");
                } else {
                    // If user details not found, print error message or redirect to an error page
                    out.println("User details not found.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Print error message or redirect to an error page
            out.println("An error occurred while fetching user details.");
        }
    }
}
