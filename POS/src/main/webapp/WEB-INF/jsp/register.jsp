<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>User Registration</title>

    <style>

        body {
            margin: 0;
            font-family: "Inter", sans-serif;
            background-color: #3f3f3f;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
            padding: 30px 0;
            overflow-y: auto;
        }

        .register-card {
            background: #f3efe9;
            width: 380px;
            padding: 28px 30px;
            border-radius: 4px;
            text-align: left;
            margin: 20px 0;
        }

        .app-title {
            font-size: 12px;
            font-weight: 700;
            color: #2f2f2f;
            letter-spacing: 1px;
            margin-bottom: 4px;
        }

        h2 {
            margin: 0 0 18px;
            font-size: 22px;
            font-weight: 700;
            color: #2f2f2f;
        }

        .back-btn {
            display: inline-block;
            margin-bottom: 14px;
            text-decoration: none;
            color: #3f3f3f;
            font-size: 12px;
            font-weight: 700;
            letter-spacing: 1px;
        }

        .back-btn:hover {
            opacity: 0.7;
        }

        .form-group {
            margin-bottom: 14px;
        }

        label {
            font-size: 10px;
            letter-spacing: 1px;
            color: #7a7a7a;
            display: block;
            margin-bottom: 4px;
        }

        input,
        select {
            width: 100%;
            box-sizing: border-box;
            padding: 8px 0;
            border: none;
            border-bottom: 2px solid #cfcfcf;
            background: transparent;
            font-size: 14px;
            outline: none;
            color: #2f2f2f;
        }

        input:focus,
        select:focus {
            border-bottom: 2px solid #3f3f3f;
        }

        select[multiple] {
            height: 90px;
        }

        .register-btn {
            width: 100%;
            box-sizing: border-box;
            padding: 11px;
            margin-top: 12px;
            background: #3f3f3f;
            color: #ffffff;
            border: 2px solid #3f3f3f;
            font-weight: 700;
            letter-spacing: 1px;
            cursor: pointer;
            transition: 0.3s;
        }

        .register-btn:hover {
            background: transparent;
            color: #3f3f3f;
        }

        .error-message {
            margin-bottom: 14px;
            padding: 8px;
            background: #ffe5e0;
            color: #b91c1c;
            font-size: 12px;
        }

    </style>

</head>

<body>

<div class="register-card">

    <a href="${pageContext.request.contextPath}/login"
       class="back-btn">
         ᐸ BACK
    </a>

    <div class="app-title">
        POS APPLICATION
    </div>

    <h2>Register</h2>

    <c:if test="${not empty message}">
        <div class="error-message">
            ${message}
        </div>
    </c:if>

    <form:form action="${pageContext.request.contextPath}/register"
               method="post"
               modelAttribute="userDto">

        <div class="form-group">

            <label>NAME</label>

            <form:input
                    path="name"
                    required="true"/>

        </div>

        <div class="form-group">

            <label>EMAIL</label>

            <form:input
                    path="username"
                    type="email"
                    required="true"
                    placeholder="example@mail.com"/>

        </div>

        <div class="form-group">

            <label>ROLES</label>

            <form:select
                    path="roles"
                    multiple="true"
                    required="true">

                <form:options
                        items="${roles}"
                        itemValue="identifier"
                        itemLabel="identifier"/>

            </form:select>

        </div>

        <div class="form-group">

            <label>PHONE NUMBER</label>

            <form:input
                    path="phoneNo"
                    type="tel"
                    required="true"
                    pattern="[0-9]{10}"
                    maxlength="10"
                    title="Phone number must be exactly 10 digits"
                    oninput="this.value=this.value.replace(/[^0-9]/g,'')"/>

        </div>

        <div class="form-group">

            <label>PASSWORD</label>

            <form:password
                    path="password"
                    required="true"
                    minlength="6"/>

        </div>

        <button type="submit"
                class="register-btn">

            REGISTER

        </button>

    </form:form>

</div>

</body>
</html>