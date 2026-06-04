<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Role</title>

    <style>
        body {
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            font-family: "Segoe UI", sans-serif;
            background: radial-gradient(circle at 20% 30%, rgba(255,122,0,0.15), transparent 40%),
                        radial-gradient(circle at 80% 70%, rgba(255,72,0,0.15), transparent 40%),
                        linear-gradient(135deg, #1f1f1f, #3a2e2a);
        }

        .card {
            width: 420px;
            background: rgba(255,255,255,0.12);
            backdrop-filter: blur(20px);
            border-radius: 16px;
            padding: 25px;
            box-shadow: 0 15px 40px rgba(0,0,0,0.3);
            border: 1px solid rgba(255,255,255,0.15);
        }

        h4 {
            text-align: center;
            margin-bottom: 25px;
            color: #fff;
            font-weight: 600;
        }

        .form-group {
            margin-bottom: 16px;
        }

        label {
            font-size: 13px;
            color: #ddd;
            margin-bottom: 6px;
            display: block;
        }

        input {
            width: 100%;
            padding: 12px;
            border-radius: 20px;
            border: 1px solid rgba(255,255,255,0.2);
            background: rgba(255,255,255,0.1);
            color: #fff;
            font-size: 14px;
        }

        input::placeholder {
            color: #ccc;
        }

        input:focus {
            outline: none;
            border-color: #ff7a00;
            box-shadow: 0 0 6px rgba(255,122,0,0.3);
        }

        .button-group {
            display: flex;
            justify-content: space-between;
            margin-top: 20px;
            gap: 10px;
        }

        .btn-update {
            flex: 1;
            padding: 12px;
            border-radius: 20px;
            border: none;
            background: linear-gradient(90deg, #ff4800, #ff7a00);
            color: white;
            cursor: pointer;
        }

        .btn-cancel {
            flex: 1;
            padding: 12px;
            border-radius: 20px;
            border: none;
            background: rgba(255,255,255,0.2);
            color: #fff;
            text-decoration: none;
            text-align: center;
        }

        .btn-cancel:hover {
            background: rgba(255,255,255,0.3);
        }

        .message-box {
            margin-top: 12px;
            font-size: 13px;
            color: #ffb3b3;
            text-align: center;
        }
    </style>
</head>

<body>

<div class="card">
    <h4>Edit Role</h4>

    <c:if test="${empty roleDto}">
        <div style="text-align:center; color:#ffb3b3; margin-bottom:10px;">
            Role not found
        </div>
    </c:if>

    <c:if test="${not empty roleDto}">
        <form:form action="/role/update" method="post" modelAttribute="roleDto">

            <form:hidden path="id"/>

            <div class="form-group">
                <label>Role Name</label>
                <form:input path="identifier" readonly="true"/>
            </div>

            <div class="form-group">
                <label>Role Description</label>
                <form:input path="description" placeholder="Enter role description"/>
            </div>

            <div class="button-group">
                <a href="/role/list" class="btn-cancel">Cancel</a>
                <button type="submit" class="btn-update">Update</button>
            </div>

            <c:if test="${not empty message}">
                <div class="message-box">${message}</div>
            </c:if>

        </form:form>
    </c:if>
</div>

</body>
</html>