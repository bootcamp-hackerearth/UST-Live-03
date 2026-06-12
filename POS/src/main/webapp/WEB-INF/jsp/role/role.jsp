<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Role</title>
    <style>
        body {
            margin: 0;
            font-family: Arial, sans-serif;
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

        .back-btn:hover {
            background: #5a6268;
        }

        .card {
            width: 460px;
            margin: 80px auto;
            background: white;
            padding: 30px 35px;
            border-radius: 10px;
            border: 1px solid #ddd;
            box-shadow: 0 3px 12px rgba(0,0,0,0.1);
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

        input {
            width: 100%;
            margin-top: 6px;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 6px;
            font-size: 13px;
            box-sizing: border-box;
        }

        input[readonly] {
            background-color: #f8f9fa;
            color: #666;
            cursor: not-allowed;
        }

        .error-msg {
            margin-bottom: 12px;
            padding: 10px;
            text-align: center;
            border-radius: 6px;
            background: #fee2e2;
            color: #b91c1c;
        }

        .btn-submit {
            margin-top: 25px;
            width: 100%;
            padding: 12px;
            background: #007bff;
            color: white;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            font-size: 14px;
            font-weight: bold;
        }

        .btn-submit:hover {
            background: #0056b3;
        }
    </style>
</head>

<body>

<a href="/role/list" class="back-btn">← Back</a>

<div class="card">

    <h2>Edit Role</h2>

    <!-- ERROR -->
    <c:if test="${not empty message}">
        <div class="error-msg">${message}</div>
    </c:if>

    <form:form action="/role/update" method="post" modelAttribute="roleDto">

        <form:hidden path="id"/>

        <!-- ROLE NAME -->
        <label>Role Name</label>
        <form:input path="identifier" readonly="true"/>

        <!-- DESCRIPTION -->
        <label>Description</label>
        <form:input path="description" required="true"/>

        <!-- SUBMIT -->
        <input type="submit" value="Update Role" class="btn-submit"/>

    </form:form>

</div>

</body>
</html>