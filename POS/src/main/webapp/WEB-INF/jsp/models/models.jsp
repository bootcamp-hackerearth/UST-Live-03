<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Model</title>

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #ffffff;
        }

        /* ===== CARD ===== */
        .card {
            width: 360px;
            background: #ffffff;
            margin: 40px auto;
            padding: 22px;
            border-radius: 14px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            position: relative;
        }

        .app-title {
            text-align: center;
            font-size: 14px;
            font-weight: 600;
            color: #14b8a6;
            margin-bottom: 4px;
        }

        .back-btn {
            position: absolute;
            top: 12px;
            left: 12px;
            padding: 5px 12px;
            border: 1px solid teal;
            color: teal;
            text-decoration: none;
            border-radius: 16px;
            font-size: 12px;
            font-weight: 600;
        }

        h2 {
            text-align: center;
            margin-bottom: 12px;
            font-size: 20px;
        }

        /* ===== FORM ===== */
        label {
            display: block;
            margin-top: 10px;
            font-size: 12px;
            font-weight: 600;
            color: #475569;
        }

        input, button {
            width: 100%;
            height: 34px;
            box-sizing: border-box;
            padding: 8px 10px;
            margin-top: 4px;
            border-radius: 18px;
            border: 1px solid #d1d5db;
            font-size: 13px;
        }

        input[readonly] {
            background: #f1f5f9;
        }

        button {
            margin-top: 16px;
            background: teal;
            color: white;
            border: none;
            font-weight: 600;
            cursor: pointer;
        }

        .error-message {
            text-align: center;
            color: #dc2626;
            font-size: 12px;
            font-weight: 600;
            margin-bottom: 10px;
        }
    </style>
</head>

<body>

<div class="card">
    <div class="app-title">POS Application</div>
    <a href="${pageContext.request.contextPath}/models/list" class="back-btn">
        Back
    </a>

    <h2>Edit Model</h2>
    <c:if test="${not empty message}">
        <div class="error-message">${message}</div>
    </c:if>
    <form:form action="${pageContext.request.contextPath}/models/update"
               method="post"
               modelAttribute="models">

        <form:hidden path="id"/>

        <label>Model Name</label>
        <form:input path="identifier" readonly="true"/>
        <button type="submit">Update Model</button>
    </form:form>]
</div>

</body>
</html>
