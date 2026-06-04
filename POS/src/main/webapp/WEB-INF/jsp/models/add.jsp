<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Model | POS</title>

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

        .content {
            padding: 40px;
        }

        .page-card {
            max-width: 500px;
            margin: auto;
            background: #ffffff;
            border-radius: 18px;
            padding: 28px;
        }

        .primary-btn {
            width: 100%;
            background: #4f46e5;
            color: #fff;
            border-radius: 12px;
            padding: 10px;
        }
    </style>
</head>

<body>

<div class="topbar">
    <h5>Add Model</h5>

    <form action="/logout" method="post">
        <button class="logout-btn">Logout</button>
    </form>
</div>

<div class="content">

    <div class="page-card">

        <form:form action="/models/add" method="post" modelAttribute="modelsDto">

            <label>Name</label>
            <form:input path="identifier" cssClass="form-control"/>

            <button class="primary-btn">Add Model</button>

        </form:form>

    </div>

</div>

</body>
</html>