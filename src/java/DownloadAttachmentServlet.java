import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.*;

public class DownloadAttachmentServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String token = request.getParameter("token");
        
        if (token == null || token.isEmpty()) {
            response.getWriter().println("Invalid token");
            return;
        }
        
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            con = DatabaseManager.getConnection();
            pstmt = con.prepareStatement("SELECT attachment, attachment_format FROM complaints WHERE token = ?");
            pstmt.setString(1, token);
            rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Blob blob = (Blob) rs.getBlob("attachment");
                String attachmentFormat = rs.getString("attachment_format").trim(); // Trim attachment format

                if (blob != null) {
                    InputStream inputStream = blob.getBinaryStream();
                    int fileLength = (int) blob.length();
                    
                    response.setContentType(getContentType(attachmentFormat));
                    response.setContentLength(fileLength);
                    
                    // Trim the attachment format before appending it to the filename
                    String trimmedAttachmentFormat = attachmentFormat.trim();
                    response.setHeader("Content-Disposition", "attachment; filename=\"attachment." + trimmedAttachmentFormat + "\"");
                    
                    OutputStream outputStream = response.getOutputStream();
                    byte[] buffer = new byte[4096];
                    int bytesRead = -1;
                    
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    
                    inputStream.close();
                    outputStream.close();
                } else {
                    response.getWriter().println("Attachment not found");
                }
            } else {
                response.getWriter().println("Token not found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().println("Error occurred while downloading attachment: " + e.getMessage());
        } finally {
            DatabaseManager.closeResultSet(rs);
            DatabaseManager.closeStatement(pstmt);
            DatabaseManager.closeConnection(con);
        }
    }

    private String getContentType(String attachmentFormat) {
        switch (attachmentFormat) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "pdf":
                return "application/pdf";
            // Add more cases for other file formats if needed
            default:
                return "application/octet-stream";
        }
    }
}
