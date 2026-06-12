<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Product List</title>

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

.container {
    width: 1100px;
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

.footer {
    margin-top: 20px;
    text-align: center;
}

.footer .actions {
    display: flex;
    justify-content: center;
    gap: 12px;
}
</style>
</head>

<body>

<div class="container">

    <h2>List of Product</h2>

    <c:if test="${empty products}">
        <div class="alert">
            No Products found
        </div>
    </c:if>

    <c:if test="${not empty products}">
        <table>
            <thead>
            <tr>
                <th>ID</th>
                <th>Product Name</th>
                <th>Category</th>
                <th>Brand</th>
                <th>Unit</th>
                <th>Model</th>
                <th>Supplier ID</th>
                <th>Action</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="product" items="${products}">
                <tr>
                    <td>${product.id}</td>
                    <td>${product.identifier}</td>
                    <td>${product.category}</td>
                    <td>${product.brand}</td>
                    <td>${product.unit}</td>
                    <td>${product.model}</td>
                    <td>${product.supplierId}</td>

                    <td>
                        <a href="/product/get?identifier=${product.identifier}"
                           class="btn btn-update">
                            Update
                        </a>

                        <a href="/product/delete?identifier=${product.identifier}"
                           class="btn btn-delete"
                           onclick="return confirm('Are you sure you want to delete this product?');">
                            Delete
                        </a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>

    <div class="footer">
        <div class="actions">
            <a href="/" class="btn btn-home">Home</a>
            <a href="/product/add" class="btn btn-add">+ Add Product</a>
        </div>
    </div>

</div>

</body>
</html>