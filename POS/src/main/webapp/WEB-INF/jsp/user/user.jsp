<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update User | POS</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <style>
        body {
            margin: 0;
            font-family: 'Inter', 'Segoe UI', sans-serif;
            background: linear-gradient(135deg, #f6f8fb, #eef2f7);
        }

        .topbar {
            height: 70px;
            background: linear-gradient(135deg, #4f46e5, #6366f1);
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 30px;
            color: #fff;
            box-shadow: 0 4px 20px rgba(0,0,0,0.08);
        }

        .topbar-left {
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .dashboard-btn {
            background: rgba(255,255,255,0.15);
            border: none;
            color: #fff;
            padding: 8px 14px;
            border-radius: 10px;
            font-size: 13px;
        }

        .dashboard-btn:hover {
            background: rgba(255,255,255,0.25);
        }

        .logout-btn {
            background: rgba(255,255,255,0.15);
            border: none;
            color: #fff;
            padding: 8px 16px;
            border-radius: 10px;
            font-size: 13px;
        }

        .logout-btn:hover {
            background: rgba(255,255,255,0.25);
        }

        .container-box {
            display: flex;
            justify-content: center;
            padding: 40px;
        }

        .card {
            width: 420px;
            background: #ffffff;
            border-radius: 18px;
            padding: 24px;
            box-shadow: 0 25px 60px rgba(0,0,0,0.08);
        }

        h2 {
            text-align: center;
            margin-bottom: 16px;
        }

        .message {
            text-align: center;
            color: green;
            font-size: 13px;
            margin-bottom: 10px;
        }

        label {
            font-size: 12px;
            color: #374151;
            margin-bottom: 4px;
            display: block;
        }

        input, select {
            width: 100%;
            padding: 10px;
            border-radius: 10px;
            border: 1px solid #d1d5db;
            font-size: 13px;
            margin-bottom: 12px;
        }

        input:focus, select:focus {
            outline: none;
            border-color: #6366f1;
            box-shadow: 0 0 0 2px rgba(99,102,241,0.15);
        }

        .badge {
            display: inline-block;
            background: #eef2ff;
            color: #4f46e5;
            padding: 4px 8px;
            border-radius: 6px;
            font-size: 11px;
            margin: 2px;
        }

        .btn-primary {
            width: 100%;
            background: #4f46e5;
            border: none;
            border-radius: 10px;
            padding: 11px;
        }

        .btn-primary:hover {
            background: #4338ca;
        }

        .back {
            display: block;
            text-align: center;
            margin-top: 12px;
            font-size: 12px;
            color: #4f46e5;
            text-decoration: none;
        }

        .back:hover {
            text-decoration: underline;
        }
    </style>
</head>

<body>

<div class="topbar">
    <div class="topbar-left">
        <h5>Edit User</h5>

        <button class="dashboard-btn"
                onclick="window.location.href='/'">
            Dashboard
        </button>
    </div>

    <form action="/logout" method="post">
        <button class="logout-btn">Logout</button>
    </form>
</div>

<div class="container-box">

    <div class="card">

        <h2>Edit User</h2>

        <div class="message">${message}</div>

        <form:form action="/user/update" method="post" modelAttribute="userDto">

            <form:input type="hidden" path="id"/>

            <label>Name</label>
            <form:input path="name"/>

            <label>Email</label>
            <form:input path="username" type="email"/>

            <label>Phone</label>
            <form:input path="phoneNo"/>

            <label>Current Roles</label>
            <div>
                <c:forEach var="r" items="${user.roles}">
                    <span class="badge">${r}</span>
                </c:forEach>
            </div>

            <label>Select Roles</label>
            <form:select path="roles" multiple="true">
                <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
            </form:select>

            <button type="submit" class="btn btn-primary">Update User</button>

        </form:form>

        <a href="/user/list" class="back">← Back to User List</a>

    </div>

</div>

</body>
</html>