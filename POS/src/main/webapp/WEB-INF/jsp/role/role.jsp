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
            font-family: 'Segoe UI', sans-serif;
            background: #eef2f7;
        }

        .header {
            background: #1e88e5;
            padding: 15px 25px;
            color: white;
            font-size: 20px;
            font-weight: bold;
        }

        .container {
            display: flex;
            justify-content: center;
            padding: 30px;
        }

        .card {
            width: 340px;
            background: white;
            padding: 22px;
            border-radius: 10px;
            box-shadow: 0 5px 15px rgba(0,0,0,0.08);
        }

        h2 {
            text-align: center;
            margin-bottom: 18px;
            color: #333;
            font-size: 18px;
        }

        .error {
            text-align: center;
            color: red;
            font-size: 12px;
            margin-bottom: 10px;
        }

        .form-group {
            margin-bottom: 14px;
        }

        label {
            font-size: 12px;
            color: #555;
            font-weight: 500;
        }

        input {
            width: 100%;
            padding: 10px;
            margin-top: 5px;
            border-radius: 6px;
            border: 1px solid #ddd;
            font-size: 13px;
        }

        input:focus {
            border-color: #1e88e5;
            outline: none;
            box-shadow: 0 0 0 2px rgba(30,136,229,0.2);
        }

        .btn {
            width: 100%;
            padding: 11px;
            margin-top: 10px;
            border-radius: 6px;
            border: none;
            background: #1e88e5;
            color: white;
            font-weight: 600;
            cursor: pointer;
        }

        .btn:hover {
            opacity: 0.9;
        }

        .back {
            display: block;
            text-align: center;
            margin-top: 10px;
            font-size: 12px;
            color: #1e88e5;
            text-decoration: none;
        }

        .back:hover {
            text-decoration: underline;
        }
    </style>
</head>

<body>

<div class="header">
    POS - Edit Role
</div>

<div class="container">

    <div class="card">

        <h2>Update Role</h2>

        <c:if test="${not empty message}">
            <div class="error">
                ${message}
            </div>
        </c:if>

        <c:if test="${empty roleDto}">
            <div class="error">
                Role not found
            </div>
        </c:if>

        <c:if test="${not empty roleDto}">
            <form:form action="/role/update"
                       method="post"
                       modelAttribute="roleDto">

                <form:hidden path="id"/>

                <div class="form-group">
                    <label>Role Name</label>
                    <form:input path="identifier"
                                placeholder="Enter role"
                                required="true"/>
                </div>

                <button type="submit" class="btn">
                    Update Role
                </button>

            </form:form>

            <a href="/role/list" class="back">
                ← Back to Role List
            </a>
        </c:if>

    </div>

</div>

</body>
</html>