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
            background: #ffffff;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card-container {
            position: relative;
            width: 450px;
            background: rgba(255, 255, 255, 0.95);
            padding: 35px 40px;
            border-radius: 16px;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
        }

        h2 {
            text-align: center;
            margin-bottom: 25px;
            color: #4b6cb7;
            font-weight: 600;
        }

        /* ✅ Back Icon */
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
            font-weight: 600;
            background: rgba(75, 108, 183, 0.08);
            border-radius: 50%;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.12);
            transition: all 0.25s ease;
        }

        .back-icon:hover {
            background: #4b6cb7;
            color: #ffffff;
            transform: translateX(-4px) scale(1.05);
        }

        .form-group {
            margin-bottom: 16px;
        }

        label {
            display: block;
            margin-bottom: 6px;
            font-weight: 500;
            font-size: 14px;
            color: #333;
        }

        .form-control {
            width: 100%;
            padding: 10px 12px;
            border-radius: 10px;
            border: 1px solid #ccc;
            font-family: 'Poppins', sans-serif;
            font-size: 14px;
        }

        .form-control:focus {
            outline: none;
            border-color: #4b6cb7;
            box-shadow: 0 0 0 2px rgba(75, 108, 183, 0.2);
        }

        select[multiple] {
            height: 100px;
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

        .btn-submit {
            width: 100%;
            padding: 12px;
            margin-top: 10px;
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: white;
            border: none;
            border-radius: 12px;
            font-weight: 600;
            font-size: 15px;
            cursor: pointer;
            transition: 0.25s ease;
        }

        .btn-submit:hover {
            transform: scale(1.05);
        }

        .footer-text {
            text-align: center;
            margin-top: 15px;
            font-size: 12px;
            color: #777;
        }
    </style>
</head>

<body>

<div class="card-container">

    <!-- ✅ Back to Login -->
    <a href="/login" class="back-icon">←</a>

    <h2>User Registration</h2>

    <!-- ✅ Message -->
    <c:if test="${not empty message}">
        <div class="error-message">
            ${message}
        </div>
    </c:if>

    <form:form action="register" method="post" modelAttribute="userDto">

        <div class="form-group">
            <label>Name</label>
            <form:input path="name" cssClass="form-control" required="true"/>
        </div>

        <div class="form-group">
            <label>Email</label>
            <form:input path="username"
                        cssClass="form-control"
                        type="email"
                        required="true"
                        pattern="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.com$"
                        title="Email must end with .com"/>
        </div>

        <div class="form-group">
            <label>Roles</label>
            <form:select path="roles" multiple="true" cssClass="form-control">
                <form:options items="${roles}"
                              itemValue="identifier"
                              itemLabel="identifier"/>
            </form:select>
        </div>

        <div class="form-group">
            <label>Phone Number</label>
            <form:input path="phoneNo"
                        cssClass="form-control"
                        maxlength="10"
                        pattern="[0-9]{10}"
                        title="Enter exactly 10 digit phone number"
                        oninput="this.value=this.value.replace(/[^0-9]/g,'')" />
        </div>

        <div class="form-group">
            <label>Password</label>
            <form:password path="password" cssClass="form-control" required="true"/>
        </div>

        <button type="submit" class="btn-submit">
            Register
        </button>

    </form:form>

    <div class="footer-text">
        POS Management System
    </div>

</div>

</body>
</html>