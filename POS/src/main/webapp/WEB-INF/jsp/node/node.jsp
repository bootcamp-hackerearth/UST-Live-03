<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Edit Node</title>

    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: Arial, sans-serif;
        }

        html, body {
            height: 100%;
        }

        body {
            background-color: #f5f6fc;
            color: #667eea;
        }

        .header {
            height: 50px;
            display: flex;
            align-items: center;
            padding: 0 20px;
            background-color: #f5f6fc;
            box-shadow: 0 0 7px rgba(75,108,183,0.25);
        }

        .hamburger {
            font-size: 22px;
            cursor: pointer;
            margin-right: 15px;
        }

        .header h3 {
            flex-grow: 1;
        }

        .header a {
            background: #667eea;
            color: white;
            padding: 6px 10px;
            border-radius: 5px;
            font-size: 12px;
            text-decoration: none;
        }

        .sidebar {
            position: fixed;
            left: -250px;
            top: 0;
            width: 250px;
            height: 100vh;
            background: #f5f6fc;
            transition: 0.3s;
            padding-top: 15px;
        }

        .sidebar.active {
            left: 0;
            box-shadow: 0 0 20px rgba(75,108,183,0.25);
        }

        .close-btn {
            text-align: right;
            padding: 10px 20px;
            cursor: pointer;
            font-size: 20px;
        }

        .sidebar a {
            display: block;
            padding: 12px 18px;
            margin: 6px 10px;
            border: 1px solid #667eea;
            color: #667eea;
            text-decoration: none;
            border-radius: 5px;
            font-size: 14px;
        }

        .sidebar a:hover {
            background: #667eea;
            color: white;
        }

        .content {
            padding: 30px;
            transition: margin-left 0.3s;
            min-height: calc(100vh - 50px);
        }

        .content.shift {
            margin-left: 250px;
        }

        .card {
            max-width: 420px;
            margin: auto;
            background: white;
            border-radius: 12px;
            padding: 25px;
            box-shadow: 0 0 15px rgba(75,108,183,0.25);
        }

        .card h3 {
            text-align: center;
            margin-bottom: 20px;
        }

        label {
            font-size: 14px;
            margin-bottom: 5px;
            display: block;
            color: #444;
        }

        input, select {
            width: 100%;
            padding: 10px 12px;
            border-radius: 6px;
            border: 1px solid #ccc;
            font-size: 14px;
        }

        input:focus, select:focus {
            outline: none;
            border-color: #667eea;
        }

        select[multiple] {
            height: 100px;
        }

        .btn-group {
            display: flex;
            justify-content: space-between;
            margin-top: 20px;
        }

        .btn {
            padding: 8px 14px;
            border-radius: 6px;
            font-size: 13px;
            text-decoration: none;
            cursor: pointer;
        }

        .btn-primary {
            background: #667eea;
            color: white;
            border: none;
        }

        .btn-primary:hover {
            background: #5a67d8;
        }

        .btn-secondary {
            background: #e2e8f0;
            color: #333;
        }

        .alert {
            text-align: center;
            padding: 10px;
            background: #fed7d7;
            color: #742a2a;
            border-radius: 6px;
            margin-bottom: 15px;
            font-size: 13px;
        }

        .message {
            text-align: center;
            font-size: 12px;
            margin-bottom: 10px;
            color: green;
        }
    </style>
</head>

<body>

<div id="sidebar" class="sidebar">
    <div class="close-btn" onclick="toggleSidebar()">✖</div>
    <c:forEach var="node" items="${nodes}">
    <a href="${node.path}"> ${node.identifier}</a>
    </c:forEach>
</div>

<div class="header">
    <span class="hamburger" onclick="toggleSidebar()">☰</span>
    <h3>Node Management</h3>
    <a href="/logout">Logout</a>
</div>

<div id="content" class="content">
    <div class="card">

        <h3>Edit Node</h3>

        <c:if test="${not empty message}">
            <div class="message">${message}</div>
        </c:if>

        <c:if test="${empty nodeDto}">
            <div class="alert">Node not found</div>
        </c:if>

        <c:if test="${not empty nodeDto}">
            <form:form action="/node/update" method="post" modelAttribute="nodeDto">

                <form:hidden path="id"/>

                <label>Node Name</label>
                <form:input path="identifier" required="true" readOnly="true"/>

                <label style="margin-top:12px;">Node Path</label>
                <form:input path="path" required="true"/>

                <label style="margin-top:12px;">Roles</label>
                <form:select path="roles" multiple="true" required="true">
                    <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
                </form:select>

                <div class="btn-group">
                    <a href="/node/list" class="btn btn-secondary">Back</a>
                    <button type="submit" class="btn btn-primary">Update</button>
                </div>

            </form:form>
        </c:if>

    </div>
</div>

<script>
    function toggleSidebar() {
        document.getElementById("sidebar").classList.toggle("active");
        document.getElementById("content").classList.toggle("shift");
    }
</script>

</body>
</html>