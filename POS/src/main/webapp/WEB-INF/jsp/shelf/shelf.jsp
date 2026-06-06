<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Update Shelf</title>

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

.card {
    width:400px;
    padding:30px;
    border-radius:12px;
    background:rgba(255,255,255,0.05);
}

h2 {
    color:#fff;
    text-align:center;
    margin-bottom:15px;
}

label {
    color:#ccc;
    font-size:13px;
    display:block;
    margin-top:10px;
}

input, textarea, select {
    width:100%;
    padding:10px;
    margin-top:6px;
    margin-bottom:15px;
    border-radius:6px;
    border:none;
    background:rgba(255,255,255,0.1);
    color:#fff;
}

input[readonly] {
    background:rgba(255,255,255,0.05);
    color:#aaa;
}

textarea {
    min-height:90px;
    resize:none;
}

input:focus, textarea:focus, select:focus {
    border:1px solid #00ffff;
    box-shadow:0 0 6px #00ffff;
}

button {
    width:100%;
    padding:12px;
    background:#00ffff;
    border:none;
    border-radius:8px;
    font-weight:bold;
    cursor:pointer;
}

.error {
    color:#ff8080;
    text-align:center;
    margin-bottom:10px;
}
</style>
</head>

<body>

<div class="card">

<h2>Update Shelf</h2>

<c:if test="${not empty message}">
    <div class="error">${message}</div>
</c:if>

<form method="post" action="/shelf/update">

    <label>Identifier</label>
    <input name="identifier" value="${shelfDto.identifier}" readonly />

    <label>Description</label>
    <textarea name="description">${shelfDto.description}</textarea>

    <label>Status</label>
    <select name="status">
        <option value="true" <c:if test="${shelfDto.status == true}">selected</c:if>>Active</option>
        <option value="false" <c:if test="${shelfDto.status == false}">selected</c:if>>Inactive</option>
    </select>

    <button type="submit">Update</button>

</form>

</div>

</body>
</html>