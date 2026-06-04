<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Update User</title>

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
            overflow: overlay;
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
            max-width: 480px;
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

        .btn-update {
            width: 100%;
            margin-top: 15px;
            padding: 10px;
            border: none;
            border-radius: 6px;
            background: #667eea;
            color: white;
            font-size: 14px;
            cursor: pointer;
        }

        .btn-update:hover {
            background: #5a67d8;
        }

        small {
            font-size: 11px;
            color: #666;
        }

        .badge {
            display: inline-block;
            padding: 3px 6px;
            background: #718096;
            color: white;
            border-radius: 4px;
            font-size: 11px;
            margin-right: 4px;
        }

        .back-link {
            margin-top: 15px;
            text-align: center;
        }

        .back-link a {
            font-size: 13px;
            text-decoration: none;
            color: #667eea;
        }

        form p {
            font-size: 12px;
            color: red;
            text-align: center;
            margin-top: 8px;
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
    <h3>User Management</h3>
    <a href="/logout">Logout</a>
</div>

<div id="content" class="content">
    <div class="card">

        <h3>Update User</h3>

        <form:form action="/user/update" method="post" modelAttribute="user">

            <form:input type="hidden" path="id"/>

            <label>Name</label>
            <form:input path="name" required="true"/>

            <label style="margin-top:12px;">Email</label>
            <form:input path="username" type="email" required="true"/>

            <label style="margin-top:12px;">Phone Number</label>
            <form:input
                    path="phoneNo"
                    type="tel"
                    pattern="[0-9]{10}"
                    title="Enter a valid 10-digit phone number"
                    required="true"/>

            <label style="margin-top:12px;">Roles</label>

            <div style="margin-bottom:6px;">
                <c:forEach var="r" items="${user.roles}">
                    <span class="badge">${r}</span>
                </c:forEach>
            </div>

            <form:select path="roles" multiple="true" required="true">
                <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
            </form:select>

            <button type="submit" class="btn-update">Update User</button>

            <p>${message}</p>
        </form:form>

        <div class="back-link">
            <a href="/user/list">← Back to User List</a>
        </div>

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
