<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Update Model Product</title>

<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet">

<style>
*{
    margin:0;
    padding:0;
    box-sizing:border-box;
    font-family:'Poppins',sans-serif;
}

body{
    min-height:100vh;
    background:linear-gradient(135deg,#1a1b26,#2a2b3d);
    display:flex;
    align-items:center;
    justify-content:center;
    padding:40px 20px;
}

.container{
    width:100%;
    max-width:520px;
    padding:35px;
    border-radius:16px;
    background:rgba(255,255,255,0.05);
    backdrop-filter:blur(12px);
    border:1px solid rgba(255,255,255,0.12);
    box-shadow:0 8px 32px rgba(0,0,0,0.5);
}

h2{
    text-align:center;
    color:#fff;
    font-size:22px;
    font-weight:700;
    margin-bottom:28px;
}

label{
    display:block;
    color:#ccc;
    font-size:14px;
    font-weight:500;
    margin-bottom:8px;
}

.form-control{
    width:100%;
    padding:12px 15px;
    margin-bottom:20px;
    border-radius:10px;
    border:none;
    outline:none;
    background:#2e3047;
    color:#fff;
    font-size:14px;
    font-family:'Poppins',sans-serif;
    transition:.3s;
}

.form-control:focus{
    border:1px solid #00ffff;
    box-shadow:0 0 8px rgba(0,255,255,0.2);
}

.form-control[readonly]{
    opacity:0.6;
    cursor:not-allowed;
}

textarea.form-control{
    resize:vertical;
    min-height:120px;
}

select.form-control option{
    background:#2e3047;
    color:#fff;
}

.error-message{
    margin-bottom:18px;
    padding:10px;
    border-radius:8px;
    text-align:center;
    color:#ff8080;
    font-size:13px;
    background:rgba(255,0,0,0.08);
    border:1px solid rgba(255,77,77,0.3);
}

.btn-row{
    display:flex;
    justify-content:space-between;
    align-items:center;
    margin-top:10px;
    margin-bottom:20px;
}

.cancel-btn{
    background:#6c757d;
    color:#fff;
    padding:12px 28px;
    border-radius:10px;
    text-decoration:none;
    font-weight:600;
    font-size:15px;
    transition:.3s;
}

.cancel-btn:hover{
    box-shadow:0 0 10px #6c757d;
}

.update-btn{
    background:#00ffff;
    color:#000;
    padding:12px 28px;
    border-radius:10px;
    border:none;
    font-weight:600;
    font-size:15px;
    cursor:pointer;
    font-family:'Poppins',sans-serif;
    transition:.3s;
}

.update-btn:hover{
    box-shadow:0 0 15px #00ffff,0 0 30px #00ffff;
}

.footer-text{
    text-align:center;
    color:#aaa;
    font-size:13px;
}
</style>
</head>

<body>

<div class="container">

    <h2>Edit Model Product</h2>

    <c:if test="${not empty message}">
        <div class="error-message">${message}</div>
    </c:if>

    <form:form
        method="post"
        action="/modelproduct/update"
        modelAttribute="modelProductDto">

        <label>Model Identifier</label>
        <form:input
            path="identifier"
            cssClass="form-control"
            readonly="true"
        />

        <label>Description</label>
        <form:textarea
            path="description"
            cssClass="form-control"
            placeholder="Enter Description"
            required="true"
            maxlength="500"
        />

        <label>Status</label>
        <form:select path="status" cssClass="form-control">
            <form:option value="true">Active</form:option>
            <form:option value="false">Inactive</form:option>
        </form:select>

        <div class="btn-row">
            <a href="/modelproduct/list" class="cancel-btn">Cancel</a>
            <button type="submit" class="update-btn">Update Model</button>
        </div>

    </form:form>

    <div class="footer-text">POS Management System</div>

</div>

</body>
</html>