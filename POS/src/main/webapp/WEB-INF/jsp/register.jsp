<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>

<html>
<head>
<meta charset="UTF-8">
<title>User Registration</title>

<style>

* {
    box-sizing: border-box;
}

body {
    font-family: 'Segoe UI', Arial, sans-serif;
    background: linear-gradient(135deg, #eef2ff, #f8fafc);
    color: #1e293b;
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 100vh;
    margin: 0;
}

/* Card */
.container {
    background: #ffffff;
    padding: 30px;
    width: 420px;
    border-radius: 12px;
    border: 1px solid #e2e8f0;
    box-shadow: 0 10px 25px rgba(0,0,0,0.08);
}

/* Title */
h2 {
    text-align: center;
    margin-bottom: 22px;
    font-weight: 600;
    color: #0f172a;
}

/* Labels */
label {
    font-size: 13px;
    margin-bottom: 5px;
    color: #475569;
    display: block;
}

/* Inputs */
input {
    width: 100%;
    padding: 10px;
    margin-bottom: 14px;
    border-radius: 8px;
    border: 1px solid #cbd5f5;
    background-color: #f8fafc;
    color: #1e293b;
    font-size: 14px;
}

/* Focus */
input:focus {
    outline: none;
    border-color: #6366f1;
    box-shadow: 0 0 6px rgba(99,102,241,0.25);
    background-color: #ffffff;
}

/* Role list */
.role-list {
    border: 1px solid #e2e8f0;
    border-radius: 8px;
    padding: 6px;
    max-height: 150px;
    overflow-y: auto;
    margin-bottom: 18px;
    background: #f9fafb;
}

/* Checkbox RIGHT layout */
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

.role-item input[type="checkbox"] {
    justify-self: center;
}

/* Button */
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

/* Error message */
.error-msg {
    color: red;
    font-size: 13px;
    text-align: center;
    margin-bottom: 10px;
}

/* Link */
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

</style>

<script>
function validateRoles() {
    const checkboxes = document.querySelectorAll('input[name="roles"]');
    const errorDiv = document.getElementById("roleError");
    let checked = false;

    checkboxes.forEach(cb => {
        if (cb.checked) {
            checked = true;
        }
    });

    if (!checked) {
        errorDiv.innerText = "Select atleast one role";
        return false;
    }

    errorDiv.innerText = "";
    return true;
}
</script>

</head>

<body>

<div class="container">

<h2>Create Account</h2>

<!-- ERROR MESSAGE -->
<c:if test="${not empty message}">
    <div class="error-msg">${message}</div>
</c:if>

<form action="${pageContext.request.contextPath}/register" method="post" onsubmit="return validateRoles()">

    <label>Name</label>
    <input type="text" name="name" value="${userDto.name}" required>

    <label>Email</label>
    <input type="email" name="username" value="${userDto.username}" required>

    <label>Password</label>
    <input type="password"
           name="password"
           required
           pattern=".{6,}"
           title="Password must be at least 6 characters long">

    <label>Phone Number</label>
    <input type="text"
           name="phoneNo"
           value="${userDto.phoneNo}"
           required
           pattern="[0-9]{10}"
           title="Enter a valid 10-digit phone number"
           inputmode="numeric"
           oninput="this.value = this.value.replace(/[^0-9]/g, '')"
           placeholder="Enter a valid 10 digit Phone Number" />

    <label>Roles</label>

    <div class="role-list">
        <c:forEach var="r" items="${roles}">
            <div class="role-item">
                <span>${r.identifier}</span>
                <input type="checkbox"
                       name="roles"
                       value="${r.identifier}"
                       <c:if test="${not empty userDto.roles and userDto.roles.contains(r.identifier)}">checked</c:if> />
            </div>
        </c:forEach>
    </div>

    <!-- ROLE ERROR MESSAGE -->
    <div id="roleError" class="error-msg"></div>

    <button type="submit">Register</button>

    <a href="${pageContext.request.contextPath}/user/list" class="link-btn">
        View Users
    </a>

</form>

</div>

</body>
</html>