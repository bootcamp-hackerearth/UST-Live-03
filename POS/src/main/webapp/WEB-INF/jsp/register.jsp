<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>User Registration</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', 'Segoe UI', sans-serif;
            height: 100vh;
            background: linear-gradient(135deg, #232526, #414345);
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .register-card {
            width: 320px;
            background: #ffffff;
            padding: 20px 22px;
            border-radius: 14px;
            box-shadow: 0 18px 40px rgba(0,0,0,0.3);
        }

        h2 {
            text-align: center;
            margin-bottom: 12px;
            color: #333;
            font-size: 20px;
            font-weight: 600;
        }

        .form-group {
            margin-bottom: 10px;
        }

        label {
            font-size: 12.5px;
            color: #444;
            margin-bottom: 3px;
            display: block;
            font-weight: 500;
        }

        input, select {
            width: 100%;
            padding: 6px 8px;
            border-radius: 6px;
            border: 1px solid #ccc;
            font-size: 12.8px;
            transition: 0.3s;
        }

        input:focus, select:focus {
            outline: none;
            border-color: #4facfe;
            box-shadow: 0 0 4px rgba(79,172,254,0.6);
        }


        select[multiple] {
            height: 70px;
        }

        .message-error {
            color: #721c24;
            background: #f8d7da;
            border: 1px solid #f5c6cb;
            padding: 7px;
            border-radius: 6px;
            text-align: center;
            font-size: 12.5px;
            margin-bottom: 8px;
        }

        .btn-submit {
            width: 100%;
            padding: 8px;
            background: linear-gradient(to right, #4facfe, #00f2fe);
            border: none;
            border-radius: 8px;
            font-size: 14px;
            font-weight: 500;
            color: white;
            cursor: pointer;
            margin-top: 6px;
        }

        .btn-submit:hover {
            opacity: 0.9;
            transform: scale(1.02);
        }

        .login-link {
            text-align: center;
            margin-top: 10px;
        }

        .login-link a {
            font-size: 12.5px;
            font-weight: 500;
            color: #4facfe;
            text-decoration: none;
        }

        .login-link a:hover {
            text-decoration: underline;
        }
    </style>
</head>

<body>

<div class="register-card">

    <h2>Create Account</h2>

    <c:if test="${not empty message}">
        <div class="message-error">
            ${message}
        </div>
    </c:if>

    <form:form action="register" method="post" modelAttribute="userDto">

        <div class="form-group">
            <label>Name</label>
            <form:input path="name" required="true"/>
        </div>

        <div class="form-group">
            <label>Email</label>
            <form:input path="username" type="email" required="true"/>
        </div>

        <div class="form-group">
            <label>Roles</label>
            <form:select path="roles" multiple="true" required="true">
                <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
            </form:select>
        </div>

        <div class="form-group">
            <label>Phone Number</label>
            <form:input path="phoneNo"
                        pattern="[0-9]{10}"
                        title="Enter 10 digit phone number"
                        required="true"/>
        </div>

        <div class="form-group">
            <label>Password</label>
            <form:password path="password" required="true" minlength="6"/>
        </div>

        <input type="submit" value="Register" class="btn-submit"/>

    </form:form>

    <div class="login-link">
        <a href="/user/list">Back to User</a>
    </div>

</div>

</body>
</html>