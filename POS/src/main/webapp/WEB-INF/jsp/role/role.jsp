<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Role</title>

    <style>
        html, body {
            margin: 0;
            padding: 0;
            height: 100vh;
            width: 100vw;
            overflow: hidden;
        }

        body {
            font-family: "Segoe UI", Arial, sans-serif;
            background: linear-gradient(135deg, #ede9fe, #ddd6fe);
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .container {
            width: 420px;
            background: #ffffff;
            padding: 25px 30px;
            border-radius: 12px;
            box-shadow: 0 20px 40px rgba(76, 29, 149, 0.18);
            box-sizing: border-box;
        }

        h2 {
            text-align: center;
            margin-top: 0;
            margin-bottom: 20px;
            color: #6d28d9;
            font-weight: 600;
        }

        label {
            margin-top: 14px;
            display: block;
            font-weight: 600;
            font-size: 13px;
            color: #4c1d95;
        }

        input, select {
            width: 100%;
            margin-top: 5px;
            padding: 9px;
            border: 1px solid #c4b5fd;
            border-radius: 6px;
            font-size: 13px;
            box-sizing: border-box;
        }

        input:focus, select:focus {
            outline: none;
            border-color: #a78bfa;
            box-shadow: 0 0 0 0.15rem rgba(167, 139, 250, 0.35);
        }

        button {
            margin-top: 22px;
            width: 100%;
            padding: 11px;
            background: #7c3aed; /* primary purple */
            color: #ffffff;
            border: none;
            font-weight: 600;
            border-radius: 6px;
            cursor: pointer;
            font-size: 13px;
        }

        button:hover {
            background: #6d28d9;
        }

        a {
            display: block;
            text-align: center;
            margin-top: 16px;
            color: #6d28d9;
            font-weight: 600;
            text-decoration: none;
            font-size: 13px;
        }

        a:hover {
            text-decoration: underline;
            color: #5b21b6;
        }

        .alert {
            background: #fee2e2;
            color: #b91c1c;
            padding: 10px;
            border-radius: 6px;
            text-align: center;
            font-size: 13px;
            margin-bottom: 14px;
        }
    </style>
</head>

<body>

<div class="container">

    <h2>Edit Role</h2>

    <c:if test="${empty role}">
        <div class="alert">
            Role not found
        </div>
    </c:if>

    <c:if test="${not empty role}">

        <form:form action="/role/update" method="post" modelAttribute="role">

            <form:hidden path="id"/>

            <label>Role Name</label>
            <form:input path="identifier" placeholder="Enter role" required="true" readonly="true"/>

            <label>Description</label>
            <form:input path="description" placeholder="Enter role" required="true"/>

            <button type="submit">Update</button>

        </form:form>

    </c:if>

    <a href="/role/list">
        ← Back to Role List
    </a>

</div>

</body>
</html>