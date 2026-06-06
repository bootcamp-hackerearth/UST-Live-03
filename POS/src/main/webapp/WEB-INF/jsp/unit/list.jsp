<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Unit List</title>

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
    box-shadow:0 8px 32px rgba(0,0,0,0.4);
}

h2 {
    color:white;
    text-align:center;
    margin-bottom:20px;
}

table {
    width:100%;
    border-collapse:collapse;
}

th {
    color:#00ffff;
    padding:12px;
    background:rgba(0,255,255,0.1);
}

td {
    color:#ddd;
    text-align:center;
    padding:12px;
    border-bottom:1px solid rgba(255,255,255,0.1);
}

tr:hover {
    background:rgba(255,255,255,0.05);
}

.switch {
    position:relative;
    width:50px;
    height:24px;
    display:inline-block;
}

.switch input { display:none; }

.slider {
    position:absolute;
    background:#ff4d4d;
    border-radius:34px;
    top:0; left:0; right:0; bottom:0;
    transition:.3s;
}

.slider:before {
    content:"";
    position:absolute;
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
    padding:6px 12px;
    border-radius:6px;
    text-decoration:none;
    font-size:12px;
    margin:2px;
}

.update {
    background:#ffc107;
    color:#000;
}

.delete {
    background:#ff4d4d;
    color:#fff;
}

.footer {
    display:flex;
    justify-content:center;
    gap:20px;
    margin-top:25px;
}

.home {
    background:#666;
    color:#fff;
    padding:10px 18px;
    border-radius:8px;
    text-decoration:none;
    font-weight:bold;
}

.add {
    background:#00ffff;
    color:#000;
    padding:10px 18px;
    border-radius:8px;
    font-weight:bold;
    text-decoration:none;
}

.add:hover {
    box-shadow:0 0 15px #00ffff;
}
</style>
</head>

<body>

<div class="container">

<h2>Unit List</h2>

<table>

<tr>
<th>ID</th>
<th>Name</th>
<th>Status</th>
<th>Action</th>
</tr>

<c:forEach var="u" items="${units}">
<tr>

<td>${u.id}</td>
<td>${u.identifier}</td>

<td>
<form action="/unit/toggleStatus" method="post">
<input type="hidden" name="identifier" value="${u.identifier}"/>

<label class="switch">
<input type="checkbox"
       <c:if test="${u.status}">checked</c:if>
       onchange="this.form.submit()">
<span class="slider"></span>
</label>

</form>
</td>

<td>
<a href="/unit/get?identifier=${u.identifier}" class="btn update">Update</a>
<a href="/unit/delete?identifier=${u.identifier}" class="btn delete">Delete</a>
</td>

</tr>
</c:forEach>

</table>

<div class="footer">
<a href="/" class="home">
Home
</a>

<a href="/unit/add" class="add">
+ Add Unit
</a>

</div>

</div>

</body>
</html>