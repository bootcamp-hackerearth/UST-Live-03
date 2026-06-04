<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update User</title>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background-color: #ffffff;
        }

        .page-wrapper {
            display: flex;
            justify-content: center;
            margin-top: 40px;
        }

        .container {
            width: 360px;
            padding: 22px;
            background: #ffffff;
            border-radius: 14px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.25);
            position: relative;
        }

        .app-title {
            text-align: center;
            font-size: 14px;
            font-weight: 600;
            color: #14b8a6; /* light teal */
            margin-bottom: 4px;
        }

        .back-btn {
            position: absolute;
            top: 12px;
            left: 12px;
            padding: 5px 12px;
            background: #ffffff;
            border: 1px solid teal;
            color: teal;
            text-decoration: none;
            border-radius: 16px;
            font-size: 12px;
            font-weight: 600;
        }

        h2 {
            text-align: center;
            margin-bottom: 12px;
            font-size: 20px;
        }

        label {
            display: block;
            margin-top: 10px;
            font-size: 12px;
            font-weight: 600;
            color: #475569;
        }

        input, select {
            display: block;
            width: 100%;
            box-sizing: border-box;
            padding: 8px 10px;
            margin-top: 4px;
            border: 1px solid #d1d5db;
            border-radius: 18px;
            font-size: 13px;
        }

        select[multiple] {
            height: 100px;
            border-radius: 12px;
        }

        button {
            margin-top: 16px;
            width: 100%;
            padding: 8px 10px;
            background: teal;
            color: white;
            border-radius: 18px;
            border: none;
            font-weight: 600;
            font-size: 13px;
            cursor: pointer;
        }

        .message {
            text-align: center;
            color: teal;
            font-size: 12px;
            margin-bottom: 10px;
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="page-wrapper">
    <div class="container">

        <div class="app-title">POS Application</div>

        <a href="${pageContext.request.contextPath}/user/list" class="back-btn">Back</a>

        <h2>Update User</h2>

        <c:if test="${not empty message}">
            <div class="message">${message}</div>
        </c:if>
        <form:form action="${pageContext.request.contextPath}/user/update"
                   method="post"
                   modelAttribute="user">

            <form:hidden path="id"/>

            <label>Name</label>
            <form:input path="name" required="true"/>

            <label>Email</label>
            <form:input path="username" type="email" required="true"/>

            <label>Phone Number</label>
            <form:input path="phoneNo"
                        type="tel"
                        required="true"
                        pattern="[0-9]{10}"
                        maxlength="10"/>

            <label>Roles</label>
            <form:select path="roles" multiple="true">
                <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
            </form:select>

            <button type="submit">Update User</button>

        </form:form>

    </div>
</div>

</body>
</html>