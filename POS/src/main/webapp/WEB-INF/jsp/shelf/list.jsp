<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Shelf List</title>

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
    text-align:center;
    color:#fff;
    margin-bottom:20px;
}

table {
    width:100%;
    border-collapse:collapse;
}

th {
    background:rgba(0,255,255,0.2);
    color:#00ffff;
    padding:12px;
}

td {
    padding:12px;
    text-align:center;
    color:#ddd;
    border-bottom:1px solid rgba(255,255,255,0.1);
}

tr:hover {
    background:rgba(255,255,255,0.05);
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
    padding:6px 10px;
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

.home-btn {
    background:#6c757d;
    color:#fff;
    padding:10px 18px;
    border-radius:10px;
    font-weight:bold;
}

.add-btn {
    background:#00ffff;
    color:#000;
    padding:10px 18px;
    border-radius:10px;
    font-weight:bold;
}

.add-btn:hover {
    box-shadow:0 0 15px #00ffff;
}
</style>
</head>

<body>

<div class="container">

<h2>Shelf List</h2>

<c:if test="${empty shelf}">
    <div style="color:#ff8080;text-align:center">
        No Data
    </div>
</c:if>

<c:if test="${not empty shelf}">
<table>

<tr>
<th>ID</th>
<th>Identifier</th>
<th>Status</th>
<th>Action</th>
</tr>

<c:forEach var="s" items="${shelf}">
<tr>

<td>${s.id}</td>
<td>${s.identifier}</td>

<td>
<form action="${pageContext.request.contextPath}/shelf/toggleStatus"
      method="post">

<input type="hidden" name="identifier" value="${s.identifier}"/>

<label class="switch">
<input type="checkbox"
       <c:if test="${s.status == true}">checked</c:if>
       onchange="this.form.submit()">
<span class="slider"></span>
</label>

</form>
</td>

<td>

<a href="${pageContext.request.contextPath}/shelf/get?identifier=${s.identifier}"
   class="btn update">Update</a>

<a href="${pageContext.request.contextPath}/shelf/delete?identifier=${s.identifier}"
   class="btn delete"
   onclick="return confirm('Are you sure you want to delete this shelf?');">
   Delete
</a>

</td>

</tr>
</c:forEach>

</table>
</c:if>

<div class="footer">

<a href="${pageContext.request.contextPath}/"
   class="home-btn">
   Home
</a>

<a href="${pageContext.request.contextPath}/shelf/add"
   class="add-btn">
   + Add Shelf
</a>

</div>

</div>

</body>
</html>