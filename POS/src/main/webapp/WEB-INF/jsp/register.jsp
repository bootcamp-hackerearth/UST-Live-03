<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>POS Retail Management | Register</title>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            background: #F4F5F7;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 20px 0;
        }

        .register-card {
            background: #FFFFFF;
            width: 100%;
            max-width: 430px;
            padding: 40px;
            border-radius: 16px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
        }

        .brand-header {
            background: #0B3C5D;
            margin: -40px -40px 30px -40px;
            padding: 25px;
            border-radius: 16px 16px 0 0;
            color: #FFFFFF;
            text-align: center;
        }

        .brand-header h1 {
            margin: 0;
            font-size: 22px;
            font-weight: 600;
        }

        h2 {
            text-align: center;
            margin-bottom: 8px;
        }

        .subtitle {
            text-align: center;
            font-size: 14px;
            color: #6B7280;
            margin-bottom: 25px;
        }

        .form-group {
            margin-bottom: 16px;
        }

        label {
            display: block;
            font-size: 13px;
            font-weight: 600;
            margin-bottom: 6px;
        }

        input, select {
            width: 100%;
            padding: 12px;
            border: 1px solid #E5E7EB;
            border-radius: 8px;
            font-size: 14px;
        }

        input:focus, select:focus {
            outline: none;
            border-color: #0B3C5D;
            background: #F9FAFB;
        }

        select[multiple] {
            height: 100px;
        }

        .btn-submit {
            width: 100%;
            padding: 12px;
            background: #0B3C5D;
            color: #FFFFFF;
            border: none;
            border-radius: 8px;
            cursor: pointer;
            margin-top: 15px;
        }

        .btn-submit:hover {
            opacity: 0.9;
        }

        .error-message {
            color: #B91C1C;
            font-size: 12px;
            margin-top: 4px;
            display: block;
        }

        .global-error {
            background: #FEE2E2;
            padding: 10px;
            border-radius: 8px;
            margin-bottom: 20px;
            text-align: center;
        }

        .login-link, .footer {
            text-align: center;
            margin-top: 20px;
            font-size: 14px;
        }
    </style>
</head>

<body>

<div class="register-card">
    <div class="brand-header">
        <h1>POS Retail Management</h1>
    </div>
    <h2>Registration</h2>
    <p class="subtitle">Enter all the details to register</p>

    <c:if test="${not empty message}">
        <div class="global-error">${message}</div>
    </c:if>

    <form:form action="register" method="post" modelAttribute="userDto">
        <div class="form-group">
            <label>Full Name *</label>
            <form:input
                    path="name"
                    required="required"
                    minlength="3"
                    maxlength="50"
                    pattern="^[A-Za-z ]+$"
                    placeholder="eg. kushal"
                    title="Only alphabets allowed (3–50 characters)"
            />
            <form:errors path="name" cssClass="error-message"/>
        </div>
        <div class="form-group">
            <label>Email Address *</label>
            <form:input
                    path="username"
                    type="email"
                    required="required"
                    placeholder="eg. user@ust.com"
                    pattern="^[a-zA-Z0-9._%+-]+@(ust\.com|gmail\.com|yahoo\.com|outlook\.com)$"
                    title="Only ust, gmail, yahoo, outlook domains allowed"
            />
            <form:errors path="username" cssClass="error-message"/>
        </div>
        <div class="form-group">
            <label>Roles *</label>
            <form:select path="roles" multiple="true" required="required">
                <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
            </form:select>
            <form:errors path="roles" cssClass="error-message"/>
        </div>
        <div class="form-group">
            <label>Phone Number *</label>
            <form:input
                    path="phoneNo"
                    required="required"
                    inputmode="numeric"
                    maxlength="10"
                    pattern="^[6-9][0-9]{9}$"
                    placeholder="10-digit mobile number"
                    title="Must be 10 digits starting from 6–9"
            />
            <form:errors path="phoneNo" cssClass="error-message"/>
        </div>
        <div class="form-group">
            <label>Password *</label>
            <form:password
                    path="password"
                    required="required"
                    minlength="8"
                    maxlength="18"
                    pattern="^(?=.*[A-Za-z])(?=.*\d)(?=.*[@#$%^&+=]).{8,18}$"
                    placeholder="Strong password"
                    title="8–18 chars with letter, number & special character"
            />
            <form:errors path="password" cssClass="error-message"/>
        </div>
        <input type="submit" value="Register User" class="btn-submit"/>
    </form:form>

    <div class="login-link">
        Already have an account?
        <a href="${pageContext.request.contextPath}/login">Login here</a>
    </div>

    <div class="footer">
        Developed by <strong>UST Global</strong> | Dev POD-1
    </div>
</div>
</body>
</html>