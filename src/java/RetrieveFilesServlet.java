import java.io.*;
import java.sql.*;
import java.util.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;

public class RetrieveFilesServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Map<String, Object>> files = new ArrayList<>();
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            conn = DatabaseManager.getConnection(); // Get connection using DatabaseManager
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM archives");
            while (rs.next()) {
                Map<String, Object> file = new HashMap<>();
                file.put("id", rs.getInt("id"));
                file.put("fileName", rs.getString("file_name"));
                file.put("dateStored", rs.getString("date_stored"));
                files.add(file);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseManager.closeResultSet(rs); // Close ResultSet using DatabaseManager
            DatabaseManager.closeStatement(stmt); // Close Statement using DatabaseManager
            DatabaseManager.closeConnection(conn); // Close Connection using DatabaseManager
        }

        // Manually serialize files list into JSON
        StringBuilder jsonBuilder = new StringBuilder("[");
        for (Map<String, Object> file : files) {
            jsonBuilder.append("{");
            jsonBuilder.append("\"id\":").append(file.get("id")).append(",");
            jsonBuilder.append("\"fileName\":\"").append(file.get("fileName")).append("\",");
            jsonBuilder.append("\"dateStored\":\"").append(file.get("dateStored")).append("\"");
            jsonBuilder.append("},");
        }
        if (files.size() > 0) {
            // Remove the trailing comma
            jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
        }
        jsonBuilder.append("]");

        // Set response content type
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Write JSON string to response
        PrintWriter out = response.getWriter();
        out.print(jsonBuilder.toString());
        out.flush();
    }
}
