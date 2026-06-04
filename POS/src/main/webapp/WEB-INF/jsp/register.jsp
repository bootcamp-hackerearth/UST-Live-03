<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>User Registration</title>

<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
      rel="stylesheet">

<style>

body {
    margin: 0;
    font-family: 'Poppins', sans-serif;
    min-height: 100vh;

    background: linear-gradient(135deg, #eef2ff, #e0e7ff);

    display: flex;
    justify-content: center;
    align-items: center;
}

.back-btn {
    position: fixed;
    top: 18px;
    left: 18px;
    width: 40px;
    height: 40px;
    border-radius: 50%;
    background: white;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
    box-shadow: 0 6px 15px rgba(0,0,0,0.15);
}
.back-btn::before {
    content: '';
    width: 9px;
    height: 9px;
    border-left: 3px solid #333;
    border-bottom: 3px solid #333;
    transform: rotate(45deg);
}

.register-card {
    width: 380px;
    max-width: 85%;
    background: white;
    padding: 28px 30px;
    border-radius: 16px;
    box-shadow: 0 12px 30px rgba(0,0,0,0.12);
}

h2 {
    text-align: center;
    margin-bottom: 20px;
    font-size: 20px;
    color: #374151;
}

.form-group {
    margin-bottom: 14px;
}

label {
    font-size: 13px;
    font-weight: 500;
    color: #475569;
}

input {
    width: 100%;
    padding: 10px 12px;
    margin-top: 5px;
    border-radius: 8px;
    border: 1px solid #d1d5db;
    font-size: 13.5px;
    transition: 0.2s;
}

input:focus {
    border-color: #6366f1;
    box-shadow: 0 0 0 2px rgba(99,102,241,0.15);
    outline: none;
}

#roleList {
    border: 1px solid #d1d5db;
    border-radius: 8px;
    padding: 8px;
    max-height: 130px;
    overflow-y: auto;
    background: #f9fafb;
}

.role-item {
    padding: 5px 3px;
}

.role-item label {
    display: flex;
    align-items: center;
    gap: 8px;
    cursor: pointer;
}

.role-item input {
    width: 14px;
    height: 14px;
}

.btn-submit {
    margin-top: 18px;
    width: 100%;
    padding: 12px;
    background: linear-gradient(135deg, #6366f1, #4338ca);
    color: white;
    border: none;
    border-radius: 10px;
    font-size: 15px;
    font-weight: 600;
    cursor: pointer;
    transition: 0.25s;
}

.btn-submit:hover {
    transform: translateY(-1px);
    box-shadow: 0 6px 15px rgba(99,102,241,0.25);
}

.error-msg {
    color: red;
    text-align: center;
    margin-bottom: 10px;
}

.error {
    color: red;
    font-size: 12px;
    display: none;
}

</style>

<script>
function validateRoles() {
    const roles = document.querySelectorAll('input[name="roles"]');
    const roleError = document.getElementById('roleError');
    let selected = false;

    roles.forEach(r => {
        if (r.checked) selected = true;
    });

    if (!selected) {
        roleError.style.display = 'block';
        return false;
    } else {
        roleError.style.display = 'none';
        return true;
    }
}
</script>

</head>

<body>

<div class="back-btn" onclick="history.back()"></div>

<div class="register-card">

    <h2>User Registration</h2>

    <c:if test="${not empty message}">
        <div class="error-msg">${message}</div>
    </c:if>

    <form:form action="register"
               method="post"
               modelAttribute="userDto"
               onsubmit="return validateRoles();">

        <div class="form-group">
            <label>Name</label>
            <form:input path="name" placeholder="Enter name" required="true"/>
        </div>

        <div class="form-group">
            <label>Email</label>
            <form:input path="username"
                        type="email"
                        placeholder="Enter email"
                        pattern="^[a-zA-Z0-9._%+-]+@gmail\.com$"
                        required="true"/>
        </div>

        <div class="form-group">
            <label>Roles</label>
            <div id="roleList">
                <c:forEach var="role" items="${roles}">
                    <div class="role-item">
                        <label>
                            <input type="checkbox"
                                   name="roles"
                                   value="${role.identifier}"/>
                            ${role.identifier}
                        </label>
                    </div>
                </c:forEach>
            </div>
            <span class="error" id="roleError">Select at least one role</span>
        </div>

        <div class="form-group">
            <label>Phone Number</label>
            <form:input path="phoneNo"
                        pattern="[0-9]{10}"
                        maxlength="10"
                        placeholder="10 digit number"
                        required="true"/>
        </div>

        <div class="form-group">
            <label>Password</label>
            <form:password path="password"
                           minlength="6"
                           placeholder="Enter password"
                           required="true"/>
        </div>

        <input type="submit" value="Register" class="btn-submit"/>

    </form:form>

</div>

</body>
</html>