<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Category List</title>

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

.switch {
    position: relative;
    display: inline-block;
    width: 50px;
    height: 24px;
}

.switch input {
    display: none;
}

.slider {
    position: absolute;
    cursor: pointer;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: #ff4d4d;
    border-radius: 34px;
    transition: .3s;
}

.slider:before {
    position: absolute;
    content: "";
    height: 18px;
    width: 18px;
    left: 3px;
    bottom: 3px;
    background: white;
    border-radius: 50%;
    transition: .3s;
}

input:checked + .slider {
    background: #00ff99;
}

input:checked + .slider:before {
    transform: translateX(26px);
}

.btn {
    padding: 8px 14px;
    border-radius: 8px;
    text-decoration: none;
    font-size: 13px;
    border: none;
    cursor: pointer;
}

.btn-update {
    background: #00ffff;
    color: #000;
    margin-right: 5px;
}

.btn-delete {
    background: #ff4d4d;
    color: #fff;
}

.footer {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin-top: 25px;
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
</style>
</head>

<body>

<div class="container">

<h2>List of Categories</h2>

<c:if test="${empty categorys}">
    <div class="alert">
        No categories found
    </div>
</c:if>

<c:if test="${not empty categorys}">
<table>
<thead>
<tr>
    <th>Category</th>
    <th>Super Category</th>
    <th>Status</th>
    <th>Action</th>
</tr>
</thead>

<tbody>
<c:forEach var="category" items="${categorys}">
<tr>

<td>${category.identifier}</td>
<td>${category.superCategory}</td>

<td>
<form action="/category/toggleStatus" method="post">
<input type="hidden" name="identifier" value="${category.identifier}"/>

<label class="switch">
<input type="checkbox"
       <c:if test="${category.status == true}">checked</c:if>
       onchange="this.form.submit()">
<span class="slider"></span>
</label>

</form>
</td>

<td>
<a href="/category/get?identifier=${category.identifier}"
   class="btn btn-update">
   Update
</a>

<a href="/category/delete?identifier=${category.identifier}"
   class="btn btn-delete"
   onclick="return confirm('Are you sure you want to delete this category?');">
   Delete
</a>
</td>

</tr>
</c:forEach>
</tbody>
</table>
</c:if>

<div class="footer">
<a href="/" class="btn btn-home">Home</a>
<a href="/category/add" class="btn btn-add">+ Add Category</a>
</div>

</div>

</body>
</html>