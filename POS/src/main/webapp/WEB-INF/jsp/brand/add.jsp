<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Brand</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #F6F7F9;
            padding: 30px;
        }

        h2 {
            text-align: center;
            color: #111827;
            margin-bottom: 20px;
        }

        .form-container {
            max-width: 420px;
            margin: auto;
            background: #ffffff;
            padding: 25px;
            border-radius: 12px;
            box-shadow: 0 6px 14px rgba(0,0,0,0.08);
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
            border: none;
            padding: 10px;
            border-radius: 6px;
            font-weight: 600;
        }

        .back-btn {
            flex: 1;
            background: #E5E7EB;
            color: #111827;
            padding: 10px;
            text-decoration: none;
            text-align: center;
            border-radius: 6px;
            font-weight: 600;
        }
    </style>
</head>

<body>

<h2>Add Brand</h2>

<div class="form-container">

    <form:form action="/brand/add" method="post" modelAttribute="brandDto">

        <label>Brand Name</label>
        <form:input path="identifier" required="true"/>

        <label>Description</label>
        <form:input path="description"/>

        <div class="btn-group">
            <button type="submit" class="save-btn">Save</button>
            <a href="/brand/list" class="back-btn">Back</a>
        </div>

    </form:form>

</div>

</body>
</html>