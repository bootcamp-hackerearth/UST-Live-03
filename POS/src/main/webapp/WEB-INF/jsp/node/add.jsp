<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Node</title>

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
            max-width: 460px;
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

        <h3>Add New Node</h3>

        <c:if test="${not empty node}">
            <div class="alert-success">
                ${role}
            </div>
        </c:if>

        <form:form method="post"
                   action="/node/add"
                   modelAttribute="nodeDto">

            <label>Node Identifier</label>
            <form:input
                    path="identifier"
                    placeholder="Enter node identifier"
                    required="true"/>

            <label style="margin-top:12px;">Node Path</label>
            <form:input
                    path="path"
                    placeholder="Enter node path"
                    required="true"/>

            <label style="margin-top:12px;">Roles</label>
            <form:select path="roles" multiple="true" required="true">
                <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
            </form:select>

            <div class="btn-group">
                <a href="/node/list" class="btn btn-secondary">Back</a>
                <button type="submit" class="btn btn-primary">Add Node</button>
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