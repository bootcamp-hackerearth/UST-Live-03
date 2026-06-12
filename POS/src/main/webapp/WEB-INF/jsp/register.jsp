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
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background-color: #f6f7f9;
            color: #1f2937;
        }

        .container {
            width: 380px;
            padding: 26px 28px;
            background: #ffffff;
            border-radius: 12px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
            position: relative;
        }

        .back-btn {
            position: absolute;
            top: 18px;
            left: 18px;
            padding: 6px 14px;
            background-color: #eef0f3;
            color: #374151;
            text-decoration: none;
            border-radius: 6px;
            font-size: 13px;
            font-weight: 600;
            border: 1px solid #d1d5db;
        }

        h2 {
            text-align: center;
            margin: 14px 0 18px;
            font-size: 22px;
            font-weight: 600;
        }

        label {
            display: block;
            margin-top: 14px;
            font-size: 12px;
            font-weight: 600;
            color: #475569;
        }

        input, select {
            width: 100%;
            padding: 9px 11px;
            margin-top: 6px;
            border-radius: 6px;
            border: 1px solid #d1d5db;
            font-size: 14px;
            color: #1f2937;
        }

        select[multiple] {
            height: 120px;
        }

        button {
            margin-top: 22px;
            width: 100%;
            padding: 10px;
            background-color: #2563eb;
            color: #ffffff;
            border: none;
            border-radius: 20px;
            font-weight: 600;
            font-size: 14px;
            cursor: pointer;
        }

        .error-message {
            text-align: center;
            margin-bottom: 14px;
            color: #dc2626;
            font-size: 13px;
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="container">

    <!--  Back Button -->
    <a href="${pageContext.request.contextPath}/login" class="back-btn">
        Back
    </a>

    <h2>User Registration</h2>

    <!--  ERROR MESSAGE -->
    <c:if test="${not empty message}">
        <div class="error-message">
            ${message}
        </div>
    </c:if>

    <!--  REGISTRATION FORM -->
    <form:form action="${pageContext.request.contextPath}/register"
               method="post"
               modelAttribute="userDto">

        <label>Name</label>
        <form:input path="name" required="true"/>


<label>Email</label>
<form:input
        path="username"
        type="email"
        required="true"
        placeholder="example@mail.com"
/>


        <label>Roles</label>
        <form:select path="roles" multiple="true" required="true">
            <form:options items="${roles}"
                          itemValue="identifier"
                          itemLabel="identifier"/>
        </form:select>

        <label>Phone Number</label>
        <form:input
                path="phoneNo"
                type="tel"
                required="true"
                pattern="[0-9]{10}"
                maxlength="10"
                title="Phone number must be exactly 10 digits"
                oninput="this.value=this.value.replace(/[^0-9]/g,'')"/>

        <label>Password</label>
        <form:password
                path="password"
                required="true"
                minlength="6"/>

        <button type="submit">Register</button>

    </form:form>
</div>

</body>
</html>