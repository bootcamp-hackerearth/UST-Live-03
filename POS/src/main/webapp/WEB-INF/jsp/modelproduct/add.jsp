<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Add Model Product</title>

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
    display:flex;
    justify-content:center;
    align-items:center;
    background:linear-gradient(135deg,#1a1b26,#2a2b3d);
}

.card-container{
    width:450px;
    padding:30px;
    border-radius:15px;
    background:rgba(255,255,255,0.05);
    backdrop-filter:blur(12px);
    border:1px solid rgba(255,255,255,0.2);
    box-shadow:0 8px 32px rgba(0,0,0,0.4);
    position:relative;
}

.back-icon{
    position:absolute;
    top:20px;
    left:20px;
    color:#00ffff;
    text-decoration:none;
    font-size:20px;
}

h2{
    text-align:center;
    color:#fff;
    margin-bottom:20px;
}

label{
    color:#ccc;
    font-size:14px;
}

.form-control{
    width:100%;
    padding:10px;
    margin-top:6px;
    margin-bottom:15px;
    border-radius:8px;
    border:none;
    outline:none;
    background:rgba(255,255,255,0.1);
    color:#fff;
}

.form-control:focus{
    border:1px solid #00ffff;
    box-shadow:0 0 8px #00ffff;
}

textarea.form-control{
    resize:vertical;
    min-height:100px;
}

select option{
    background:#1a1b26;
    color:#fff;
}

.btn-submit{
    width:100%;
    padding:12px;
    border:none;
    border-radius:8px;
    background:#00ffff;
    color:#000;
    font-weight:600;
    cursor:pointer;
    transition:.3s;
}

.btn-submit:hover{
    box-shadow:0 0 15px #00ffff,0 0 30px #00ffff;
}

.error-message{
    margin-bottom:15px;
    padding:10px;
    border-radius:8px;
    text-align:center;
    color:#ff8080;
    font-size:13px;
    background:rgba(255,0,0,0.08);
}
</style>
</head>

<body>

<a href="/modelproduct/list" class="back-icon">←</a>

<div class="card-container">

    <h2>Add Model Product</h2>

    <c:if test="${not empty message}">
        <div class="error-message">
            ${message}
        </div>
    </c:if>

    <form:form
        method="post"
        action="/modelproduct/add"
        modelAttribute="modelProductDto">

        <label>Model Name</label>
        <form:input
            path="identifier"
            cssClass="form-control"
            placeholder="Enter Model Name"
            required="true"
            minlength="2"
            maxlength="100"
            pattern="^[A-Za-z0-9 ]+$"
            title="Only letters, numbers and spaces are allowed"
        />

        <label>Description</label>
        <form:textarea
            path="description"
            cssClass="form-control"
            placeholder="Enter Description"
            rows="4"
            required="true"
            maxlength="500"
        />

        <label>Status</label>
        <form:select
            path="status"
            cssClass="form-control"
            required="true">

            <form:option value="">-- Select Status --</form:option>
            <form:option value="true">Active</form:option>
            <form:option value="false">Inactive</form:option>

        </form:select>

        <button type="submit" class="btn-submit">
            Add Model Product
        </button>

    </form:form>

</div>

</body>
</html>