<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Add Shelf</title>

<style>
body {
    font-family: 'Poppins', sans-serif;
    background: linear-gradient(135deg,#1a1b26,#2a2b3d);
    display:flex;
    justify-content:center;
    align-items:center;
    height:100vh;
}

.card {
    width:380px;
    padding:30px;
    border-radius:12px;
    background:rgba(255,255,255,0.05);
}

h2 { color:#fff; text-align:center; }

label { color:#ccc; font-size:13px; }

input, select, textarea {
    width:100%;
    padding:10px;
    margin:10px 0;
    border-radius:6px;
    border:none;
    background:rgba(255,255,255,0.1);
    color:#fff;
}

textarea {
    min-height:90px;
    resize:none;
}

input:focus, select:focus, textarea:focus {
    border:1px solid #00ffff;
    box-shadow:0 0 6px #00ffff;
}

button {
    width:100%;
    padding:10px;
    background:#00ffff;
    border:none;
    border-radius:6px;
    cursor:pointer;
}
</style>
</head>

<body>

<div class="card">

<h2>Add Shelf</h2>

<c:if test="${not empty message}">
    <div style="color:red;text-align:center">${message}</div>
</c:if>

<!-- ✅ NORMAL FORM -->
<form method="post" action="/shelf/add">

    <label>Identifier</label>
    <input name="identifier"
           required
           minlength="2"
           maxlength="50"
           pattern="[A-Za-z0-9 ]+"
           placeholder="Shelf code" />

    <label>Description</label>
    <textarea name="description"
              placeholder="Enter description"
              maxlength="200"></textarea>

    <label>Status</label>
    <select name="status">
        <option value="true">Active</option>
        <option value="false">Inactive</option>
    </select>

    <button type="submit">Save</button>

</form>

</div>

</body>
</html>