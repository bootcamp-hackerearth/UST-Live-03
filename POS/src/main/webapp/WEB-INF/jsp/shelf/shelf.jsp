<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Shelf | POS</title>

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
        }

        .dashboard-btn,
        .logout-btn {
            background: rgba(255,255,255,0.15);
            border: none;
            color: #fff;
            padding: 8px 14px;
            border-radius: 10px;
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
        }

        input, select {
            width: 100%;
            padding: 10px;
            border-radius: 10px;
            margin-bottom: 12px;
        }

        .btn-primary {
            width: 100%;
            background: #4f46e5;
        }

        .back {
            text-align: center;
            display: block;
            margin-top: 12px;
        }
    </style>
</head>

<body>

<div class="topbar">
    <h5>Edit Shelf</h5>

    <div>
        <button class="dashboard-btn" onclick="window.location.href='/'">Dashboard</button>

        <form action="/logout" method="post" style="display:inline;">
            <button class="logout-btn">Logout</button>
        </form>
    </div>
</div>

<div class="container-box">
    <div class="card">

        <h2>Edit Shelf</h2>

        <form:form action="/shelf/update" method="post" modelAttribute="shelfs">

            <form:hidden path="id"/>

            <label>Shelf Name</label>
            <form:input path="identifier" readonly="true"/>

            <button class="btn btn-primary">Update Shelf</button>

        </form:form>

        <a href="/shelf/list" class="back">← Back to Shelf List</a>

    </div>
</div>

</body>
</html>