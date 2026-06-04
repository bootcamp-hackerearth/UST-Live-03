<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Role | POS</title>

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
            width: 380px;
            background: #ffffff;
            border-radius: 18px;
            padding: 24px;
            box-shadow: 0 25px 60px rgba(0,0,0,0.08);
        }

        h2 {
            text-align: center;
            margin-bottom: 16px;
        }

        .success {
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

        input {
            width: 100%;
            padding: 10px;
            border-radius: 10px;
            border: 1px solid #d1d5db;
            font-size: 13px;
            margin-bottom: 12px;
        }

        input:focus {
            outline: none;
            border-color: #6366f1;
            box-shadow: 0 0 0 2px rgba(99,102,241,0.15);
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

        .error {
            text-align: center;
            color: red;
            font-size: 12px;
            margin-top: 8px;
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
        <h5>Add Role</h5>

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

        <h2>Create Role</h2>

        <c:if test="${not empty role}">
            <div class="success">
                ${role}
            </div>
        </c:if>

        <form:form method="post" action="/role/add" modelAttribute="roleDto">

            <label>Role Name</label>
            <form:input path="identifier" placeholder="Enter role name"/>

            <button type="submit" class="btn btn-primary">Add Role</button>

            <div class="error">${message}</div>

        </form:form>

        <a href="/" class="back">← Back to Dashboard</a>

    </div>

</div>

</body>
</html>