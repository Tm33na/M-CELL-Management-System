import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.UUID;

public class SubmitComplaintServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        
        String name = request.getParameter("name");
        String email = request.getParameter("email");
        String instituteId = request.getParameter("instituteId");
        String complaintType = request.getParameter("complaintType");
        String priority = request.getParameter("priority");
        String location = request.getParameter("location");
        String roomNo = request.getParameter("roomNo");
        String subject = request.getParameter("subject");
        String message = request.getParameter("message");
        Part attachmentPart = request.getPart("attachment");
        String attachmentFormat = getAttachmentFormat(attachmentPart); // Get attachment format
        
        // Additional input validation
        if (name == null || email == null || instituteId == null || complaintType == null ||
            priority == null || location == null || subject == null || message == null || attachmentPart == null) {
            out.println("Please fill in all required fields and attach a file.");
            return;
        }
        
        String uniqueToken = generateUniqueToken();
        
        Connection con = null;
        PreparedStatement pstmt = null;
        try {
            con = DatabaseManager.getConnection(); // Get connection using DatabaseManager
            con.setAutoCommit(false); // Start transaction
            
            // Insert complaint into database
            pstmt = con.prepareStatement("INSERT INTO complaints (name, email, institute_id, complaint_type, priority, location, room_no, subject, message, token, attachment, attachment_format) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, instituteId);
            pstmt.setString(4, complaintType);
            pstmt.setString(5, priority);
            pstmt.setString(6, location);
            pstmt.setString(7, roomNo);
            pstmt.setString(8, subject);
            pstmt.setString(9, message);
            pstmt.setString(10, uniqueToken);
            
            if (attachmentPart != null && attachmentPart.getSize() > 0 && attachmentPart.getSize() < 10485760) { // Limit attachment size to 10 MB
                InputStream inputStream = attachmentPart.getInputStream();
                pstmt.setBlob(11, inputStream);
                pstmt.setString(12, attachmentFormat); // Set attachment format
            } else {
                pstmt.setNull(11, Types.BLOB);
                pstmt.setNull(12, Types.VARCHAR);
            }
            
            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                con.commit(); // Commit if successful
                
                
                // Write your HTML with JavaScript to create the alert
        out.println("<html><head><title>Alert Example</title></head><body>");
        out.println("<script type=\"text/javascript\">");
        out.println("alert('Complaint submitted successfully. Your unique token is:" +uniqueToken+ "');");
       out.println("window.location.href = 'UserHome.html';"); // Redirect to another page after showing the alert

        out.println("</script>");
        out.println("</body></html>");
                
            } else {
                con.rollback(); // Rollback if failed
                out.println("Failed to submit complaint.");
            }
        } catch (SQLException e) {
            out.println("An error occurred while processing the complaint: " + e.getMessage());
            e.printStackTrace(); // Log the exception for further investigation
            try {
                if (con != null) con.rollback(); // Rollback in case of exception
            } catch (SQLException ex) {
                ex.printStackTrace(); // Log rollback exception
            }
        } finally {
            // Close resources in a finally block
            DatabaseManager.closeStatement(pstmt); // Close PreparedStatement using DatabaseManager
            DatabaseManager.closeConnection(con); // Close Connection using DatabaseManager
        }
    }
    
    private String generateUniqueToken() {
        return UUID.randomUUID().toString();
    }
    
    private String getAttachmentFormat(Part attachmentPart) {
        // Extract attachment format from part's content-disposition header
        String header = attachmentPart.getHeader("content-disposition");
        if (header != null) {
            int indexOfLastDot = header.lastIndexOf('.');
            int indexOfLastQuote = header.lastIndexOf('"');
            if (indexOfLastDot != -1 && indexOfLastQuote != -1 && indexOfLastDot < indexOfLastQuote) {
                String extension = header.substring(indexOfLastDot + 1, indexOfLastQuote);
                return extension.toLowerCase();
            }
        }
        return null;
    }
}
