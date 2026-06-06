<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Brand List</title>

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
    padding:30px;
}

.top-bar {
    display:flex;
    justify-content:space-between;
    align-items:center;
    margin-bottom:20px;
}

h2 { color:#fff; }

.add-btn {
    padding:10px 18px;
    background:#00ffff;
    color:#000;
    border-radius:8px;
    text-decoration:none;
    font-weight:600;
}

.add-btn:hover {
    box-shadow:0 0 10px #00ffff;
}

.search-bar input {
    width:300px;
    padding:10px;
    border-radius:8px;
    border:none;
    background:rgba(255,255,255,0.1);
    color:#fff;
}

table {
    width:100%;
    border-collapse:collapse;
    background:rgba(255,255,255,0.05);
    backdrop-filter:blur(10px);
}

th {
    color:#00ffff;
    padding:12px;
    background:rgba(0,255,255,0.1);
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

img {
    width:40px;
    height:40px;
    border-radius:50%;
}

.switch {
    position:relative;
    display:inline-block;
    width:50px;
    height:24px;
}

.switch input { display:none; }

.slider {
    position:absolute;
    cursor:pointer;
    top:0; left:0; right:0; bottom:0;
    background:#ff4d4d;
    border-radius:34px;
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
    padding:7px 14px;
    border-radius:8px;
    text-decoration:none;
    font-size:13px;
    font-weight:600;
    display:inline-block;
    margin:2px;
}

.update-btn {
    background:#ffc107;
    color:#000;
}

.update-btn:hover {
    box-shadow:0 0 10px #ffc107;
}

.delete-btn {
    background:#ff4d4d;
    color:#fff;
}

.delete-btn:hover {
    box-shadow:0 0 10px #ff4d4d;
}

.bottom-bar {
    margin-top:25px;
    display:flex;
    justify-content:center;
    gap:20px;
}

.home-btn {
    padding:10px 20px;
    background:#666;
    color:#fff;
    border-radius:8px;
    text-decoration:none;
    font-weight:600;
}

.add-bottom-btn {
    padding:10px 20px;
    background:#00ffff;
    color:#000;
    border-radius:8px;
    text-decoration:none;
    font-weight:600;
}

.add-bottom-btn:hover {
    box-shadow:0 0 12px #00ffff;
}
</style>
</head>

<body>

<table id="brandTable">
<thead>
<tr>
<th>SL</th>
<th>Brand Name</th>
<th>Description</th>
<th>Status</th>
<th>Action</th>
</tr>
</thead>

<tbody>
<c:forEach items="${brands}" var="brand" varStatus="loop">
<tr>

<td>${loop.index + 1}</td>

<td class="brand-name">${brand.identifier}</td>
<td>${brand.description}</td>

<td>
<form action="${pageContext.request.contextPath}/brand/toggleStatus" method="post">
<input type="hidden" name="identifier" value="${brand.identifier}"/>

<label class="switch">
<input type="checkbox"
       <c:if test="${brand.status}">checked</c:if>
       onchange="this.form.submit()">
<span class="slider"></span>
</label>

</form>
</td>

<td>
<a href="${pageContext.request.contextPath}/brand/get?identifier=${brand.identifier}"
   class="btn update-btn">Update</a>

<a href="${pageContext.request.contextPath}/brand/delete?identifier=${brand.identifier}"
   class="btn delete-btn"
   onclick="return confirm('Delete this brand?');">
   Delete
</a>
</td>

</tr>
</c:forEach>
</tbody>
</table>

<div class="bottom-bar">
<a href="/" class="home-btn">
    Home
</a>

<a href="/brand/add" class="add-bottom-btn">
    + Add  Brand
</a>

</div>


</body>
</html>