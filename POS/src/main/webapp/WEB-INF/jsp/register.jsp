<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

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
            background: linear-gradient(135deg, #667eea, #764ba2);
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .register-card {
            position: relative;
            width: 430px;
            background: rgba(255, 255, 255, 0.95);
            padding: 35px 40px;
            border-radius: 16px;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
        }

        .back-icon {
            position: absolute;
            top: 16px;
            left: 16px;
            width: 36px;
            height: 36px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
            color: #4b6cb7;
            text-decoration: none;
            background: rgba(75, 108, 183, 0.08);
            border-radius: 50%;
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
            color: #4b6cb7;
        }

        .error-message {
            margin-bottom: 16px;
            padding: 10px;
            background: rgba(220, 53, 69, 0.12);
            border: 1px solid #dc3545;
            color: #dc3545;
            border-radius: 8px;
            text-align: center;
            font-size: 13px;
            font-weight: 500;
        }

        .form-group {
            margin-bottom: 14px;
        }

        label {
            font-size: 13px;
            font-weight: 500;
        }

        input, select {
            width: 100%;
            padding: 11px 14px;
            border-radius: 8px;
            border: 1px solid #ccc;
        }

        .btn-submit {
            width: 100%;
            padding: 13px;
            margin-top: 10px;
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: white;
            border: none;
            border-radius: 10px;
            font-weight: 600;
            cursor: pointer;
        }
    </style>
</head>

<body>

<div class="register-card">

    <a href="/login" class="back-icon">←</a>
    <h2>User Registration</h2>

    <c:if test="${not empty message}">
        <div class="error-message">
            ${message}
        </div>
    </c:if>

    <form:form action="register" method="post" modelAttribute="userDto">

        <div class="form-group">
            <label>Name</label>
            <form:input path="name"/>
        </div>

        <div class="form-group">
            <label>Email</label>
            <form:input path="username"
                        type="email"
                        required="required"
                        title="Email must end with .com"/>
        </div>

        <div class="form-group">
            <label>Roles</label>
            <form:select path="roles" multiple="true">
                <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
            </form:select>
        </div>

        <div class="form-group">
            <label>Phone Number</label>
            <form:input path="phoneNo"
                        maxlength="10"
                        pattern="[0-9]{10}"
                        title="Enter exactly 10 digit phone number"
                        oninput="this.value=this.value.replace(/[^0-9]/g,'')"/>
        </div>

        <div class="form-group">
            <label>Password</label>
            <form:password path="password" required="required"/>
        </div>

        <input type="submit" value="Register" class="btn-submit"/>

    </form:form>

</div>

</body>
</html>