<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Stock List</title>

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
    width: 950px;
    padding: 30px;
    border-radius: 15px;

    background: rgba(255,255,255,0.05);
    backdrop-filter: blur(12px);

    border: 1px solid rgba(255,255,255,0.2);
    box-shadow: 0 8px 32px rgba(0,0,0,0.4);
}

h2 {
    text-align: center;
    color: #fff;
    margin-bottom: 20px;
}

table {
    width: 100%;
    border-collapse: collapse;
    color: #fff;
}

th {
    background: rgba(0,255,255,0.2);
    color: #00ffff;
    padding: 12px;
}

td {
    padding: 12px;
    text-align: center;
    border-bottom: 1px solid rgba(255,255,255,0.1);
}

tr:hover {
    background: rgba(0,255,255,0.1);
}

.btn {
    padding: 6px 10px;
    border-radius: 6px;
    font-size: 12px;
    text-decoration: none;
    margin: 0 4px;
    border: none;
    cursor: pointer;
}

.btn-update {
    background: #00ffff;
    color: #000;
}

.btn-delete {
    background: #ff4d4d;
    color: #fff;
}

.btn-home {
    background: #666;
    color: #fff;
}

.btn-add {
    background: #00ffff;
    color: #000;
}

.btn:hover {
    box-shadow: 0 0 10px #00ffff;
}

.alert {
    text-align: center;
    padding: 10px;
    color: #aaa;
}

.footer-actions {
    margin-top: 20px;
    display: flex;
    justify-content: center;
    gap: 12px;
}
</style>
</head>

<body>

<div class="card-container">

    <h2>Stock List</h2>
    <c:if test="${empty stocks}">
        <div class="alert">No stock records found</div>
    </c:if>

    <c:if test="${not empty stocks}">
        <table>
            <thead>
            <tr>
                <th>Stock</th>
                <th>Product</th>
                <th>Warehouse</th>
                <th>Quantity</th>
                <th>stockStatus</th>
                <th>Action</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="stock" items="${stocks}">
                <tr>
                    <td>${stock.identifier}</td>
                    <td>${stock.productName}</td>
                    <td>${stock.warehouseName}</td>
                    <td>${stock.quantity}</td>
                    <td>${stock.stockStatus}</td>

                    <td>
                        <a href="${pageContext.request.contextPath}/stock/get?identifier=${stock.identifier}"
                           class="btn btn-update">
                            Update
                        </a>

                        <a href="${pageContext.request.contextPath}/stock/delete?identifier=${stock.identifier}"
                           class="btn btn-delete"
                           onclick="return confirm('Are you sure you want to delete this stock?');">
                            Delete
                        </a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>

    <div class="footer-actions">
        <a href="${pageContext.request.contextPath}/" class="btn btn-home">Home</a>
        <a href="${pageContext.request.contextPath}/stock/add" class="btn btn-add">+ Add Stock</a>
    </div>

</div>

</body>
</html>