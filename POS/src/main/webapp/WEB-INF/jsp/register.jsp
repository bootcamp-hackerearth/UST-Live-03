<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Register</title>

    <style>
        body {
            margin: 0;
            font-family: 'Segoe UI', sans-serif;
            background: #f2f4f8;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .container {
            width: 800px;
            height: 480px;
            display: flex;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 10px 30px rgba(0,0,0,0.15);
            background: white;
        }

        .left {
            width: 50%;
            padding: 30px;
            overflow-y: auto;
        }

        h2 {
            margin-bottom: 15px;
            color: #444;
        }

        input, select {
            width: 100%;
            padding: 9px;
            margin-bottom: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 13px;
        }

        input:focus, select:focus {
            border-color: #007bff;
            outline: none;
        }

        .btn {
            width: 100%;
            padding: 10px;
            background: #007bff;
            color: white;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }

        .btn:hover {
            background: #0056b3;
        }

        .link {
            text-align: center;
            margin-top: 8px;
            font-size: 12px;
        }

        .link a {
            color: #007bff;
            text-decoration: none;
        }

        .right {
            width: 50%;
            background: #007bff;
            color: white;
            display: flex;
            justify-content: center;
            align-items: center;
            text-align: center;
        }

        .right h1 {
            font-size: 26px;
            line-height: 1.4;
        }

        .error {
            color: red;
            font-size: 11px;
            margin-bottom: 8px;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="left">
        <h2>Create Account</h2>

        <form:form action="/register" method="post" modelAttribute="userDto">

            <form:input path="name" placeholder="Name" required="true"/>
            <form:errors path="name" cssClass="error"/>

            <form:input path="username" type="email" placeholder="Email" required="true"/>
            <form:errors path="username" cssClass="error"/>

            <form:password path="password" placeholder="Password" required="true" pattern="(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}"/>
            <form:errors path="password" cssClass="error"/>

            <form:input path="phoneNo"
                        placeholder="Phone (10 digits)"
                        maxlength="10"
                        pattern="[0-9]{10}"
                        inputmode="numeric"
                        oninput="this.value=this.value.replace(/[^0-9]/g,'')"
                        required="true"/>
            <form:errors path="phoneNo" cssClass="error"/>

            <form:select path="roles" multiple="true" required="true">
                <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
            </form:select>
            <form:errors path="roles" cssClass="error"/>

            <button type="submit" class="btn">Register</button>

        </form:form>

        <div class="link">
            Already have account? <a href="/login">Login</a>
        </div>

    </div>

    <div class="right">
        <h1>POS<br>System</h1>
    </div>

</div>

</body>
</html>