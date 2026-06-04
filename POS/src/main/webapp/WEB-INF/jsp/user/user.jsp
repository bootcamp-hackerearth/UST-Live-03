<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update User</title>

    <style>

        body {
            margin: 0;
            font-family: "Inter", sans-serif;
            background-color: #3f3f3f;
            display: flex;
            justify-content: center;
            align-items: center;
            min-height: 100vh;
        }

        .brand-card {
            background: #f3efe9;
            width: 470px;
            padding: 42px;
            box-sizing: border-box;
        }

        .back-btn {
            display: inline-block;
            margin-bottom: 22px;
            text-decoration: none;
            color: #2f2f2f;
            font-size: 13px;
            font-weight: 700;
            letter-spacing: 1px;
        }

        .back-btn:hover {
            opacity: 0.7;
        }

        h2 {
            margin: 0 0 34px;
            font-size: 26px;
            font-weight: 700;
            color: #2f2f2f;
        }

        .form-group {
            margin-bottom: 28px;
        }

        label {
            font-size: 12px;
            letter-spacing: 2px;
            color: #8a8a8a;
            display: block;
            margin-bottom: 10px;
        }

        input,
        select {
            width: 100%;
            box-sizing: border-box;
            padding: 10px 0;
            border: none;
            border-bottom: 3px solid #cfcfcf;
            background: transparent;
            font-size: 16px;
            outline: none;
            color: #2f2f2f;
            font-family: "Inter", sans-serif;
        }

        select {
            background-color: #f3efe9;
            color: #2f2f2f;
        }

        select option {
            background-color: #f3efe9;
            color: #2f2f2f;
        }

        select[multiple] {
            height: 120px;
        }

        input:focus,
        select:focus {
            border-bottom: 3px solid #3f3f3f;
        }

        input:-webkit-autofill,
        input:-webkit-autofill:hover,
        input:-webkit-autofill:focus {

            -webkit-box-shadow: 0 0 0px 1000px #f3efe9 inset !important;
            -webkit-text-fill-color: #2f2f2f !important;
            transition: background-color 5000s ease-in-out 0s;

        }

        .update-btn {
            width: 100%;
            padding: 16px;
            margin-top: 8px;
            background: #3f3f3f;
            color: #ffffff;
            border: 2px solid #3f3f3f;
            font-size: 15px;
            font-weight: 700;
            letter-spacing: 2px;
            cursor: pointer;
            transition: 0.3s;
        }

        .update-btn:hover {
            background: transparent;
            color: #3f3f3f;
        }

        .error-message {
            margin-bottom: 16px;
            padding: 12px;
            background: #ffe5e0;
            color: #b91c1c;
            font-size: 13px;
        }

    </style>

</head>

<body>

<div class="brand-card">

    <a href="${pageContext.request.contextPath}/user/list"
       class="back-btn">

        ᐸ BACK

    </a>

    <h2>Update User</h2>

    <c:if test="${not empty message}">

        <div class="error-message">

            ${message}

        </div>

    </c:if>

    <form:form
            action="${pageContext.request.contextPath}/user/update"
            method="post"
            modelAttribute="user">

        <form:hidden path="id"/>

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
                    required="true"/>

        </div>

        <div class="form-group">

            <label>PHONE NUMBER</label>

            <form:input
                    path="phoneNo"
                    type="tel"
                    required="true"
                    pattern="[0-9]{10}"
                    maxlength="10"/>

        </div>

        <div class="form-group">

            <label>ROLES</label>

            <form:select
                    path="roles"
                    multiple="true">

                <form:options
                        items="${roles}"
                        itemValue="identifier"
                        itemLabel="identifier"/>

            </form:select>

        </div>

        <button type="submit"
                class="update-btn">

            UPDATE USER

        </button>

    </form:form>

</div>

</body>
</html>