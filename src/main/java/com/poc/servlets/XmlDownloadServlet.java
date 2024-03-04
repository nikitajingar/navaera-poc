package com.poc.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.poc.utils.Constants;

@WebServlet("/xml-download")
public class XmlDownloadServlet extends HttpServlet {

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tableName = request.getParameter("tableName");

        // Fetch records from the specified table
        List<HashMap<String, String>> records = fetchRecords(tableName);

        // Convert records to XML
        String xmlResponse = convertToXml(tableName, records);

        // Set response content type to XML
        response.setContentType("text/xml");
        PrintWriter out = response.getWriter();
        out.println(xmlResponse);
    }

    // Method to fetch records from the specified table
    private List<HashMap<String, String>> fetchRecords(String tableName) {
        List<HashMap<String,String>> records = new ArrayList<>();

        try {
            // Register JDBC driver	
            Class.forName(Constants.JDBC_DRIVER);

            // Open a connection
            Connection conn = DriverManager.getConnection(Constants.url, Constants.username, Constants.password);

            // Create a statement
            Statement stmt = conn.createStatement();

            // Execute SQL query to fetch records from the specified table
            String sql = "SELECT * FROM " + tableName + " LIMIT 100";
            ResultSet rs = stmt.executeQuery(sql);

            // Get metadata about the result set (column names)
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnCount = rsmd.getColumnCount();

            // Process the result set
            while (rs.next()) {
                HashMap<String, String> record = new HashMap<String,String>();
                ResultSetMetaData rsMetaData = rs.getMetaData();
                for (int i = 1; i <= columnCount; i++) {
                    record.put(rsMetaData.getColumnName(i),rs.getString(i));
                }
                records.add(record);
            }

            // Close the resources
            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            // Handle exceptions
        }

        return records;
    }

    // Method to convert records to XML
    private String convertToXml(String tableName, List<HashMap<String, String>> records) {
        StringBuilder xmlBuilder = new StringBuilder();
        xmlBuilder.append("<").append(tableName).append(">\n");

        for (HashMap<String, String> record : records) {
            xmlBuilder.append("<record>\n");            
            record.forEach((k,v) -> xmlBuilder.append("<").append(k).append(">").append(v).append("</").append(k).append(">"));
            xmlBuilder.append("</record>\n");
        }

        xmlBuilder.append("</").append(tableName).append(">");
        return xmlBuilder.toString();
    }
}
