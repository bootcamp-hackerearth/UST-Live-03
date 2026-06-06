<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Rack List</title>

<style>
* {
    margin:0;
    padding:0;
    box-sizing:border-box;
    font-family:'Poppins',sans-serif;
}

body {
    min-height:100vh;
    background:linear-gradient(135deg,#1a1b26,#2a2b3d);
    display:flex;
    justify-content:center;
    align-items:center;
}

.container {
    width:900px;
    padding:30px;
    border-radius:15px;
    background:rgba(255,255,255,0.05);
    backdrop-filter:blur(12px);
    border:1px solid rgba(255,255,255,0.2);
}

h2 {
    text-align:center;
    color:#fff;
    margin-bottom:20px;
}

table {
    width:100%;
    border-collapse:collapse;
}

th {
    color:#00ffff;
    padding:12px;
    text-align:center;
    background:rgba(0,255,255,0.1);
}

td {
    color:#ddd;
    padding:12px;
    text-align:center;
}

.switch {
    position:relative;
    display:inline-block;
    width:50px;
    height:24px;
}

.switch input {
    display:none;
}

.slider {
    position:absolute;
    cursor:pointer;
    top:0;
    left:0;
    right:0;
    bottom:0;
    background:#ff4d4d;
    border-radius:34px;
    transition:.3s;
}

.slider:before {
    position:absolute;
    content:"";
    height:18px;
    width:18px;
    left:3px;
    bottom:3px;
    background:white;
    border-radius:50%;
    transition:.3s;
}

input:checked + .slider {
    background:#00ff99;
}

input:checked + .slider:before {
    transform:translateX(26px);
}

.btn {
    padding:6px 10px;
    border-radius:6px;
    text-decoration:none;
    font-size:12px;
}

.update { background:#ffc107; color:#000; }
.delete { background:#ff4d4d; color:#fff; }

.footer {
    display:flex;
    justify-content:center;
    gap:15px;
    margin-top:20px;
}

.add-btn, .home-btn {
    padding:10px 16px;
    border-radius:8px;
    text-decoration:none;
    font-weight:bold;
}

.add-btn {
    background:#00ffff;
    color:#000;
}

.home-btn {
    background:#666;
    color:#fff;
}
</style>
</head>

<body>

<div class="container">

<h2>Rack List</h2>

<c:if test="${empty racks}">
    <div style="color:#ff8080;text-align:center;">
        No racks found
    </div>
</c:if>

<c:if test="${not empty racks}">
<table>

<tr>
<th>SL</th>
<th>Rack</th>
<th>Shelfs</th>
<th>Status</th>
<th>Action</th>
</tr>

<c:forEach var="r" items="${racks}" varStatus="s">
<tr>

<td>${s.count}</td>
<td>${r.identifier}</td>
<td>${r.shelfs}</td>

<td>
<form action="${pageContext.request.contextPath}/rack/toggleStatus"
      method="post">

    <input type="hidden" name="identifier" value="${r.identifier}"/>

    <label class="switch">
        <input type="checkbox"
               <c:if test="${r.status == true}">checked</c:if>
               onchange="this.form.submit()">
        <span class="slider"></span>
    </label>

</form>
</td>

<td>
<a href="${pageContext.request.contextPath}/rack/get?identifier=${r.identifier}"
   class="btn update">Update</a>

<a href="${pageContext.request.contextPath}/rack/delete?identifier=${r.identifier}"
   class="btn delete"
   onclick="return confirm('Delete this rack?');">
   Delete
</a>
</td>

</tr>
</c:forEach>

</table>
</c:if>

<div class="footer">
<a href="${pageContext.request.contextPath}/"
   class="home-btn">Home</a>

<a href="${pageContext.request.contextPath}/rack/add"
   class="add-btn">+ Add Rack</a>
</div>

</div>

</body>
</html>