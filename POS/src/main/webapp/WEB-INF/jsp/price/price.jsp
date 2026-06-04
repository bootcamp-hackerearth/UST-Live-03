<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Price</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #ffffff;
        }

        .card {
            width: 360px;
            margin: 40px auto;
            background: #ffffff;
            padding: 22px;
            border-radius: 14px;
            position: relative;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
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
            background: #ffffff;
            border: 1px solid teal;
            text-decoration: none;
            border-radius: 16px;
            font-size: 12px;
            font-weight: 600;
            color: teal;
        }

        h2 {
            text-align: center;
            margin-bottom: 12px;
            font-size: 20px;
        }

        label {
            display: block;
            margin-top: 10px;
            font-size: 12px;
            font-weight: 600;
            color: #475569;
        }

        input {
            display: block;
            width: 100%;
            box-sizing: border-box;
            height: 34px;
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
            width: 100%;
            height: 34px;
            background: teal;
            color: white;
            border: none;
            border-radius: 18px;
            font-weight: 600;
            font-size: 13px;
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

    <a href="${pageContext.request.contextPath}/price/list" class="back-btn">
        Back
    </a>

    <h2>Edit Price</h2>
    <c:if test="${not empty message}">
        <div class="error-message">${message}</div>
    </c:if>

    <form:form action="${pageContext.request.contextPath}/price/update"
               method="post"
               modelAttribute="price">

        <form:hidden path="id"/>

        <label>Product Name</label>
        <form:input path="identifier" readonly="true"/>

        <label>MRP</label>
        <form:input path="mrp" type="number" step="0.01"/>

        <label>Selling Price</label>
        <form:input path="sellingPrice" type="number" step="0.01"/>

        <label>Effective From</label>
        <form:input path="effectiveFrom" type="date"/>

        <button type="submit">Update Price</button>
    </form:form>
</div>

</body>
</html>