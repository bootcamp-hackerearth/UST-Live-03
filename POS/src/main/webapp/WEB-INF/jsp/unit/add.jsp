<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Add Unit</title>

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

.card-container {
    position:relative;
    width:420px;
    padding:35px;
    border-radius:15px;
    background:rgba(255,255,255,0.05);
    backdrop-filter:blur(12px);
    border:1px solid rgba(255,255,255,0.2);
    box-shadow:0 8px 32px rgba(0,0,0,0.4);
}

.back-icon {
    position:absolute;
    top:15px;
    left:15px;
    width:36px;
    height:36px;
    display:flex;
    align-items:center;
    justify-content:center;
    font-size:18px;
    color:#00ffff;
    text-decoration:none;
    background:rgba(0,255,255,0.1);
    border-radius:50%;
}

.back-icon:hover {
    box-shadow:0 0 10px #00ffff;
}

h2 {
    text-align:center;
    color:#fff;
    margin-bottom:20px;
}

label {
    color:#ccc;
    font-size:13px;
}

input, select {
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

select option {
    background:#1a1b26;
    color:#fff;
}

input:focus, select:focus {
    border:1px solid #00ffff;
    box-shadow:0 0 8px #00ffff;
}

.btn-submit {
    width:100%;
    padding:12px;
    border-radius:8px;
    border:none;
    cursor:pointer;
    font-weight:bold;
    background:#00ffff;
    color:#000;
}

.btn-submit:hover {
    box-shadow:0 0 15px #00ffff;
}

.error-message {
    margin-bottom:12px;
    padding:10px;
    background:rgba(255,0,0,0.15);
    color:#ff8080;
    text-align:center;
    border-radius:6px;
}
</style>
</head>

<body>

<div class="card-container">

    <a href="/unit/list" class="back-icon">←</a>

    <h2>Add Unit</h2>

    <c:if test="${not empty message}">
        <div class="error-message">
            ${message}
        </div>
    </c:if>

    <form:form method="post" action="/unit/add" modelAttribute="unitDto">

        <label>Unit Name</label>
        <form:input
            path="identifier"
            placeholder="Enter Unit Name"
            required="true"
            minlength="2"
            maxlength="50"
            pattern="[A-Za-z ]+"
            title="Enter valid unit name (letters only)"
        />

        <label>Status</label>
        <form:select path="status" required="true">
            <form:option value="true" label="Active"/>
            <form:option value="false" label="Inactive"/>
        </form:select>

        <input type="submit" value="Add Unit" class="btn-submit"/>

    </form:form>

</div>

</body>
</html>