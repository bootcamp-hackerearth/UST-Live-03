<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Warehouse</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 30px;
        }
        h2 {
            margin-bottom: 15px;
        }
        .form-container {
            width: 400px;
        }
        label {
            display: block;
            margin-top: 15px;
            font-weight: bold;
        }
        input, select {
            width: 100%;
            padding: 8px;
            margin-top: 5px;
        }
        .btn {
            margin-top: 20px;
            padding: 8px 14px;
            border: none;
            cursor: pointer;
            color: white;
            border-radius: 4px;
            font-size: 14px;
            text-decoration: none;
        }
        .btn-save {
            background-color: #28a745;
        }
        .btn-back {
            background-color: #6c757d;
            margin-left: 10px;
        }
        .message {
            font-weight: bold;
            margin-bottom: 10px;
        }
        .success {
            color: green;
        }
        .error {
            color: red;
        }
    </style>
</head>

<body>

<h2>Add Warehouse</h2>

<!--  MESSAGE DISPLAY -->
<c:if test="${not empty message}">
    <div class="message ${success ? 'success' : 'error'}">
        ${message}
    </div>
</c:if>

<div class="form-container">

   <form action="${pageContext.request.contextPath}/warehouse/add" method="post">

        <label>Warehouse Name</label>
        <input type="text" name="identifier" required />

        <label>Code</label>
        <input type="text" name="code" required />

        <label>Location</label>
        <input type="text" name="location" />

        <label>Address</label>
        <input type="text" name="address" />
        <label>Status</label>
        <select name="status">
            <option value="true">ACTIVE</option>
            <option value="false">INACTIVE</option>
        </select>

        <button type="submit" class="btn btn-save">Save</button>

        <a href="${pageContext.request.contextPath}/warehouse/list"
           class="btn btn-back">Back to List</a>

    </form>

</div>

</body>
</html>