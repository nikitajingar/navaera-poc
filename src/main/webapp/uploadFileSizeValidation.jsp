<% 
    final int MAX_FILE_SIZE = 100 * 1024 * 1024; // 100 MB
    
    // Check if file size exceeds maximum limit
    if (request.getContentLength() > MAX_FILE_SIZE) { %>
        File size exceeds maximum limit (100 MB).
<%  } %>
