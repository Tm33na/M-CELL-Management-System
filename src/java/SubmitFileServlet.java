import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

public class SubmitFileServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String fileName = request.getParameter("fileName");
        String fileFormat = request.getParameter("fileFormat");
        Part filePart = request.getPart("file");
        
        // Check file size
        long fileSize = filePart.getSize();
        if (fileSize > 10485760) { // Limit file size to 10 MB
            response.getWriter().println("File size exceeds the limit (10 MB).");
            return;
        }
        
        InputStream fileContent = filePart.getInputStream();
        
        Connection conn = null;
        PreparedStatement pstmt = null;
        try {
            // Get connection using DatabaseManager
            conn = DatabaseManager.getConnection();
            pstmt = conn.prepareStatement("INSERT INTO archives (file_name, file_format, file, date_stored) VALUES (?, ?, ?, CURDATE())");
            pstmt.setString(1, fileName);
            pstmt.setString(2, fileFormat);
            // Set the file content as a BLOB
            pstmt.setBlob(3, fileContent);
            pstmt.executeUpdate();
            
            // Insertion successful
            response.getWriter().println("File uploaded successfully.");
        } catch (Exception e) {
            // Error handling
            response.getWriter().println("An error occurred while uploading the file: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close resources using DatabaseManager
            DatabaseManager.closeStatement(pstmt);
            DatabaseManager.closeConnection(conn);
        }
    }
}
