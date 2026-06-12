<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Registration</title>

    <style>
        * { box-sizing: border-box; }

        body {
            margin: 0;
            font-family: -apple-system, BlinkMacSystemFont, "Segoe UI",
                         Roboto, "Helvetica Neue", Arial, sans-serif;
            min-height: 100vh;
            background: linear-gradient(135deg, #f3f4f6, #e5e7eb, #f9fafb);
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .register-card {
            width: 380px;
            background: #ffffff;
            padding: 45px 40px;
            border-radius: 16px;
            border: 1px solid #e5e7eb;
            box-shadow: 0 20px 40px rgba(0,0,0,0.10);
            position: relative;
        }

        .register-card::before {
            content: "";
            position: absolute;
            top: 0;
            left: 0;
            height: 6px;
            width: 100%;
            background: linear-gradient(90deg, #4f46e5, #2563eb);
            border-radius: 16px 16px 0 0;
        }

        h2 {
            text-align: center;
            margin-bottom: 28px;
            font-size: 26px;
            color: #111827;
            font-weight: 700;
        }

        .form-group { margin-bottom: 18px; }

        label {
            font-size: 13px;
            font-weight: 600;
            color: #4b5563;
            margin-bottom: 6px;
            display: block;
        }

        input, select {
            width: 100%;
            padding: 13px;
            border-radius: 10px;
            border: 1px solid #d1d5db;
            font-size: 14px;
            background: #f9fafb;
        }

        input:focus, select:focus {
            outline: none;
            border-color: #6366f1;
            background: #ffffff;
            box-shadow: 0 0 0 4px rgba(99,102,241,0.15);
        }

        select[multiple] { height: 110px; }

        .btn-submit {
            width: 100%;
            padding: 13px;
            background: linear-gradient(135deg, #4f46e5, #2563eb);
            color: white;
            border: none;
            border-radius: 10px;
            font-size: 15px;
            font-weight: 600;
            cursor: pointer;
        }

        .btn-submit:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(79,70,229,0.25);
        }

        .error-box {
            margin-bottom: 15px;
            padding: 10px;
            border-radius: 10px;
            font-size: 13px;
            text-align: center;
            background: #fff1f2;
            color: #be123c;
            border: 1px solid #fecdd3;
        }

        .field-error {
            font-size: 11px;
            color: #dc2626;
        }
    </style>
</head>

<body>

<div class="register-card">

    <h2>User Registration</h2>

    <c:if test="${not empty message}">
        <div class="error-box">${message}</div>
    </c:if>

<form:form action="${pageContext.request.contextPath}/register"
           method="post"
           modelAttribute="userDto">

        <div class="form-group">
            <label>Name *</label>
            <form:input path="name" required="true"/>
            <form:errors path="name" cssClass="field-error"/>
        </div>

        <div class="form-group">
            <label>Email *</label>
            <form:input path="username"
                        type="email"
                        required="true"
                        pattern="^[a-zA-Z0-9._%+-]+@gmail\.com$"
                        title="Email must be a valid @gmail.com address"/>
            <form:errors path="username" cssClass="field-error"/>
        </div>

        <div class="form-group">
            <label>Roles *</label>
            <form:select path="roles" multiple="true" required="true">
                <form:options items="${roles}"
                              itemValue="identifier"
                              itemLabel="identifier"/>
            </form:select>
            <form:errors path="roles" cssClass="field-error"/>
        </div>

        <div class="form-group">
            <label>Phone Number *</label>
            <form:input path="phoneNo"
                        required="true"
                        pattern="^[0-9]{10}$"
                        maxlength="10"
                        title="Phone number must be exactly 10 digits"/>
            <form:errors path="phoneNo" cssClass="field-error"/>
        </div>

        <div class="form-group">
            <label>Password *</label>
            <form:password path="password" required="true"/>
            <form:errors path="password" cssClass="field-error"/>
        </div>

        <button type="submit" class="btn-submit">Register</button>

    </form:form>

</div>
</body>
</html>