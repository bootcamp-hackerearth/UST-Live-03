<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Role</title>

    <style>

        body {
            margin: 0;
            font-family: "Inter", sans-serif;
            background-color: #3f3f3f;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
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
        textarea {
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

        textarea {
            resize: none;
            height: 70px;
            overflow: hidden;
        }

        input:focus,
        textarea:focus {
            border-bottom: 3px solid #3f3f3f;
        }

        input[readonly] {
            color: #7a7a7a;
            cursor: not-allowed;
        }

        input:-webkit-autofill,
        input:-webkit-autofill:hover,
        input:-webkit-autofill:focus,
        textarea:-webkit-autofill {

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
            margin-top: 16px;
            padding: 12px;
            background: #ffe5e0;
            color: #b91c1c;
            font-size: 13px;
        }

    </style>

</head>

<body>

<div class="brand-card">

    <a href="${pageContext.request.contextPath}/role/list"
       class="back-btn">

        ᐸ BACK

    </a>

    <h2>Edit Role</h2>

    <c:if test="${not empty message}">

        <div class="error-message">

            ${message}

        </div>

    </c:if>

    <form:form
            action="${pageContext.request.contextPath}/role/update"
            method="post"
            modelAttribute="role">

        <form:hidden path="id"/>

        <div class="form-group">

            <label>ROLE NAME</label>

            <form:input
                    path="identifier"
                    readonly="true"/>

        </div>

        <div class="form-group">

            <label>DESCRIPTION</label>

            <form:textarea
                    path="description"
                    rows="3"/>

        </div>

        <button type="submit"
                class="update-btn">

            UPDATE ROLE

        </button>

    </form:form>

</div>

</body>
</html>