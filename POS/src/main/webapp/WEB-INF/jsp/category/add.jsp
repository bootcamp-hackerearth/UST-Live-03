<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Add Category</title>

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
    width:420px;
    padding:30px;
    border-radius:15px;
    background:rgba(255,255,255,0.05);
    backdrop-filter:blur(12px);
    border:1px solid rgba(255,255,255,0.2);
    box-shadow:0 8px 32px rgba(0,0,0,0.4);
    position:relative;
}

.back-btn {
    position:absolute;
    top:15px;
    left:15px;
    color:#00ffff;
    text-decoration:none;
    font-size:18px;
}

h2 {
    text-align:center;
    color:#fff;
    margin-bottom:20px;
}

.form-group {
    margin-bottom:15px;
}

label {
    color:#ccc;
    font-size:13px;
}

input, select {
    width:100%;
    padding:10px;
    margin-top:6px;
    border-radius:8px;
    border:none;
    outline:none;
    background:rgba(255,255,255,0.1);
    color:#fff;
}

select {
    appearance:none;
    cursor:pointer;
}

.dropdown-wrapper {
    position:relative;
}

.dropdown-icon {
    position:absolute;
    right:12px;
    top:50%;
    transform:translateY(-50%);
    color:#00ffff;
    pointer-events:none;
}

select option {
    background:#1a1b26;
    color:#fff;
}

input:focus, select:focus {
    border:1px solid #00ffff;
    box-shadow:0 0 8px #00ffff;
}

button {
    width:100%;
    padding:12px;
    margin-top:10px;
    border:none;
    border-radius:8px;
    background:#00ffff;
    color:#000;
    font-weight:bold;
    cursor:pointer;
}

button:hover {
    box-shadow:0 0 15px #00ffff;
}

.error-message {
    text-align:center;
    color:#ff8080;
    margin-bottom:10px;
    font-size:13px;
}
</style>
</head>

<body>

<div class="container">

<a href="/category/list" class="back-btn">←</a>

<h2>Add Category</h2>

<c:if test="${not empty message}">
    <div class="error-message">
        ${message}
    </div>
</c:if>

<form:form method="post" action="/category/add" modelAttribute="categoryDto">

    <div class="form-group">
        <label>Category</label>
        <form:input
            path="identifier"
            placeholder="Enter category"
            required="true"
            minlength="2"
            maxlength="50"
            pattern="[A-Za-z0-9 ]+"
        />
    </div>

    <div class="form-group">
        <label>Super Category</label>

        <div class="dropdown-wrapper">
            <form:select path="superCategory">
                <form:option value="">-- Select Super Category --</form:option>

                <c:forEach var="c" items="${categories}">
                    <form:option value="${c.identifier}">
                        ${c.identifier}
                    </form:option>
                </c:forEach>

            </form:select>

            <span class="dropdown-icon">▼</span>
        </div>

    </div>

    <button type="submit">Add Category</button>

</form:form>

</div>

</body>
</html>