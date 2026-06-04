<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Model</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            font-family: "Segoe UI", Tahoma, Arial, sans-serif;
            background: linear-gradient(120deg, #e0eafc, #cfdef3);
            padding: 30px;
            margin: 0;
        }

        .container {
            width: 420px;
            margin: auto;
            background: #ffffff;
            padding: 28px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.12);
        }

        h2 {
            text-align: center;
            margin-bottom: 22px;
            color: #2c3e50;
        }

        .form-group {
            margin-bottom: 16px;
        }

        label {
            display: block;
            font-weight: 600;
            margin-bottom: 6px;
            color: #333;
        }

        input, select {
            width: 100%;
            padding: 10px;
            border-radius: 6px;
            border: 1px solid #ccc;
            font-size: 14px;
        }

        input[readonly] {
            background-color: #f1f3f5;
        }

        .btn-group {
            margin-top: 24px;
            display: flex;
            justify-content: center;
            gap: 12px;
        }

        .btn {
            padding: 10px 18px;
            border-radius: 6px;
            border: none;
            cursor: pointer;
            font-size: 14px;
            font-weight: 600;
        }

        .save-btn {
            background: #28a745;
            color: #ffffff;
        }

        .cancel-btn {
            background-color: #6c757d;
            color: #ffffff;
            text-decoration: none;
        }

        .error-msg {
            background-color: #fdecea;
            color: #b02a37;
            border: 1px solid #f5c2c7;
            padding: 10px;
            border-radius: 6px;
            text-align: center;
            margin-bottom: 18px;
            font-weight: 600;
        }

        .back-link {
            display: block;
            text-align: center;
            margin-top: 18px;
            font-weight: 600;
            text-decoration: none;
            color: #0d6efd;
        }
    </style>
</head>

<body>

<div class="container">
    <h2>Edit Model</h2>

    <c:if test="${not empty message}">
        <div class="error-msg">${message}</div>
    </c:if>

    <form:form method="post"
               action="${pageContext.request.contextPath}/model/update"
               modelAttribute="modelDto">

        <div class="form-group">
            <label for="identifier">Model Name</label>
            <form:input path="identifier"
                        id="identifier"
                        readonly="true"/>
        </div>

        <div class="form-group">
            <label>Status</label>
            <form:select path="status" required="true">
                <form:option value="true">Active</form:option>
                <form:option value="false">Deactive</form:option>
            </form:select>
        </div>

        <div class="btn-group">
            <button type="submit" class="btn save-btn">Save</button>
            <a href="${pageContext.request.contextPath}/model/list"
               class="btn cancel-btn">Cancel</a>
        </div>

    </form:form>

    <a class="back-link"
       href="${pageContext.request.contextPath}/model/list">
        ← Back to Model List
    </a>

</div>

</body>
</html>