<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Add Price</title>

<style>

* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: 'Segoe UI', Arial, sans-serif;
    background: linear-gradient(135deg, #eef2ff, #f8fafc);
    color: #1e293b;
    padding: 40px 20px;
}

.container {
    width: 95%;
    max-width: 500px;
    margin: auto;
}

h2 {
    text-align: center;
    margin-bottom: 25px;
}

.form-container {
    background: #ffffff;
    padding: 25px;
    border-radius: 12px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 8px 20px rgba(0,0,0,0.05);
}

input, select {
    width: 100%;
    padding: 10px;
    margin: 10px 0 18px 0;
    border-radius: 6px;
    border: 1px solid #cbd5f5;
    font-size: 14px;
}

.btn {
    width: 100%;
    padding: 10px;
    border: none;
    border-radius: 6px;
    background: #6366f1;
    color: white;
    font-size: 14px;
    cursor: pointer;
}

.btn:hover {
    background: #4f46e5;
}

.error {
    color: #dc2626;
    margin-bottom: 10px;
    text-align: center;
}

.btn-container {
    text-align: center;
    margin-top: 20px;
}

.link-btn {
    display: inline-block;
    margin-top: 10px;
    text-decoration: none;
    color: #0ea5e9;
}

</style>

<script>
function validateForm() {

    let productId = document.forms["priceForm"]["productId"].value;
    let price = document.forms["priceForm"]["price"].value;
    let priceType = document.forms["priceForm"]["priceType"].value;

    if (productId === "") {
        alert("Please select a product");
        return false;
    }

    if (price === "" || isNaN(price) || Number(price) <= 0) {
        alert("Price must be a valid number greater than 0");
        return false;
    }

    if (priceType === "") {
        alert("Please select a price type");
        return false;
    }

    return true;
}
</script>

</head>

<body>

<div class="container">

    <h2>Add Price</h2>

    <div class="form-container">

        <!-- Backend Error -->
        <c:if test="${not empty message}">
            <div class="error">${message}</div>
        </c:if>

        <form name="priceForm"
              action="${pageContext.request.contextPath}/price/add"
              method="post"
              onsubmit="return validateForm()">

            <!-- Product Dropdown -->
            <label>Product</label>
            <select name="product">
                <option value="">-- Select Product --</option>
                <c:forEach items="${products}" var="product">
                    <option value="${product.identifier}">
                        ${product.identifier}
                    </option>
                </c:forEach>
            </select>

            <!-- Price -->
            <label>Price</label>
            <input type="number" step="0.01" name="costPrice" />

            <!-- Price Type -->
            <label>Price Type</label>
            <input type="text" name="priceType" />

            <button type="submit" class="btn">Save Price</button>

        </form>

    </div>

    <div class="btn-container">
        <a href="${pageContext.request.contextPath}/price/list" class="link-btn">
            Back to Price List
        </a>
    </div>

</div>

</body>
</html>