<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>User Registration</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
          rel="stylesheet">

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

        .error {
            color: #d9534f;
            font-size: 12px;
            margin-top: 4px;
            display: block;
            text-align: center;
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

    <!-- ❌ Duplicate email message from backend -->
    <c:if test="${not empty message}">
        <div class="error">${message}</div>
    </c:if>

    <!-- ✅ FORM -->
    <form:form action="register" method="post" modelAttribute="userDto">

        <!-- Name -->
        <div class="form-group">
            <label>Name</label>
            <form:input path="name" required="true"/>
        </div>

        <!-- ✅ EMAIL (STRICT VALIDATION) -->
        <div class="form-group">
            <label>Email</label>

            <!--
              ✅ THIS BLOCKS `manager@mjbj`
              ✅ REQUIRES a dot + valid domain
              ✅ lowercase only
            -->
            <form:input
                path="username"
                type="text"
                required="true"
                pattern="^[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$"
                title="Enter a valid email like user@gmail.com (lowercase only)" />

        </div>

        <!-- Roles -->
        <div class="form-group">
            <label>Roles</label>
            <form:select path="roles" multiple="true" required="true">
                <form:options items="${roles}"
                              itemValue="identifier"
                              itemLabel="identifier"/>
            </form:select>
        </div>

        <!-- Phone -->
        <div class="form-group">
            <label>Phone Number</label>
            <form:input
                path="phoneNo"
                type="text"
                required="true"
                pattern="[0-9]{10}"
                title="Phone number must be exactly 10 digits"/>
        </div>

        <!-- Password -->
        <div class="form-group">
            <label>Password</label>
            <form:password path="password" required="true"/>
        </div>

        <input type="submit" value="Register" class="btn-submit"/>

    </form:form>
</div>

<!-- ✅ FORCE LOWERCASE (JSP‑ONLY, NO BACKEND) -->
<script>
    const emailInput = document.querySelector('input[name="username"]');
    emailInput.addEventListener('input', function () {
        this.value = this.value.toLowerCase();
    });
</script>

</body>
</html>