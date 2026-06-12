<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Model Product List</title>

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
}

th {
    background: rgba(0,255,255,0.2);
    color: #00ffff;
    padding: 12px;
}

td {
    padding: 12px;
    text-align: center;
    color: #ddd;
    border-bottom: 1px solid rgba(255,255,255,0.1);
}

tr:hover {
    background: rgba(255,255,255,0.05);
}

.toggle-switch {
    position: relative;
    width: 52px;
    height: 26px;
    display: inline-block;
}

.toggle-switch input {
    display: none;
}

.toggle-slider {
    position: absolute;
    inset: 0;
    background-color: #e74c3c;
    border-radius: 30px;
    transition: .3s;
    cursor: pointer;
}

.toggle-slider:before {
    content: "";
    position: absolute;
    height: 20px;
    width: 20px;
    left: 3px;
    bottom: 3px;
    background: white;
    border-radius: 50%;
    transition: .3s;
}

.toggle-switch input:checked + .toggle-slider {
    background-color: #2ecc71;
}

.toggle-switch input:checked + .toggle-slider:before {
    transform: translateX(26px);
}

.btn {
    padding: 6px 12px;
    border-radius: 6px;
    text-decoration: none;
    font-size: 12px;
    margin: 2px;
}

.update {
    background: #ffc107;
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
</style>
</head>

<body>

<div class="container">

<h2>Model Product List</h2>

<c:if test="${empty modelProducts}">
    <div style="color:#ff8080; text-align:center;">
        No Model Products Found
    </div>
</c:if>

<c:if test="${not empty modelProducts}">
<table>
<thead>
<tr>
<th>Model Name</th>
<th>Status</th>
<th>Actions</th>
</tr>
</thead>

<tbody>
<c:forEach var="m" items="${modelProducts}">
<tr>

<td>${m.identifier}</td>

<td>
<form action="${pageContext.request.contextPath}/modelproduct/toggleStatus"
      method="post" style="margin:0;">

<input type="hidden" name="identifier" value="${m.identifier}"/>

<label class="toggle-switch">
<input type="checkbox"
       <c:if test="${m.status == true}">checked</c:if>
       onchange="this.form.submit()"/>
<span class="toggle-slider"></span>
</label>

</form>
</td>

<td>
<a href="${pageContext.request.contextPath}/modelproduct/get?identifier=${m.identifier}"
   class="btn update">Update</a>

<a href="${pageContext.request.contextPath}/modelproduct/delete?identifier=${m.identifier}"
   class="btn delete"
   onclick="return confirm('Are you sure you want to delete this model?');">
   Delete
</a>
</td>

</tr>
</c:forEach>
</tbody>
</table>
</c:if>

<div class="footer">

<a href="${pageContext.request.contextPath}/" class="home">
Home
</a>

<a href="${pageContext.request.contextPath}/modelproduct/add" class="add">
+ Add  Model
</a>

</div>

</div>

</body>
</html>