<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Warehouse List</title>

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
    margin-bottom: 20px;
    color: #fff;
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

.action-group {
    display: flex;
    justify-content: center;
    gap: 10px;
}

.btn {
    padding: 6px 14px;
    border-radius: 8px;
    text-decoration: none;
    font-size: 13px;
    font-weight: 600;
}

.update-btn {
    background: #00ffff;
    color: #000;
}

.delete-btn {
    background: #ff4d4d;
    color: #fff;
}

.footer-actions {
    margin-top: 20px;
    display: flex;
    justify-content: center;
    gap: 15px;
}

.btn-home {
    background: #6c757d;
    color: white;
    padding: 10px 18px;
    border-radius: 8px;
    text-decoration: none;
}

.btn-add {
    background: #00ffff;
    color: #000;
    padding: 10px 18px;
    border-radius: 8px;
    text-decoration: none;
    font-weight: bold;
}

.btn-add:hover {
    box-shadow: 0 0 15px #00ffff;
}

.alert-warning {
    text-align: center;
    padding: 10px;
    color: #aaa;
}
</style>
</head>

<body>

<div class="card-container">

<h2>List of Warehouses</h2>

<c:if test="${empty warehouses}">
    <div class="alert-warning">
        No Warehouses found
    </div>
</c:if>

<c:if test="${not empty warehouses}">
<table>
<thead>
<tr>
<th>ID</th>
<th>Warehouse Name</th>
<th>Country</th>
<th>Pincode</th>
<th>Address</th>
<th>Action</th>
</tr>
</thead>

<tbody>
<c:forEach var="warehouse" items="${warehouses}">
<tr>

<td>${warehouse.id}</td>
<td>${warehouse.identifier}</td>
<td>${warehouse.country}</td>
<td>${warehouse.pincode}</td>
<td>${warehouse.address}</td>

<td>
<div class="action-group">

<a href="/warehouse/get?identifier=${warehouse.identifier}"
   class="btn update-btn">
   Update
</a>

<a href="/warehouse/delete?identifier=${warehouse.identifier}"
   class="btn delete-btn"
   onclick="return confirm('Are you sure you want to delete this warehouse?');">
   Delete
</a>

</div>
</td>

</tr>
</c:forEach>
</tbody>
</table>
</c:if>

<div class="footer-actions">
<a href="/" class="btn-home">Home</a>
<a href="/warehouse/add" class="btn-add">+ Add Warehouse</a>
</div>

</div>

</body>
</html>