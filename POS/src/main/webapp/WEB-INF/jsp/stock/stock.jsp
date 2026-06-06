<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Edit Stock</title>

<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
      rel="stylesheet">

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

input, select {
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

input:focus, select:focus {
    border: 1px solid #00ffff;
    box-shadow: 0 0 8px #00ffff;
}

.btn {
    width: 100%;
    padding: 12px;
    border-radius: 8px;
    border: none;
    background: #00ffff;
    color: #000;
    font-weight: bold;
    cursor: pointer;
}

.btn:hover {
    box-shadow: 0 0 15px #00ffff,
                0 0 30px #00ffff;
}

.error-message {
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
    <h2>Edit Stock</h2>

    <c:if test="${not empty message}">
        <div class="error-message">${message}</div>
    </c:if>

    <form method="post" action="${pageContext.request.contextPath}/stock/update">
        <input type="hidden" name="identifier" value="${stock.identifier}" />
        <label>Stock</label>
        <input type="text" value="${stock.identifier}" readonly />

        <label>Product</label>
        <select name="productName" required>
            <option value="">-- Select Product --</option>
            <c:forEach items="${product}" var="product">
                <option value="${product.productName}"
                    ${product.productName == stock.productName ? 'selected' : ''}>
                    ${product.productName}
                </option>
            </c:forEach>
        </select>

        <label>Warehouse</label>
        <select name="warehouseName" required>
            <option value="">-- Select Warehouse --</option>
            <c:forEach items="${warehouse}" var="warehouse">
                <option value="${warehouse.identifier}"
                    ${warehouse.identifier == stock.warehouseName ? 'selected' : ''}>
                    ${warehouse.identifier}
                </option>
            </c:forEach>
        </select>

        <label>Quantity</label>
        <input
            type="number"
            name="quantity"
            value="${stock.quantity}"
            required
            min="1"
            max="100000"
        />

        <label>Status</label>
        <select name="stockStatus" required>
            <option value="IN_STOCK"
                ${stock.stockStatus == 'IN_STOCK' ? 'selected' : ''}>
                In Stock
            </option>
            <option value="OUT_OF_STOCK"
                ${stock.stockStatus == 'OUT_OF_STOCK' ? 'selected' : ''}>
                Out of Stock
            </option>
        </select>

        <button type="submit" class="btn">
            Update
        </button>

    </form>

</div>

</body>
</html>