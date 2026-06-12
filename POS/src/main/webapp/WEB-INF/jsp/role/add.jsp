<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Add Role</title>

<style>

* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
}

body {
    font-family: 'Segoe UI', Arial, sans-serif;
    background: linear-gradient(135deg, #eef2ff, #f8fafc);
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    color: #1e293b;
}

/* Card */
.container {
    width: 400px;
    background: #ffffff;
    padding: 28px;
    border-radius: 12px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 10px 25px rgba(0,0,0,0.08);
}

/* Title */
h2 {
    text-align: center;
    margin-bottom: 20px;
    font-weight: 600;
    color: #0f172a;
}

/* Input group */
.input-group {
    margin-bottom: 16px;
}

/* Label */
label {
    font-size: 13px;
    color: #475569;
    display: block;
    margin-bottom: 5px;
}

/* Input */
input {
    width: 100%;
    padding: 10px;
    border-radius: 8px;
    border: 1px solid #cbd5f5;
    background: #f8fafc;
    color: #1e293b;
}

input:focus {
    outline: none;
    border-color: #6366f1;
    box-shadow: 0 0 6px rgba(99,102,241,0.25);
    background: #ffffff;
}

/* Button */
button {
    width: 100%;
    padding: 12px;
    margin-top: 10px;
    border-radius: 8px;
    border: none;
    background: linear-gradient(135deg, #6366f1, #4f46e5);
    color: white;
    font-weight: 600;
    cursor: pointer;
}

button:hover {
    box-shadow: 0 5px 15px rgba(99,102,241,0.3);
}

/* Message */
.success-msg {
    margin-bottom: 12px;
    padding: 10px;
    text-align: center;
    border-radius: 6px;
    background: #dcfce7;
    color: #166534;
    font-size: 13px;
}

.footer {
    margin-top: 15px;
    text-align: center;
}

.footer a {
    text-decoration: none;
    font-size: 14px;
    color: #4f46e5;
}

.footer a:hover {
    text-decoration: underline;
}

.error-msg {
    margin-bottom: 12px;
    padding: 10px;
    text-align: center;
    border-radius: 6px;
    background: #fee2e2;
    color: #b91c1c;
    font-size: 13px;
}

</style>
</head>

<body>

<div class="container">

    <h2>Add New Role</h2>

    <c:if test="${not empty error}">
        <div class="error-msg">
            ${error}
        </div>
    </c:if>

    <form:form method="post"
               action="/role/add"
               modelAttribute="roleDto">

        <div class="input-group">
            <label>Role Name</label>
            <form:input path="identifier"
                        placeholder="Enter role name" required="true" />
        </div>

        <div class="input-group">
            <label>Description</label>
            <form:input path="description"
                        placeholder="Enter role description"
                        required="true" />
        </div>

        <button type="submit">
            Add Role
        </button>

    </form:form>

    <div class="footer">
        <a href="${pageContext.request.contextPath}/role/list">
            Back to Roles
        </a>
    </div>

</div>

</body>
</html>