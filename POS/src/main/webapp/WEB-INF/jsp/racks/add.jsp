<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Rack | POS</title>

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

        .dashboard-btn, .logout-btn {
            background: rgba(255,255,255,0.15);
            border: none;
            color: #fff;
            padding: 8px 14px;
            border-radius: 10px;
            font-size: 13px;
        }

        .content {
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

        label {
            font-size: 12px;
            margin-bottom: 4px;
        }

        input, select {
            width: 100%;
            padding: 10px;
            border-radius: 10px;
            border: 1px solid #d1d5db;
            font-size: 13px;
            margin-bottom: 12px;
        }

        .checkbox-box {
            border: 1px solid #d1d5db;
            border-radius: 10px;
            padding: 10px;
            margin-bottom: 12px;
        }

        .btn-primary {
            width: 100%;
            background: #4f46e5;
            border: none;
            border-radius: 10px;
            padding: 11px;
        }

        .back {
            display: block;
            text-align: center;
            margin-top: 12px;
            font-size: 12px;
            color: #4f46e5;
        }
    </style>
</head>

<body>

<div class="topbar">
    <div class="topbar-left">
        <h5>Add Rack</h5>
        <button class="dashboard-btn" onclick="window.location.href='/'">Dashboard</button>
    </div>

    <form action="/logout" method="post">
        <button class="logout-btn">Logout</button>
    </form>
</div>

<div class="content">

    <div class="card">

        <h2>Add Rack</h2>

        <form:form action="/racks/add" method="post" modelAttribute="racks">

            <label>Rack Name</label>
            <form:input path="identifier"/>

            <label>Select Shelves</label>
            <div class="checkbox-box">
                <c:forEach var="s" items="${shelves}">
                    <div>
                        <form:checkbox path="shelves" value="${s.identifier}"/>
                        ${s.identifier}
                    </div>
                </c:forEach>
            </div>

            <button type="submit" class="btn btn-primary">
                Add Rack
            </button>

        </form:form>

        <a href="/racks/list" class="back">← Back to List</a>

    </div>

</div>

</body>
</html>