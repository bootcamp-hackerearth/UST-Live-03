<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Unit | POS</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <style>
        body {
            margin: 0;
            font-family: 'Inter', 'Segoe UI';
            background: linear-gradient(135deg, #f6f8fb, #eef2f7);
        }

        .topbar {
            height: 70px;
            background: linear-gradient(135deg, #4f46e5, #6366f1);
            display: flex;
            align-items: center;
            justify-content: space-between;
            padding: 0 30px;
            color: #ffffff;
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
            padding: 25px;
            box-shadow: 0 25px 60px rgba(0,0,0,0.08);
        }

        input {
            width: 100%;
            padding: 10px;
            border-radius: 10px;
            border: 1px solid #d1d5db;
            margin-bottom: 12px;
        }

        input:focus {
            outline: none;
            border-color: #6366f1;
            box-shadow: 0 0 0 2px rgba(99,102,241,0.15);
        }

        .btn-primary {
            width: 100%;
            border-radius: 10px;
        }

        .back {
            display: block;
            text-align: center;
            margin-top: 10px;
            font-size: 12px;
            color: #4f46e5;
            text-decoration: none;
        }
    </style>
</head>

<body>

<div class="topbar">
    <h5>Add Unit</h5>

    <button class="btn btn-light btn-sm"
            onclick="window.location.href='/'">
        Dashboard
    </button>
</div>

<div class="content">

    <div class="card">

        <h4 class="text-center mb-3">Create Unit</h4>

        <form:form action="/unit/add" method="post" modelAttribute="unitDto">

            <label>Unit Name</label>
            <form:input path="identifier" required="true"/>

            <button type="submit" class="btn btn-primary">
                Add Unit
            </button>

        </form:form>

        <a href="/unit/list" class="back">
            ← Back to Unit List
        </a>

    </div>

</div>

</body>
</html>