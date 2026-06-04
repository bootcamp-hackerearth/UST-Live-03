<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Role</title>

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

        /* CONTENT */
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
            display: block;
            font-size: 14px;
            margin-bottom: 5px;
            color: #444;
        }

        input {
            width: 100%;
            padding: 10px 12px;
            border-radius: 6px;
            border: 1px solid #ccc;
            font-size: 14px;
        }

        input:focus {
            outline: none;
            border-color: #667eea;
        }

        .btn-group {
            display: flex;
            justify-content: space-between;
            gap: 10px;
            margin-top: 20px;
        }

        .btn {
            flex: 1;
            padding: 10px;
            border-radius: 6px;
            font-size: 14px;
            text-align: center;
            text-decoration: none;
            cursor: pointer;
            border: none;
        }

        .btn-primary {
            background: #667eea;
            color: white;
        }

        .btn-primary:hover {
            background: #5a67d8;
        }

        .btn-secondary {
            background: #718096;
            color: white;
        }

        .alert-success {
            background: #c6f6d5;
            color: #22543d;
            padding: 10px;
            border-radius: 5px;
            font-size: 13px;
            text-align: center;
            margin-bottom: 15px;
        }

        #message {
            font-size: 12px;
            color: red;
            text-align: center;
            margin-top: 10px;
        }
    </style>
</head>

<body>

<!-- SIDEBAR -->
<div id="sidebar" class="sidebar">
    <div class="close-btn" onclick="toggleSidebar()">✖</div>
    <c:forEach var="node" items="${nodes}">
    <a href="${node.path}"> ${node.identifier}</a>
    </c:forEach>
</div>

<!-- HEADER -->
<div class="header">
    <span class="hamburger" onclick="toggleSidebar()">☰</span>
    <h3>Role Management</h3>
    <a href="/logout">Logout</a>
</div>

<!-- CONTENT -->
<div id="content" class="content">
    <div class="card">

        <h3>Add New Role</h3>

        <c:if test="${not empty role}">
            <div class="alert-success">
                ${role}
            </div>
        </c:if>

        <form:form method="post" action="/role/add" modelAttribute="roleDto">

            <label>Role Name</label>
            <form:input
                    path="identifier"
                    placeholder="Enter role name"
                    required="true"/>

            <label style="margin-top:12px;">Role Description</label>
            <form:input
                    path="description"
                    placeholder="Enter role description"
                    required="true"/>

            <div class="btn-group">
                <a href="/role/list" class="btn btn-secondary">Back</a>
                <button type="submit" class="btn btn-primary">Add Role</button>
            </div>

            <p id="message">${message}</p>
        </form:form>

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