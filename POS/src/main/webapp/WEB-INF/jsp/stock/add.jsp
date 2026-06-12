<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Retail POS | Add Stock</title>

<style>
    body {
        font-family: system-ui, sans-serif;
        background: #f8fafc;
        display: flex;
        justify-content: center;
        align-items: center;
        min-height: 100vh;
        margin: 0;
    }

    .container {
        background: #ffffff;
        width: 420px;
        padding: 32px;
        border-radius: 10px;
        box-shadow: 0 4px 6px rgba(0,0,0,0.1);
        border-top: 4px solid #0f172a;
    }

    h2 {
        text-align: center;
        margin-bottom: 20px;
    }

    label {
        font-size: 12px;
        font-weight: 600;
        margin-top: 14px;
        display: block;
        color: #475569;
    }

    input, select {
        width: 100%;
        padding: 10px;
        margin-top: 6px;
        border-radius: 6px;
        border: 1px solid #e2e8f0;
    }

    button {
        margin-top: 24px;
        width: 100%;
        padding: 12px;
        background: #0f172a;
        color: white;
        border: none;
        border-radius: 6px;
        font-size: 14px;
        font-weight: 600;
        cursor: pointer;
    }

    .error-message {
        background: #fff1f2;
        color: #be123c;
        padding: 10px;
        margin-bottom: 16px;
        border-radius: 6px;
        text-align: center;
        font-size: 14px;
    }
</style>
</head>

<body>

<div class="container">
<!-- Back Button -->
    <a href="${pageContext.request.contextPath}/stock/list">← Back</a>

    <h2>Add Stock</h2>

    <c:if test="${not empty message}">
        <div class="error-message">${message}</div>
    </c:if>

    <form:form
        method="post"
        action="${pageContext.request.contextPath}/stock/add"
        modelAttribute="stockDto">

        <label>Identifier</label>
        <form:input path="identifier" required="true"/>

        <label>Product</label>
        <form:select path="product" required="true">
            <form:option value="" label="-- Select Product --"/>
            <form:options
                items="${product}"
                itemValue="identifier"
                itemLabel="identifier"/>
        </form:select>

        <label>Warehouse</label>
        <form:select path="warehouse" required="true">
            <form:option value="" label="-- Select Warehouse --"/>
            <form:options
                items="${warehouse}"
                itemValue="identifier"
                itemLabel="identifier"/>
        </form:select>

        <label>Quantity</label>
        <form:input path="quantity" type="number" min="0" required="true"/>

        <label>Minimum Stock Level</label>
        <form:input path="minimumstock" type="number" min="0" required="true"/>

        <button type="submit">Add Stock</button>

    </form:form>

</div>

</body>
</html>