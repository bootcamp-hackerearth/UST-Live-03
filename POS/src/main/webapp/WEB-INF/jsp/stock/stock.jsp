<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Update Stock</title>

<style>

/* RESET */
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

/* BODY */
body {
    font-family: 'Segoe UI', Arial, sans-serif;
    background: linear-gradient(135deg, #e0e7ff, #f8fafc);
    color: #1e293b;
    min-height: 100vh;
    display: flex;
    align-items: center;
    justify-content: center;
}

/* CONTAINER */
.container {
    width: 100%;
    max-width: 700px;
}

/* TITLE */
h2 {
    text-align: center;
    margin-bottom: 30px;
    font-size: 28px;
    font-weight: 600;
}

/* CARD */
.form-container {
    background: #ffffff;
    border-radius: 16px;
    padding: 35px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 12px 30px rgba(0,0,0,0.08);
}

/* LABEL */
label {
    display: block;
    margin-top: 18px;
    font-size: 14px;
    font-weight: 500;
}

/* INPUT + SELECT */
input, select {
    width: 100%;
    padding: 12px;
    margin-top: 8px;
    border-radius: 8px;
    border: 1px solid #cbd5f5;
    font-size: 14px;
    background: #f8fafc;
}

input:focus, select:focus {
    outline: none;
    border-color: #6366f1;
    background: #ffffff;
}

/* DISABLED SELECT */
select:disabled {
    background: #e2e8f0;
    cursor: not-allowed;
}

/* BUTTON */
button {
    width: 100%;
    padding: 14px;
    margin-top: 25px;
    background: linear-gradient(135deg, #6366f1, #4f46e5);
    color: white;
    border: none;
    border-radius: 10px;
    font-size: 15px;
    cursor: pointer;
}

button:hover {
    box-shadow: 0 6px 15px rgba(79,70,229,0.25);
}

/* MESSAGE */
.message {
    margin-top: 15px;
    color: #ef4444;
    font-size: 13px;
    text-align: center;
}

/* BUTTON LINK */
.btn-container {
    text-align: center;
    margin-top: 25px;
}

.btn {
    display: inline-block;
    padding: 10px 20px;
    border-radius: 8px;
    text-decoration: none;
    color: white;
    background: #6366f1;
}

.btn:hover {
    background: #4f46e5;
}

</style>

<script>
function validateForm() {

    const minStock = document.forms["stockForm"]["minimumStock"].value;
    const quantity = document.forms["stockForm"]["quantity"].value;

    if (minStock === "" || minStock < 0) {
        alert("Minimum stock must be 0 or greater");
        return false;
    }

    if (quantity === "" || quantity < 0) {
        alert("Quantity must be 0 or greater");
        return false;
    }

    return true;
}
</script>

</head>

<body>

<div class="container">

    <h2>Update Stock</h2>

    <div class="form-container">

        <form name="stockForm"
              action="${pageContext.request.contextPath}/stock/update"
              method="post"
              onsubmit="return validateForm()">

            <!-- Identifier -->
            <label>Identifier</label>
            <input type="text"
                   name="identifier"
                   value="${stock.identifier}"
                   readonly />

            <!-- PRODUCT (DISABLED DROPDOWN) -->
            <label>Product</label>
            <select disabled>
                <c:forEach var="product" items="${products}">
                    <option value="${product.identifier}"
                        <c:if test="${product.identifier == stock.product}">selected</c:if>>
                        ${product.identifier}
                    </option>
                </c:forEach>
            </select>

            <!-- Hidden field (IMPORTANT) -->
            <input type="hidden" name="product" value="${stock.product}" />

            <!-- WAREHOUSE (DISABLED DROPDOWN) -->
            <label>Warehouse</label>
            <select disabled>
                <c:forEach var="warehouse" items="${warehouses}">
                    <option value="${warehouse.identifier}"
                        <c:if test="${warehouse.identifier == stock.warehouse}">selected</c:if>>
                        ${warehouse.identifier}
                    </option>
                </c:forEach>
            </select>

            <!-- Hidden field -->
            <input type="hidden" name="warehouse" value="${stock.warehouse}" />

            <!-- MINIMUM STOCK -->
            <label>Minimum Stock</label>
            <input type="number"
                   name="minimumStock"
                   value="${stock.minimumStock}"
                   min="0"
                   required />

            <!-- QUANTITY -->
            <label>Quantity</label>
            <input type="number"
                   name="quantity"
                   value="${stock.quantity}"
                   min="0"
                   required />

            <button type="submit">Update Stock</button>

        </form>

        <div class="message">${message}</div>

    </div>

    <div class="btn-container">
        <a href="${pageContext.request.contextPath}/stock/list" class="btn">
            View Stock List
        </a>
    </div>

</div>

</body>
</html>