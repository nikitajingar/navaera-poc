<!DOCTYPE html>
<%@ page import="java.sql.*"%>
<%@ page import="java.util.*"%>
<%@ page import="com.poc.utils.DatabaseConnector"%>

<html>
<head>
<title>Table Selection</title>
<link
	href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css"
	rel="stylesheet">
<style>
body {
	background-color: #f0f0f0; /* mild background color */
	padding-top: 50px; /* top padding to accommodate navbar */
}

.container {
	max-width: 500px; /* limiting container width */
	margin: auto; /* centering container */
}
</style>
</head>
<body>

	<div class="container">
		<h2 class="mb-4">Select Table Name:</h2>


		<form action="/navaera-poc/xml-download" method="post" class="row" >

			<div class="form-group col-md-8">
				<label for="dropdown">Select an option:</label> <select
					class="form-control" id="dropdown" name="tableName">
					<%
					try (Connection connection = DatabaseConnector.getConnection()) {
						Statement statement = connection.createStatement();
						ResultSet tables = statement.executeQuery("SHOW TABLES FROM navaera");

						while (tables.next()) {
							String tableName = tables.getString("Tables_in_navaera");
					%>
					<option value="<%=tableName%>"><%=tableName%></option>
					<%
					}
					} catch (SQLException e) {
					e.printStackTrace();
					}
					%>
				</select>
			</div>
			<div class="form-group col-md-4">
				<button type="submit" class="btn btn-primary">Submit</button>
			</div>
	</form>
	</div>

	<script
		src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

</body>
</html>