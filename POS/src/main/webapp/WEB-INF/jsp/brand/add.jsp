<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Brand</title>

    <style>
        body {
            margin: 0;
            font-family: Arial;
            background: #f4f7f6;
        }

        .back-btn {
            position: fixed;
            top: 20px;
            left: 20px;
            padding: 8px 14px;
            background: #6c757d;
            color: white;
            text-decoration: none;
            border-radius: 6px;
            font-size: 13px;
        }

        .card {
            width: 420px;
            margin: 100px auto;
            background: white;
            padding: 30px 35px;
            border-radius: 12px;
            border: 1px solid #ddd;
            box-shadow: 0 4px 10px rgba(0,0,0,0.08);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
        }

        label {
            display: block;
            margin-top: 15px;
            font-size: 13px;
            font-weight: bold;
        }

        input, select {
            width: 100%;
            margin-top: 6px;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 6px;
        }

        .btn-submit {
            margin-top: 25px;
            width: 100%;
            padding: 12px;
            background: #28a745;
            color: white;
            border: none;
            border-radius: 6px;
            cursor: pointer;
        }

        .error-msg {
            margin-bottom: 12px;
            padding: 10px;
            text-align: center;
            border-radius: 6px;
            background: #fee2e2;
            color: #b91c1c;
        }
    </style>
</head>

<body>

<a href="/brand/list" class="back-btn">← Back</a>

<div class="card">

    <h2>Add Brand</h2>

    <!-- ERROR -->
    <c:if test="${not empty message}">
        <div class="error-msg">${message}</div>
    </c:if>

    <form:form action="/brand/add" method="post" modelAttribute="brand">

        <!-- BRAND NAME -->
        <label>Brand Name</label>
        <form:input path="identifier" required="true"/>

        <!-- DESCRIPTION -->
        <label>Description</label>
        <form:input path="description"/>

        <!-- STATUS -->
        <label>Status</label>
        <form:select path="status">
            <form:option value="true">Active</form:option>
            <form:option value="false">Inactive</form:option>
        </form:select>

        <!-- SUBMIT -->
        <input type="submit" value="Save Brand" class="btn-submit"/>

    </form:form>

</div>

</body>
</html>