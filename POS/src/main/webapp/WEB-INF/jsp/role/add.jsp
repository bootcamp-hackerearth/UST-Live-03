<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Role</title>
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

        .error-msg {
            margin-bottom: 12px;
            padding: 10px;
            text-align: center;
            border-radius: 6px;
            background: #fee2e2;
            color: #b91c1c;
        }

        .success-msg {
            margin-bottom: 12px;
            padding: 10px;
            text-align: center;
            border-radius: 6px;
            background: #d1fae5;
            color: #065f46;
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
            font-size: 14px;
            font-weight: bold;
        }

        .btn-submit:hover {
            background: #218838;
        }
    </style>
</head>

<body>

<a href="/role/list" class="back-btn">← Back</a>

<div class="card">

    <h2>Add Role</h2>

    <c:if test="${not empty role}">
        <div class="success-msg">${role}</div>
    </c:if>

    <c:if test="${not empty message}">
        <div class="error-msg">${message}</div>
    </c:if>

    <form:form method="post" action="/role/add" modelAttribute="roleDto">

        <label>Role Name</label>
        <form:input path="identifier" required="true"/>

        <label>Description</label>
        <form:input path="description" required="true"/>

        <input type="submit" value="Add Role" class="btn-submit"/>

    </form:form>

</div>

</body>
</html>