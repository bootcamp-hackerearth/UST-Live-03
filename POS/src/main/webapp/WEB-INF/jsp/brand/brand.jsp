<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Edit Brand</title>

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
    width:450px;
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

label {
    color:#ccc;
    font-size:14px;
    display:block;
    margin-top:10px;
}

input, textarea, select {
    width:100%;
    padding:10px;
    margin-top:6px;
    margin-bottom:12px;

    border-radius:8px;
    border:none;
    outline:none;

    background:rgba(255,255,255,0.1);
    color:#fff;
}

select option {
    background:#1a1b26;
    color:#fff;
}

input:focus, textarea:focus, select:focus {
    border:1px solid #00ffff;
    box-shadow:0 0 8px #00ffff;
}

input[readonly] {
    background:rgba(255,255,255,0.05);
    color:#aaa;
}

.btn-group {
    margin-top:20px;
    display:flex;
    justify-content:space-between;
}

.btn-save {
    padding:10px 20px;
    border-radius:8px;
    border:none;
    cursor:pointer;
    background:#00ffff;
    color:#000;
    font-weight:bold;
}

.btn-save:hover {
    box-shadow:0 0 15px #00ffff;
}

.btn-cancel {
    padding:10px 20px;
    border-radius:8px;
    background:#666;
    color:#fff;
    text-decoration:none;
}

.btn-cancel:hover {
    background:#555;
}
</style>
</head>

<body>

<div class="container">
    <h2>Edit Brand</h2>

    <form action="/brand/update" method="post">

        <input type="hidden" name="identifier" value="${brandDto.identifier}" />

        <label>Brand Name</label>
        <input type="text" value="${brandDto.identifier}" readonly />

        <label>Description</label>
        <textarea name="description" rows="4">${brandDto.description}</textarea>

        <label>Status</label>
        <select name="status">
            <option value="true" ${brandDto.status ? 'selected' : ''}>Active</option>
            <option value="false" ${!brandDto.status ? 'selected' : ''}>Inactive</option>
        </select>

        <div class="btn-group">
            <button type="submit" class="btn-save">Update</button>
            <a href="/brand/list" class="btn-cancel">Cancel</a>
        </div>

    </form>
</div>

</body>
</html>