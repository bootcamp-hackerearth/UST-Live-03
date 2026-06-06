<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Add Stock</title>

<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">

<style>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: 'Poppins', sans-serif;
}

body {
    min-height: 100vh;
    background: linear-gradient(135deg, #1a1b26, #2a2b3d);
    display: flex;
    justify-content: center;
    align-items: center;
}

.card-container {
    position: relative;
    width: 420px;
    padding: 30px;
    border-radius: 15px;
    background: rgba(255,255,255,0.05);
    backdrop-filter: blur(12px);
    border: 1px solid rgba(255,255,255,0.2);
    box-shadow: 0 8px 32px rgba(0,0,0,0.4);
}

.back-icon {
    position: absolute;
    top: 15px;
    left: 15px;
    width: 34px;
    height: 34px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    text-decoration: none;
    font-size: 18px;
    background: rgba(255,255,255,0.1);
    color: #00ffff;
}

.back-icon:hover {
    box-shadow: 0 0 10px #00ffff;
}

h2 {
    text-align: center;
    color: #fff;
    margin-bottom: 20px;
}

label {
    color: #ccc;
    font-size: 13px;
}

.form-control {
    width: 100%;
    padding: 10px;
    margin-top: 6px;
    margin-bottom: 15px;
    border-radius: 8px;
    border: none;
    outline: none;
    background: rgba(255,255,255,0.1);
    color: #fff;
}

select option {
    background: #1a1b26;
    color: #fff;
}

.form-control:focus {
    border: 1px solid #00ffff;
    box-shadow: 0 0 8px #00ffff;
}

.btn-submit {
    width: 100%;
    padding: 12px;
    border-radius: 8px;
    border: none;
    background: #00ffff;
    color: #000;
    font-weight: bold;
    cursor: pointer;
}

.btn-submit:hover {
    box-shadow: 0 0 15px #00ffff,
                0 0 30px #00ffff;
}

.message {
    text-align: center;
    color: #ff8080;
    margin-bottom: 10px;
    font-size: 13px;
}
</style>
</head>

<body>

<div class="card-container">

    <a href="${pageContext.request.contextPath}/stock/list" class="back-icon">←</a>
    <h2>Add Stock</h2>

    <c:if test="${not empty message}">
        <div class="message">
            ${message}
        </div>
    </c:if>

    <form:form method="post"
               action="${pageContext.request.contextPath}/stock/add"
               modelAttribute="stockDto">

        <label>Stock</label>
        <form:input
            path="identifier"
            cssClass="form-control"
            placeholder="Enter stock name"
            required="true"
            minlength="2"
            maxlength="50"
        />

        <label>Product</label>
        <form:select path="productName" cssClass="form-control" required="true">
            <form:option value="" label="-- Select Product --"/>
            <c:forEach items="${product}" var="product">
                <form:option value="${product.productName}">
                    ${product.productName}
                </form:option>
            </c:forEach>
        </form:select>

        <label>Warehouse</label>
        <form:select path="warehouseName" cssClass="form-control" required="true">
            <form:option value="" label="-- Select Warehouse --"/>
            <c:forEach items="${warehouse}" var="warehouse">
                <form:option value="${warehouse.identifier}">
                    ${warehouse.identifier}
                </form:option>
            </c:forEach>
        </form:select>

        <label>Quantity</label>
        <form:input
            path="quantity"
            type="number"
            cssClass="form-control"
            placeholder="Enter quantity"
            required="true"
            min="1"
            max="100000"
        />

        <label>Status</label>
        <form:select path="stockStatus" cssClass="form-control" required="true">
            <form:option value="" label="-- Select Status --"/>
            <form:option value="IN_STOCK">In Stock</form:option>
            <form:option value="OUT_OF_STOCK">Out of Stock</form:option>
        </form:select>

        <button type="submit" class="btn-submit">
            Add Stock
        </button>
    </form:form>

</div>

</body>
</html>