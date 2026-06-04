<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>User Registration</title>
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

        input, select {
            width: 100%;
            margin-top: 6px;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 6px;
            font-size: 13px;
            box-sizing: border-box;
        }

        select[multiple] {
            height: 110px;
        }

        small {
            font-size: 11px;
            color: #999;
            margin-top: 3px;
            display: block;
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

<a href="${pageContext.request.contextPath}/user/list" class="back-btn">← Back</a>

<div class="card">

    <h2>User Registration</h2>

    <c:if test="${not empty message}">
        <div class="error-msg">${message}</div>
    </c:if>

    <form:form action="register" method="post" modelAttribute="userDto">

        <label>Name</label>
        <form:input path="name" required="true"/>

        <label>Email</label>
        <form:input path="username"
                    type="email"
                    required="true"
                    pattern="^[a-zA-Z0-9._%+-]+@gmail\.com$"
                    title="Enter a valid Gmail (example@gmail.com)"/>

        <label>Mobile Number</label>
        <form:input path="phoneNo"
                    type="tel"
                    required="true"
                    pattern="[0-9]{10}"
                    maxlength="10"
                    title="Enter a valid 10-digit mobile number"/>

        <label>Password</label>
        <form:password path="password"
                       required="true"
                       pattern=".{6,}"
                       title="Password must be at least 6 characters"/>

        <label>Roles</label>
        <form:select path="roles" multiple="true" required="true">
            <form:options items="${roles}"
                          itemValue="identifier"
                          itemLabel="identifier"/>
        </form:select>
        <small>Hold Ctrl (Windows) or Cmd (Mac) to select multiple</small>

        <input type="submit" value="Register" class="btn-submit"/>

    </form:form>

</div>

</body>
</html>