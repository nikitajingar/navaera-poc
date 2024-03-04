package com.poc.servlets;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.poc.utils.DatabaseConnector;

@MultipartConfig
@WebServlet(urlPatterns = "/fileUploadServlet", name = "FileUploadServlet")
public class FileUploadServlet extends HttpServlet {

	static String tempFilepath;

	static Connection conn;

	public FileUploadServlet() throws ClassNotFoundException, SQLException {
		conn = DatabaseConnector.getConnection();// TODO Auto-generated constructor stub
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {

		try {
			Part filePart = request.getPart("xmlFile");
			createFile(filePart, response);
			File xmlFile = new File(tempFilepath);

			try (InputStream stream = new FileInputStream(tempFilepath)) {
				XMLInputFactory inputFactory = XMLInputFactory.newFactory();
				inputFactory.setProperty(XMLInputFactory.IS_COALESCING, true);
				XMLStreamReader reader = inputFactory.createXMLStreamReader(stream);
				XmlMapper xmlMapper = new XmlMapper();
				reader.next();
				String tableName = String.valueOf(reader.getName());

				Map<String, List<Map<String, Object>>> map = xmlMapper.readValue(reader, Map.class);
				List<String> columns = new ArrayList();
				List<Map<String, Object>> rows = map.get("row");
				rows.get(0).forEach((k, v) -> columns.add(k));

				if (!checkTableExist(tableName))
					createTable(tableName, columns);

				// insert table method call
				insertRecord(tableName, columns, rows);

			} catch (XMLStreamException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getFileName(final Part part) {
		final String partHeader = part.getHeader("content-disposition");
		for (String content : part.getHeader("content-disposition").split(";")) {
			if (content.trim().startsWith("filename")) {
				return content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
			}
		}
		return null;
	}

	private String createFile(Part filePart, HttpServletResponse response) throws IOException {
		final String fileName = getFileName(filePart);
		// temp added this path due to permission issue in local machine.
		String path = "D://temp/";
		OutputStream out = null;
		InputStream filecontent = null;
		final PrintWriter writer = response.getWriter();
		tempFilepath = path + '/' + fileName;

		try {
			out = new FileOutputStream(new File(path + File.separator + fileName));
			filecontent = filePart.getInputStream();

			int read = 0;
			final byte[] bytes = new byte[1024];

			while ((read = filecontent.read(bytes)) != -1) {
				out.write(bytes, 0, read);
			}
			response.sendRedirect("success.jsp");

			writer.println("New file " + fileName + " created at " + path);

		} catch (FileNotFoundException fne) {
			writer.println("You either did not specify a file to upload or are "
					+ "trying to upload a file to a protected or nonexistent " + "location.");
			writer.println("<br/> ERROR: " + fne.getMessage());

		} finally {
			if (out != null) {
				out.close();
			}
			if (filecontent != null) {
				filecontent.close();
			}
			if (writer != null) {
				writer.close();
			}
		}
		return tempFilepath;
	}

	private boolean checkTableExist(String tableName) throws SQLException {
		PreparedStatement preparedStatement = conn.prepareStatement(
				"SELECT count(*) " + "FROM information_schema.tables " + "WHERE table_name = ?" + "LIMIT 1;");
		preparedStatement.setString(1, tableName);

		ResultSet resultSet = preparedStatement.executeQuery();
		resultSet.next();
		return resultSet.getInt(1) != 0;
	}

	private void createTable(String tableName, List<String> columns) throws SQLException {
		final String CREATE_TABLE_QUERY = "CREATE TABLE %s (id int NOT NULL AUTO_INCREMENT , %s, PRIMARY KEY (`id`) )";
		columns = columns.stream().map(value -> value.concat(" VARCHAR(255)")).collect(Collectors.toList());
		Statement statement = conn.createStatement();
		String createQuery = String.format(CREATE_TABLE_QUERY, tableName, String.join(",", columns));
		statement.executeUpdate(createQuery);
	}

	private void insertRecord(String tableName, List<String> columns, List<Map<String, Object>> rows)
			throws SQLException {
		List<String> insertQeuryValues = new ArrayList();

		rows.forEach(row -> insertQeuryValues.add(concatenateValues(row)));

		String insertQuery = "INSERT INTO %s (%s) VALUES %s";

		Statement statement = conn.createStatement();
		String insertFormattedQuery = String.format(insertQuery, tableName, String.join(",", columns),
				String.join(",", insertQeuryValues));
		statement.executeUpdate(insertFormattedQuery);

	}

	public static String concatenateValues(Map<String, Object> map) {
		String valuesString = map.values().stream().map(Object::toString).map(s -> "\'" + s + "\'")
				.collect(Collectors.joining(",", "(", ")"));
		return valuesString;
	}
}
