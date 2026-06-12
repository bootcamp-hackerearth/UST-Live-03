<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Customer Management</title>

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
    display: flex;
    justify-content: center;
    align-items: center;
    background: linear-gradient(135deg, #1a1b26, #2a2b3d);
}

.card-container {
    width: 900px;
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
}

th {
    color: #00ffff;
    padding: 12px;
    background: rgba(0,255,255,0.1);
}

td {
    color: #ddd;
    padding: 12px;
    text-align: center;
    border-bottom: 1px solid rgba(255,255,255,0.1);
}

tr:hover {
    background: rgba(255,255,255,0.05);
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

.update {
    background: #00ffff;
    color: #000;
}

.delete {
    background: #ff4d4d;
    color: #fff;
}

.footer {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin-top: 25px;
}

.home {
    background: #666;
    color: #fff;
    padding: 10px 18px;
    border-radius: 8px;
    text-decoration: none;
    font-weight: bold;
}

.add {
    background: #00ffff;
    color: #000;
    padding: 10px 18px;
    border-radius: 8px;
    font-weight: bold;
    text-decoration: none;
}

.add:hover {
    box-shadow: 0 0 15px #00ffff;
}

.no-data {
    text-align: center;
    color: #ff8080;
    margin-bottom: 15px;
}
</style>
</head>

<body>

<div class="card-container">

<h2>Customer Management</h2>

<c:if test="${empty customer}">
    <div class="no-data">
        No customers found
    </div>
</c:if>

<c:if test="${not empty customer}">
<table>

<tr>
<th>ID</th>
<th>Name</th>
<th>Email</th>
<th>Phone</th>
<th>Party Type</th>
<th>Balance</th>
<th>Credit Limit</th>
<th>Actions</th>
</tr>

<c:forEach var="cat" items="${customer}">
<tr>

<td>${cat.id}</td>
<td>${cat.name}</td>
<td>${cat.identifier}</td>
<td>${cat.phoneNo}</td>
<td>${cat.partyType}</td>
<td>${cat.balance}</td>
<td>${cat.creditLimit}</td>

<td>
<div class="action-group">

<a href="/customer/get?identifier=${cat.identifier}" class="btn update">
Update
</a>

<a href="/customer/delete?identifier=${cat.identifier}"
   class="btn delete"
   onclick="return confirm('Are you sure you want to delete this customer?');">
Delete
</a>

</div>
</td>

</tr>
</c:forEach>

</table>
</c:if>

<div class="footer">
<a href="/" class="home">Home</a>
<a href="/customer/add" class="add">+ Add New Customer</a>
</div>

</div>

</body>
</html>