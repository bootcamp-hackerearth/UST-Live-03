<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Add Stock</title>

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

/* MAIN CONTAINER */
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
    color: #0f172a;
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
    color: #334155;
}

/* INPUT + SELECT */
input, select {
    width: 100%;
    padding: 12px;
    margin-top: 8px;
    border-radius: 8px;
    border: 1px solid #cbd5f5;
    font-size: 14px;
    transition: all 0.2s ease;
    background: #f8fafc;
}

input:focus, select:focus {
    outline: none;
    border-color: #6366f1;
    background: #ffffff;
    box-shadow: 0 0 0 2px rgba(99,102,241,0.15);
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
    font-weight: 500;
    cursor: pointer;
    transition: 0.3s ease;
}

button:hover {
    transform: translateY(-1px);
    box-shadow: 0 6px 15px rgba(79,70,229,0.25);
}

/* ERROR */
.error {
    margin-top: 15px;
    color: #ef4444;
    font-size: 13px;
    text-align: center;
}

/* FOOTER BUTTON */
.btn-container {
    text-align: center;
    margin-top: 25px;
}

.btn {
    display: inline-block;
    padding: 10px 20px;
    border-radius: 8px;
    text-decoration: none;
    font-size: 14px;
    color: white;
    background: #6366f1;
    transition: 0.3s;
}

.btn:hover {
    background: #4f46e5;
}

</style>

<script>
function validateForm() {

    const product = document.forms["stockForm"]["product"].value;
    const warehouse = document.forms["stockForm"]["warehouse"].value;
    const minStock = document.forms["stockForm"]["minimumStock"].value;
    const quantity = document.forms["stockForm"]["quantity"].value;

    if (product === "") {
        alert("Please select a product");
        return false;
    }

    if (warehouse === "") {
        alert("Please select a warehouse");
        return false;
    }

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

    <h2>Add Stock</h2>

    <div class="form-container">

        <form name="stockForm"
              action="${pageContext.request.contextPath}/stock/add"
              method="post"
              onsubmit="return validateForm()">

            <!-- PRODUCT DROPDOWN -->
            <label>Select Product</label>
            <select name="product" required>
                <option value="">-- Select Product --</option>
                <c:forEach var="product" items="${products}">
                    <option value="${product.identifier}">
                        ${product.identifier}
                    </option>
                </c:forEach>
            </select>

            <!-- WAREHOUSE DROPDOWN -->
            <label>Select Warehouse</label>
            <select name="warehouse" required>
                <option value="">-- Select Warehouse --</option>
                <c:forEach var="warehouse" items="${warehouses}">
                    <option value="${warehouse.identifier}">
                        ${warehouse.identifier}
                    </option>
                </c:forEach>
            </select>

            <!-- MINIMUM STOCK -->
            <label>Minimum Stock</label>
            <input type="number"
                   name="minimumStock"
                   placeholder="Enter minimum stock"
                   min="0"
                   required />

            <!-- QUANTITY -->
            <label>Quantity</label>
            <input type="number"
                   name="quantity"
                   placeholder="Enter quantity"
                   min="0"
                   required />

            <button type="submit">Save Stock</button>

        </form>

        <div class="error">${error}</div>

    </div>

    <div class="btn-container">
        <a href="${pageContext.request.contextPath}/stock/list" class="btn">
            View Stock List
        </a>
    </div>

</div>

</body>
</html>