<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Role</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            min-height: 100vh;
            background: #ffffff;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card-container {
            position: relative;
            width: 430px;
            background: rgba(255, 255, 255, 0.95);
            padding: 35px 40px;
            border-radius: 16px;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
        }

        .back-icon {
            position: absolute;
            top: 16px;
            left: 16px;
            width: 36px;
            height: 36px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
            color: #4b6cb7;
            text-decoration: none;
            font-weight: 600;
            background: rgba(75, 108, 183, 0.08);
            border-radius: 50%;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.12);
            transition: all 0.25s ease;
        }

        .back-icon:hover {
            background: #4b6cb7;
            color: #ffffff;
            transform: translateX(-4px) scale(1.05);
            box-shadow: 0 8px 18px rgba(75, 108, 183, 0.35);
        }

        h2 {
            text-align: center;
            margin-bottom: 25px;
            color: #4b6cb7;
            font-weight: 600;
        }

        .form-group {
            margin-bottom: 18px;
        }

        label {
            font-size: 13px;
            font-weight: 500;
            color: #333;
            margin-bottom: 6px;
            display: block;
        }

        input {
            width: 100%;
            padding: 11px 14px;
            border-radius: 8px;
            border: 1px solid #ccc;
            font-size: 14px;
        }

        input[readonly] {
            background: #f5f5f5;
        }

        .btn-submit {
            margin-top: 10px;
            width: 100%;
            padding: 13px;
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
        }

        .btn-cancel {
            margin-top: 10px;
            width: 100%;
            padding: 11px;
            background: #f1f1f1;
            color: #333;
            border: none;
            border-radius: 10px;
            font-size: 14px;
            text-align: center;
            text-decoration: none;
            display: inline-block;
        }

        .home-link {
            position: absolute;
            top: 16px;
            right: 16px;
            font-size: 14px;
            font-weight: 600;
            color: #4b6cb7;
            text-decoration: none;
            padding: 8px 14px;
            border-radius: 8px;
            background: rgba(75, 108, 183, 0.08);
            transition: all 0.25s ease;
        }

        .home-link:hover {
            background: #4b6cb7;
            color: #ffffff;
            box-shadow: 0 8px 18px rgba(75, 108, 183, 0.35);
            transform: translateY(-2px);
        }

    </style>
</head>

<body>

<div class="card-container">

    <a href="/role/list" class="back-icon">←</a>
    <a href="/" class="home-link">Home</a>

    <h2>Edit Role</h2>

    <c:if test="${empty roleDto}">
        <div class="alert alert-danger">
            Role not found
        </div>
    </c:if>

    <c:if test="${not empty roleDto}">
        <form:form action="/role/update"
                   method="post"
                   modelAttribute="roleDto">

            <form:hidden path="id" value="${roleDto.id}"/>

            <div class="form-group">
                <label>Role Name</label>
                <form:input path="identifier" readonly="true"/>
            </div>

            <div class="form-group">
                <label>Role Description</label>
                <form:input path="description" placeholder="Enter role description" required="true"/>
            </div>

            <input type="submit" value="Update Role" class="btn-submit"/>

            <a href="/role/list" class="btn-cancel">Cancel</a>

        </form:form>
    </c:if>

</div>

</body>
</html>