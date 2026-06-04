<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Edit Profile</title>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }

body {
    font-family: 'Segoe UI', Arial, sans-serif;
    background: linear-gradient(135deg, #eef2ff, #f8fafc);
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    color: #1e293b;
}

.edit-card {
    width: 420px;
    background: #ffffff;
    padding: 28px;
    border-radius: 12px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 10px 25px rgba(0,0,0,0.08);
}

h2 {
    text-align: center;
    margin-bottom: 20px;
    font-weight: 600;
    color: #0f172a;
}

label {
    font-size: 13px;
    margin-bottom: 5px;
    display: block;
    color: #475569;
}

input, select {
    width: 100%;
    padding: 10px;
    margin-bottom: 14px;
    border-radius: 8px;
    border: 1px solid #cbd5f5;
    background-color: #f8fafc;
    font-size: 14px;
}

input:focus, select:focus {
    outline: none;
    border-color: #6366f1;
    box-shadow: 0 0 6px rgba(99,102,241,0.25);
    background-color: #ffffff;
}

.role-list {
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    padding: 6px;
    max-height: 150px;
    overflow-y: auto;
    margin-bottom: 18px;
    background: #f9fafb;
}

.role-item {
    display: grid;
    grid-template-columns: 1fr 30px;
    align-items: center;
    padding: 8px 6px;
    font-size: 14px;
    border-radius: 6px;
}

.role-item:hover {
    background-color: #eef2ff;
}

button {
    width: 100%;
    padding: 12px;
    border: none;
    border-radius: 8px;
    background: linear-gradient(135deg, #6366f1, #4f46e5);
    color: white;
    font-weight: 600;
    cursor: pointer;
}

button:hover {
    box-shadow: 0 5px 15px rgba(99,102,241,0.3);
}

.error {
    text-align: center;
    color: #ef4444;
    font-weight: 600;
}

.link-btn {
    display: block;
    text-align: center;
    margin-top: 15px;
    color: #4f46e5;
    text-decoration: none;
    font-size: 13px;
}

.link-btn:hover {
    text-decoration: underline;
}

.role-error {
    color: #ef4444;
    font-size: 13px;
    margin-top: -10px;
    margin-bottom: 12px;
}

</style>
</head>

<script>
function validateRoles() {

    const checkedRoles = document.querySelectorAll('input[type="checkbox"]:checked');
    const roleError = document.getElementById("roleError");

    roleError.innerText = "";

    if (checkedRoles.length === 0) {
        roleError.innerText = "Select at least one role";
        return false;
    }

    return true;
}
</script>


<body>

<div class="edit-card">

<h2>Edit Profile</h2>

<c:if test="${not empty user}">

<form:form action="${pageContext.request.contextPath}/user/update"
           method="post"
           modelAttribute="user"
           onsubmit="return validateRoles()">

    <form:hidden path="id"/>

    <label>Name</label>
    <form:input path="name"
                required="true"/>

    <label>Email</label>
    <form:input path="username"
                type="email"
                required="true"
                pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,}$"
                title="Enter a valid email address"/>

    <label>Phone</label>
    <form:input path="phoneNo"
                required="true"
                pattern="[0-9]{10}"
                title="Enter a valid 10-digit phone number"
                oninput="this.value = this.value.replace(/[^0-9]/g, '')"/>

    <label> Roles </label>

    <div class="role-list">
        <c:forEach var="r" items="${roles}">
            <div class="role-item">
                <span>${r.identifier}</span>
                <form:checkbox path="roles" value="${r.identifier}" />
            </div>
        </c:forEach>
    </div>

    <div id="roleError" class="role-error"></div>

    <button type="submit">Save</button>

     <a href="${pageContext.request.contextPath}/user/list" class="link-btn">
            View Users
        </a>

</form:form>

</c:if>

<c:if test="${not empty message}">
    <p class="error">${message}</p>
</c:if>

</div>

</body>
</html>