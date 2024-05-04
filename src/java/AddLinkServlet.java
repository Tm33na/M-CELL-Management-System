import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.sql.*;

public class AddLinkServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String title = request.getParameter("title");
        String url = request.getParameter("url");

        try {
            Connection con = DatabaseManager.getConnection();

            PreparedStatement insertStatement = con.prepareStatement("INSERT INTO Notification (title, url) VALUES (?, ?)");
            insertStatement.setString(1, title);
            insertStatement.setString(2, url);
            insertStatement.executeUpdate();

            out.println("<script>alert('Link added successfully.');</script>");
            RequestDispatcher rd = request.getRequestDispatcher("adminHome.html");
            rd.include(request, response);

            DatabaseManager.closeConnection(con);
        } catch (SQLException e) {
            System.out.println("Exception Caught: " + e);
        }
    }
}
