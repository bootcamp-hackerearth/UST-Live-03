<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update User</title>

    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
            font-family: "Segoe UI", sans-serif;
        }

        body {
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            background: radial-gradient(circle at 20% 30%, rgba(255,122,0,0.15), transparent 40%),
                        radial-gradient(circle at 80% 70%, rgba(255,72,0,0.15), transparent 40%),
                        linear-gradient(135deg, #1f1f1f, #3a2e2a);
        }

        .form-container {
            width: 100%;
            max-width: 420px;
            background: rgba(255,255,255,0.12);
            backdrop-filter: blur(20px);
            -webkit-backdrop-filter: blur(20px);
            padding: 30px;
            border-radius: 16px;
            box-shadow: 0 15px 40px rgba(0,0,0,0.3);
            border: 1px solid rgba(255,255,255,0.15);
        }

        h2 {
            font-size: 28px;
            margin-bottom: 20px;
            color: #fff;
            text-align: center;
        }

        .form-group {
            margin-bottom: 14px;
        }

        label {
            font-size: 13px;
            color: #ddd;
            margin-bottom: 6px;
            display: block;
        }

        input, select {
            width: 100%;
            padding: 12px;
            border-radius: 20px;
            border: 1px solid rgba(255,255,255,0.2);
            font-size: 14px;
            background: rgba(255,255,255,0.1);
            color: #fff;
        }

        input::placeholder {
            color: #ccc;
        }

        input:focus, select:focus {
            outline: none;
            border-color: #ff7a00;
            box-shadow: 0 0 6px rgba(255,122,0,0.3);
        }

        select[multiple] {
            height: 110px;
            border-radius: 12px;
        }

        .current-roles {
            margin-bottom: 8px;
        }

        .badge {
            display: inline-block;
            padding: 5px 10px;
            border-radius: 20px;
            background: rgba(255,255,255,0.2);
            color: #ff7a00;
            font-size: 12px;
            margin-right: 5px;
        }

        .btn-update {
            width: 100%;
            padding: 12px;
            border-radius: 20px;
            border: none;
            background: linear-gradient(90deg, #ff4800, #ff7a00);
            color: white;
            font-size: 14px;
            cursor: pointer;
            margin-top: 10px;
        }

        .btn-back {
            width: 100%;
            padding: 10px;
            border-radius: 20px;
            border: none;
            background: rgba(255,255,255,0.2);
            color: #fff;
            font-size: 14px;
            cursor: pointer;
            margin-top: 10px;
        }

        .btn-update:hover {
            opacity: 0.9;
        }

        .btn-back:hover {
            background: rgba(255,255,255,0.3);
        }

        .message-box {
            margin-top: 10px;
            font-size: 13px;
            color: #ffb3b3;
            text-align: center;
        }
    </style>
</head>

<body>

<div class="form-container">
    <h2>Edit User</h2>

    <form:form action="/user/update" method="post" modelAttribute="user">

        <form:input type="hidden" path="id"/>

        <div class="form-group">
            <label>Name</label>
            <form:input path="name" required="true"/>
        </div>

        <div class="form-group">
            <label>Email</label>
            <form:input path="username" type="email" required="true"/>
        </div>

        <div class="form-group">
            <label>Phone Number</label>
            <form:input path="phoneNo" required="true" type="tel" pattern="[0-9]{10}"/>
        </div>

        <div class="form-group">
            <label>Roles</label>

            <div class="current-roles">
                <c:forEach var="r" items="${user.roles}">
                    <span class="badge">${r}</span>
                </c:forEach>
            </div>

            <form:select path="roles" multiple="true">
                <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
            </form:select>
        </div>

        <button type="submit" class="btn-update">Update User</button>

        <button type="button" class="btn-back" onclick="window.location.href='/user/list'">
            Back to User List
        </button>

        <c:if test="${not empty message}">
            <div class="message-box">${message}</div>
        </c:if>

    </form:form>
</div>

</body>
</html>