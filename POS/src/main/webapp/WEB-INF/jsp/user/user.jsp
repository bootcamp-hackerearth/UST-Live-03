<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update User</title>

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
            width: 380px;
            background: white;
            border-radius: 10px;
            padding: 20px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
        }

        h2 {
            text-align: center;
            margin-bottom: 15px;
            color: #333;
        }

        .message {
            text-align: center;
            color: green;
            font-size: 13px;
            margin-bottom: 10px;
        }

        .form-group {
            margin-bottom: 12px;
        }

        label {
            font-size: 12px;
            color: #555;
            font-weight: 500;
        }

        input, select {
            width: 100%;
            padding: 9px;
            margin-top: 4px;
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

        .badge {
            display: inline-block;
            background: #e3f2fd;
            color: #1e88e5;
            padding: 4px 8px;
            border-radius: 4px;
            font-size: 11px;
            margin: 2px;
        }

        .roles-box {
            margin-bottom: 8px;
        }

        .btn {
            width: 100%;
            padding: 11px;
            margin-top: 10px;
            border-radius: 6px;
            border: none;
            background: #1e88e5;
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

        small {
            font-size: 11px;
            color: #777;
        }
    </style>
</head>

<body>

<div class="header">
    POS - Edit User
</div>

<div class="container">

    <div class="card">

        <h2>Edit User</h2>

        <div class="message">${message}</div>

        <form:form action="/user/update" method="post" modelAttribute="userDto">

            <form:input type="hidden" path="id"/>

            <div class="form-group">
                <label>Name</label>
                <form:input path="name" required="true"/>
            </div>

            <div class="form-group">
                <label>Email</label>
                <form:input path="username" type="email" required="true"/>
            </div>

            <div class="form-group">
                <label>Phone</label>
                <form:input path="phoneNo" required="true"/>
            </div>

            <div class="form-group">
                <label>Current Roles</label>
                <div class="roles-box">
                    <c:forEach var="r" items="${user.roles}">
                        <span class="badge">${r}</span>
                    </c:forEach>
                </div>
            </div>

            <div class="form-group">
                <label>Select Roles</label>
                <form:select path="roles" multiple="true">
                    <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
                </form:select>
                <small>Hold Ctrl/Cmd to select multiple</small>
            </div>

            <button type="submit" class="btn">Update User</button>

        </form:form>

        <a href="/user/list" class="back">← Back to User List</a>

    </div>

</div>

</body>
</html>