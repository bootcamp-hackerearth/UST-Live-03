<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Model</title>

    <style>
        body {
            font-family: "Segoe UI", Arial, sans-serif;
            background: #F6F7F9;
            padding: 30px;
        }

        h2 {
            text-align: center;
            color: #111827;
            margin-bottom: 20px;
        }

        .form-card {
            max-width: 420px;
            margin: auto;
            background: #fff;
            padding: 25px;
            border-radius: 16px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.06);
            border: 1px solid #E5E7EB;
        }

        label {
            font-weight: 600;
            display: block;
            margin-top: 15px;
        }

        input, select {
            width: 100%;
            padding: 10px;
            margin-top: 6px;
            border-radius: 6px;
            border: 1px solid #D1D5DB;
        }

        .btn-group {
            display: flex;
            gap: 10px;
            margin-top: 25px;
        }

        .save-btn {
            flex: 1;
            background: #2B2B2B;
            color: white;
            padding: 10px;
            border: none;
            border-radius: 8px;
            font-weight: 600;
            cursor: pointer;
        }

        .save-btn:hover {
            background: #111111;
        }

        .back-btn {
            flex: 1;
            background: #E5E7EB;
            color: #111827;
            padding: 10px;
            border-radius: 8px;
            font-weight: 600;
            text-align: center;
            text-decoration: none;
        }

        .back-btn:hover {
            background: #D1D5DB;
        }
    </style>
</head>

<body>

<h2>Add Model</h2>

<div class="form-card">

    <form:form action="/models/add" method="post" modelAttribute="modelsDto">

        <label>Model Name</label>
        <form:input path="identifier" required="true"/>

        <div class="btn-group">
            <button type="submit" class="save-btn">Save</button>
            <a href="/models/list" class="back-btn">Back</a>
        </div>

    </form:form>

</div>

</body>
</html>