<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>File Upload Form</title>
    <!-- Bootstrap CSS -->
    <link href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet">
    <!-- Custom CSS -->
    <style>
        .container {
            margin-top: 50px;
        }

        .card {
            border: none;
            box-shadow: 0px 4px 8px rgba(0, 0, 0, 0.1);
        }

        .card-body {
            padding: 30px;
        }

        .table-label {
            background-color: #007bff; /* Blue color */
            color: #fff; /* White color */
            padding: 10px;
            border-radius: 5px;
            text-align: center;
            margin-bottom: 20px;
            cursor: pointer; /* Change cursor to pointer when hovered over */
            display: block;
        }

        .form-group {
            margin-bottom: 20px;
        }

        .btn-primary {
            width: 100%;
        }

        .error-message {
            margin-top: 20px;
            text-align: center;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="row justify-content-center">
            <div class="col-md-6">
                <div class="card">
                    <div class="card-body">
                        <h2 class="card-title text-center mb-4">Upload XML File</h2>
                        <!-- Wrap the label within an anchor tag for redirection -->
                        <a href="displayTables.jsp" class="table-label mb-4">Display Table</a>
                        <form action="/navaera-poc/fileUploadServlet" method="post" enctype="multipart/form-data">
                            <div class="form-group">
                                <label for="xmlFile">Choose XML File</label>
                                <input type="file" class="form-control-file" id="xmlFile" name="xmlFile" accept=".xml" required>
                            </div>
                            <div class="text-center">
                                <button type="submit" class="btn btn-primary">Upload</button>
                            </div>
                        </form>
                        <div class="error-message">
                            <%-- Display error message if file size exceeds 100 MB --%>
                            <%@ include file="uploadFileSizeValidation.jsp" %>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <!-- Bootstrap JS (optional, if needed) -->
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
</body>
</html>
