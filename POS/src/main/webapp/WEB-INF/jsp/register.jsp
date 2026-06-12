<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Register | POS</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: 'Inter', 'Segoe UI', sans-serif;
            background: linear-gradient(135deg, #f6f8fb, #eef2f7);
            height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .register-wrapper {
            width: 460px;
            background: #ffffff;
            padding: 30px;
            border-radius: 14px;
            box-shadow: 0 20px 60px rgba(0, 0, 0, 0.08);
        }

        .brand {
            text-align: center;
            margin-bottom: 22px;
        }

        .logo {
            width: 52px;
            height: 52px;
            border-radius: 12px;
            background: linear-gradient(135deg, #4f46e5, #6366f1);
            display: inline-flex;
            align-items: center;
            justify-content: center;
            color: #ffffff;
            font-size: 22px;
            font-weight: 600;
            margin-bottom: 8px;
        }

        .brand h1 {
            font-size: 20px;
            margin: 0;
            color: #1f2937;
        }

        .brand p {
            font-size: 13px;
            color: #6b7280;
            margin-top: 6px;
        }

        .form-group {
            margin-bottom: 12px;
        }

        .form-group label {
            font-size: 12px;
            color: #374151;
            margin-bottom: 5px;
            display: block;
        }

        input, select {
            width: 100%;
            padding: 10px 12px;
            border-radius: 8px;
            border: 1px solid #d1d5db;
            font-size: 13px;
        }

        input:focus, select:focus {
            border-color: #6366f1;
            outline: none;
            box-shadow: 0 0 0 2px rgba(99, 102, 241, 0.15);
        }

        .btn {
            width: 100%;
            background: #4f46e5;
            color: #ffffff;
            border: none;
            border-radius: 8px;
            padding: 11px;
            font-size: 14px;
            cursor: pointer;
            margin-top: 8px;
        }

        .btn:hover {
            background: #4338ca;
        }

        .error {
            color: #dc2626;
            font-size: 11px;
            margin-top: 2px;
            display: block;
        }

        .footer-links {
            text-align: center;
            margin-top: 16px;
            font-size: 12px;
            color: #6b7280;
        }

        .footer-links a {
            color: #4f46e5;
            text-decoration: none;
            font-weight: 500;
        }
    </style>
</head>

<body>

    <div class="register-wrapper">

        <div class="brand">
            <div class="logo">POS</div>
            <h1>Create Account</h1>
            <p>Get started with your POS system</p>
        </div>

        <form:form action="/register" method="post" modelAttribute="userDto">

            <div class="form-group">
                <label>Name</label>
                <form:input path="name" required="true"/>
                <form:errors path="name" cssClass="error"/>
            </div>

            <div class="form-group">
                <label>Email</label>
                <form:input path="username" type="email" required="true"/>
                <form:errors path="username" cssClass="error"/>
            </div>

            <div class="form-group">
                <label>Password</label>
                <form:password path="password" required="true"
                    pattern="(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&]).{8,}"/>
                <form:errors path="password" cssClass="error"/>
            </div>

            <div class="form-group">
                <label>Phone</label>
                <form:input path="phoneNo"
                            maxlength="10"
                            pattern="[0-9]{10}"
                            inputmode="numeric"
                            oninput="this.value=this.value.replace(/[^0-9]/g,'')"
                            required="true"/>
                <form:errors path="phoneNo" cssClass="error"/>
            </div>

            <div class="form-group">
                <label>Roles</label>
                <form:select path="roles" multiple="true" required="true">
                    <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
                </form:select>
                <form:errors path="roles" cssClass="error"/>
            </div>

            <button type="submit" class="btn">Register</button>

        </form:form>

        <div class="footer-links">
            Already have an account? <a href="/login">Login</a>
        </div>

    </div>

</body>
</html>