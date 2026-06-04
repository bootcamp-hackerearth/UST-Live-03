<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Node</title>

    <style>
        body {
            margin: 0;
            font-family: 'Segoe UI', sans-serif;
            background: #eef2f7;
        }

        .header {
            background: #1e88e5;
            padding: 15px 25px;
            color: white;
            font-size: 20px;
            font-weight: bold;
        }

        .container {
            display: flex;
            justify-content: center;
            padding: 30px;
        }

        .card {
            width: 400px;
            background: white;
            padding: 22px;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
        }

        h2 {
            text-align: center;
            margin-bottom: 18px;
            color: #333;
            font-size: 18px;
        }

        .success {
            text-align: center;
            color: green;
            font-size: 13px;
            margin-bottom: 10px;
        }

        .form-group {
            margin-bottom: 14px;
        }

        label {
            font-size: 12px;
            color: #555;
            font-weight: 500;
        }

        input, select {
            width: 100%;
            padding: 10px;
            margin-top: 5px;
            border-radius: 6px;
            border: 1px solid #ddd;
            font-size: 13px;
        }

        select[multiple] {
            height: 90px;
        }

        input:focus, select:focus {
            border-color: #1e88e5;
            outline: none;
            box-shadow: 0 0 0 2px rgba(30,136,229,0.2);
        }

        .btn {
            width: 100%;
            padding: 11px;
            margin-top: 10px;
            border-radius: 6px;
            border: none;
            background: #28a745;
            color: white;
            font-weight: 600;
            cursor: pointer;
        }

        .btn:hover {
            opacity: 0.9;
        }

        .back {
            display: block;
            text-align: center;
            margin-top: 10px;
            font-size: 12px;
            color: #1e88e5;
            text-decoration: none;
        }

        .back:hover {
            text-decoration: underline;
        }

        .error {
            text-align: center;
            color: #dc3545;
            font-size: 12px;
            margin-top: 8px;
        }

        small {
            font-size: 11px;
            color: #777;
        }
    </style>
</head>

<body>

<div class="header">
    POS - Add Node
</div>

<div class="container">

    <div class="card">

        <h2>Create Node</h2>

        <c:if test="${not empty message}">
            <div class="success">
                ${message}
            </div>
        </c:if>

        <form:form method="post" action="/node/add" modelAttribute="nodeDto">

            <div class="form-group">
                <label>Node Identifier</label>
                <form:input path="identifier" placeholder="Enter node name" required="true"/>
            </div>

            <div class="form-group">
                <label>Node Path</label>
                <form:input path="path" placeholder="/admin/dashboard" required="true"/>
            </div>

            <div class="form-group">
                <label>Assign Roles</label>
                <form:select path="roles" multiple="true">
                    <c:forEach var="role" items="${roles}">
                        <option value="${role.identifier}">
                            ${role.identifier}
                        </option>
                    </c:forEach>
                </form:select>
                <small>Hold Ctrl/Cmd to select multiple roles</small>
            </div>

            <button type="submit" class="btn">Add Node</button>

            <p class="error">${message}</p>

        </form:form>

        <a href="/" class="back">← Back to Dashboard</a>

    </div>

</div>

</body>
</html>