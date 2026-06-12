<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Edit Role</title>

<style>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: 'Poppins', sans-serif;
}

body {
    min-height: 100vh;
    background: linear-gradient(135deg, #1a1b26, #2a2b3d);
    display: flex;
    justify-content: center;
    align-items: center;
}

.card {
    width: 420px;
    padding: 30px;
    border-radius: 15px;

    background: rgba(255,255,255,0.05);
    backdrop-filter: blur(12px);

    border: 1px solid rgba(255,255,255,0.2);
    box-shadow: 0 8px 32px rgba(0,0,0,0.4);
}

.card-header {
    text-align: center;
    margin-bottom: 20px;
}

.card-header h5 {
    color: #fff;
}

.form-label {
    font-size: 13px;
    color: #ccc;
}

.form-control {
    width: 100%;
    padding: 10px;
    margin-top: 6px;
    margin-bottom: 15px;

    border-radius: 8px;
    border: none;
    outline: none;

    background: rgba(255,255,255,0.1);
    color: #fff;
}

.form-control:focus {
    border: 1px solid #00ffff;
    box-shadow: 0 0 8px #00ffff;
}

.readonly {
    background: rgba(255,255,255,0.05);
    color: #aaa;
}

.btn {
    padding: 10px 14px;
    border-radius: 8px;
    text-decoration: none;
    font-size: 13px;
    cursor: pointer;
    border: none;
}

.btn-primary {
    background: #00ffff;
    color: #000;
}

.btn-primary:hover {
    box-shadow: 0 0 15px #00ffff;
}

.btn-outline-secondary {
    color: #ccc;
    border: 1px solid #ccc;
}

.btn-outline-secondary:hover {
    color: #fff;
    border-color: #fff;
}

.btn-group {
    display: flex;
    justify-content: space-between;
    margin-top: 15px;
}

.alert {
    text-align: center;
    padding: 10px;
    margin-bottom: 12px;
    border-radius: 6px;
    font-size: 13px;
}

.alert-info {
    color: #00ffcc;
}

.alert-danger {
    color: #ff8080;
}

.card-footer {
    text-align: center;
    margin-top: 10px;
    font-size: 12px;
    color: #aaa;
}
</style>
</head>

<body>

<div class="card">

    <div class="card-header">
        <h5>Edit Role</h5>
    </div>

    <c:if test="${not empty message}">
        <div class="alert alert-info">
            ${message}
        </div>
    </c:if>

    <c:if test="${empty role}">
        <div class="alert alert-danger">
            Role not found
        </div>
    </c:if>

    <c:if test="${not empty role}">

        <form:form action="/role/update"
                   method="post"
                   modelAttribute="role">

            <form:hidden path="id"/>
            <form:hidden path="identifier"/>

            <label class="form-label">Role Name</label>
            <input type="text"
                   class="form-control readonly"
                   value="${role.identifier}"
                   readonly>

            <label class="form-label">Description</label>
            <form:input path="description"
                        cssClass="form-control"
                        required="true"/>

            <div class="btn-group">
                <a href="/role/list" class="btn btn-outline-secondary">
                    Cancel
                </a>

                <button type="submit" class="btn btn-primary">
                    Update Role
                </button>
            </div>

        </form:form>

    </c:if>

    <div class="card-footer">
        POS Management System
    </div>

</div>

</body>
</html>