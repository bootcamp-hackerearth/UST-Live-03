<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Add Stock</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #F6F7F9;
            padding: 30px;
        }

        .container {
            max-width: 500px;
            margin: auto;
            background: #ffffff;
            padding: 24px;
            border-radius: 12px;
            box-shadow: 0 6px 14px rgba(0,0,0,0.08);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
            color: #2B2B2B;
        }

        label {
            font-weight: 600;
            display: block;
            margin-top: 12px;
        }

        input, select {
            width: 100%;
            padding: 10px;
            margin-top: 6px;
            border-radius: 6px;
            border: 1px solid #D1D5DB;
        }

        button {
            margin-top: 20px;
            width: 100%;
            padding: 12px;
            background: #2B2B2B;
            color: white;
            border-radius: 6px;
            font-weight: 600;
            cursor: pointer;
            border: none;
        }

        button:hover {
            background: #444;
        }

        .back {
            display: block;
            text-align: center;
            margin-top: 12px;
            font-weight: 600;
            color: #2B2B2B;
            text-decoration: none;
        }
    </style>
</head>

<body>

<div class="container">

    <h2>Add Stock</h2>

    <form:form method="post"
               action="${pageContext.request.contextPath}/stock/add"
               modelAttribute="stockDto">

        <label>Product</label>
        <form:select path="product">
            <form:option value="" label="-- Select Product --"/>
            <c:forEach items="${productList}" var="product">
                <form:option value="${product.name}">${product.name}</form:option>
            </c:forEach>
        </form:select>

        <label>Quantity</label>
        <form:input path="quantity" type="number" min="0" required="true"/>

        <label>Status</label>
        <form:select path="stockStatus">
            <form:option value="" label="-- Select Status --"/>
            <form:option value="AVAILABLE">Available</form:option>
            <form:option value="OUT_OF_STOCK">Out of Stock</form:option>
        </form:select>

        <label>Warehouse</label>
        <form:select path="warehouse">
            <form:option value="" label="-- Select Warehouse --"/>
            <c:forEach items="${warehouseList}" var="wh">
                <form:option value="${wh.identifier}">${wh.identifier}</form:option>
            </c:forEach>
        </form:select>

        <button type="submit">Save</button>
        <a href="${pageContext.request.contextPath}/stock/list" class="back">Back</a>

    </form:form>

</div>

</body>
</html>