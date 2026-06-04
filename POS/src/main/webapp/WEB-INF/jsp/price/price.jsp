<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Edit Price</title>

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

input[disabled] {
    background: #f1f5f9;
    cursor: not-allowed;
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

    let price = document.forms["priceForm"]["price"].value;
    let priceType = document.forms["priceForm"]["priceType"].value;

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

    <h2>Edit Price</h2>

    <div class="form-container">

        <c:if test="${not empty message}">
            <div class="error">${message}</div>
        </c:if>

        <form name="priceForm"
              action="${pageContext.request.contextPath}/price/update"
              method="post"
              onsubmit="return validateForm()">

            <label>Product</label>
            <input type="text" value="${priceDto.product}" disabled />

            <input type="hidden" name="product" value="${priceDto.product}" />

            <label>Identifier</label>
            <input type="hidden" name="identifier" value="${priceDto.identifier}" hidden />

            <label>Price</label>
            <input type="number" step="0.01" name="costPrice"
                   value="${priceDto.costPrice}" required = "true"/>

            <label>Price Type</label>
            <input type="text" name="priceType" value="${priceDto.priceType}" required = "true"/>

            <button type="submit" class="btn">Update Price</button>

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