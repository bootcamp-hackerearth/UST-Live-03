<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %> <!-- IMPORTANT -->

<!DOCTYPE html>
<html>
<head>
    <title>User Registration</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            min-height: 100vh;
            background: linear-gradient(135deg, #0f766e, #022c43);
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .register-card {
            width: 430px;
            background: rgba(255, 255, 255, 0.95);
            padding: 35px 40px;
            border-radius: 16px;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
            color: #4b6cb7;
        }

        .form-group {
            margin-bottom: 16px;
        }

        label {
            font-size: 13px;
            font-weight: 500;
            color: #333;
            display: block;
            margin-bottom: 6px;
        }

        input, select {
            width: 100%;
            padding: 11px 14px;
            border-radius: 8px;
            border: 1px solid #ccc;
            font-size: 14px;
        }

        select[multiple] {
            height: 130px;
        }

        .btn-submit {
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

        .error-box {
            background: #ff4d4d;
            color: #fff;
            padding: 10px;
            border-radius: 8px;
            text-align: center;
            margin-bottom: 15px;
        }

        .success-box {
            background: #28a745;
            color: #fff;
            padding: 10px;
            border-radius: 8px;
            text-align: center;
            margin-bottom: 15px;
        }

        .error-input {
            border: 2px solid #ff4d4d !important;
            }

        /* Link */
        .link-btn {
            display: block;
            text-align: center;
            margin-top: 15px;
            color: #4f46e5;
            text-decoration: none;
            font-size: 13px;
        }

        .link-btn:hover {
            text-decoration: underline;
        }
    </style>
</head>

<body>

<div class="register-card">
    <h2>User Registration</h2>

    <c:if test="${not empty message}">
        <div class="${fn:contains(message,'success') ? 'success-box' : 'error-box'}">
            ${message}
        </div>
    </c:if>

    <form:form action="register" method="post" modelAttribute="userDto">

        <div class="form-group">
            <label>Name</label>
            <form:input path="name" required="required"/>
        </div>

        <div class="form-group">
            <label>Email</label>
            <form:input path="username"
                        type="email"
                        required="required"
                        cssClass="${not empty message ? 'error-input' : ''}"/>
        </div>

        <div class="form-group">
            <label>Roles</label>
            <form:select path="roles" multiple="true" required="required">
                <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
            </form:select>
        </div>

        <div class="form-group">
            <label>Phone Number</label>
            <form:input path="phoneNo"
                        pattern="[0-9]{10}"
                        inputmode="numeric"
                        required="required"
                        oninput="this.value = this.value.replace(/[^0-9]/g, '')"/>
        </div>

        <div class="form-group">
            <label>Password</label>
            <form:password path="password"
                           minlength="6"
                           required="required"/>
        </div>

        <input type="submit" value="Register" class="btn-submit"/>
        <a href="${pageContext.request.contextPath}/user/list" class="link-btn">
            Back
        </a>

    </form:form>
</div>
</body>
</html>