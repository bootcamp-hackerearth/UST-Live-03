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
            width: 430px;
            background: rgba(255, 255, 255, 0.95);
            padding: 35px 40px;
            border-radius: 16px;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
        }

        h2 {
            text-align: center;
            margin-bottom: 30px;
            color: #4b6cb7;
            font-weight: 600;
        }

        .form-group {
            margin-bottom: 16px;
        }

        label {
            font-size: 13px;
            font-weight: 500;
            color: #333;
            margin-bottom: 6px;
            display: block;
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

        small {
            color: #666;
            font-size: 11px;
        }

        .btn-submit {
            margin-top: 10px;
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
    </style>
</head>

<body>

<div class="register-card">
    <h2>User Registration</h2>

    <form:form action="register" method="post" modelAttribute="userDto">

        <c:if test="${success == false}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>

        <div class="form-group">
            <label>Name</label>
            <form:input path="name" required="required"/>
        </div>

        <div class="form-group">
            <label>Email</label>
            <form:input path="username"
                        type="email"
                        required="required"
                        pattern="^[A-Za-z0-9+_.-]+@[A-Za-z0-9-]+\.com$"
                        title="Email must end with .com"/>
        </div>

        <div class="form-group">
            <label>Roles</label>
            <form:select path="roles" multiple="true" required="required">
                <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
            </form:select>
            <small>Hold Ctrl (Windows/Linux) or Cmd (Mac)</small>
        </div>

        <div class="form-group">
            <label>Phone Number</label>
            <form:input path="phoneNo"
                        maxlength="10"
                        minlength="10"
                        pattern="[0-9]{10}"
                        title="Phone number must be exactly 10 digits"
                        required="required"/>
        </div>

        <div class="form-group">
            <label>Password</label>
            <form:password path="password"
                           minlength="6"
                           required="required"
                           title="Password must be at least 6 characters"/>
        </div>

        <input type="submit" value="Register" class="btn-submit"/>
    </form:form>
</div>
</body>
</html>