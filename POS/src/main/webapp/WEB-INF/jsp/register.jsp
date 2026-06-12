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
            height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            overflow: hidden;
            background-color: #f6f7f9;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
        }

        .container {
            width: 360px;
            padding: 20px 24px;
            background: #ffffff;
            border-radius: 12px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.15);
            position: relative;
        }

        .back-btn {
            position: absolute;
            top: 12px;
            left: 12px;
            padding: 4px 10px;
            background-color: #e5e7eb;
            color: #374151;
            text-decoration: none;
            border-radius: 6px;
            font-size: 12px;
            font-weight: 600;
        }

        h2 {
            text-align: center;
            margin: 8px 0 14px;
            font-size: 20px;
            font-weight: 700;
            color: #020617;
        }

        label {
            display: block;
            margin-top: 10px;
            font-size: 11px;
            font-weight: 600;
            color: #475569;
        }

        input, select {
            width: 100%;
            padding: 10px;
            margin-top: 4px;

            border-radius: 22px;
            border: 1px solid #cbd5e1;

            font-size: 13px;
        }

        select[multiple] {
            height: 80px;
            border-radius: 10px;
        }

        button {
            margin-top: 16px;
            width: 100%;
            padding: 10px;
            background: teal;
            color: #ffffff;
            border: none;
            border-radius: 22px;
            font-weight: 600;
            font-size: 13px;
            cursor: pointer;
            box-shadow: 0 3px 8px rgba(0,0,0,0.2);
        }

        button:hover {
            background: #0f766e;
        }

        .error-message {
            text-align: center;
            margin-bottom: 10px;
            color: #dc2626;
            font-size: 12px;
            font-weight: 600;
        }

    </style>
</head>

<body>

<div class="container">

    <!-- Back -->
    <a href="${pageContext.request.contextPath}/login" class="back-btn">
        Back
    </a>

    <h2>User Registration</h2>

    <!-- Error -->
    <c:if test="${not empty message}">
        <div class="error-message">
            ${message}
        </div>
    </c:if>

    <!-- Form -->
    <form:form action="${pageContext.request.contextPath}/register"
               method="post"
               modelAttribute="userDto">

        <label>Name</label>
        <form:input path="name" required="true"/>

        <label>Email</label>
        <form:input path="username" type="email" required="true"
                    placeholder="example@mail.com"/>

        <label>Roles</label>
        <form:select path="roles" multiple="true" required="true">
            <form:options items="${roles}"
                          itemValue="identifier"
                          itemLabel="identifier"/>
        </form:select>

        <label>Phone Number</label>
        <form:input path="phoneNo"
                    type="tel"
                    required="true"
                    pattern="[0-9]{10}"
                    maxlength="10"
                    title="Phone number must be exactly 10 digits"
                    oninput="this.value=this.value.replace(/[^0-9]/g,'')"/>

        <label>Password</label>
        <form:password path="password"
                       required="true"
                       minlength="6"/>

        <button type="submit">Register</button>

    </form:form>

</div>

</body>
</html>