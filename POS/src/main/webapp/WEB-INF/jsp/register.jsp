<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>User Registration</title>

<style>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: 'Poppins', sans-serif;
}

body {
    min-height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
    background: linear-gradient(135deg, #1a1b26, #2a2b3d);
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

h5 {
    text-align: center;
    margin-bottom: 20px;
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

select[multiple] {
    height: 110px;
}

.form-text {
    font-size: 11px;
    color: #aaa;
}

.btn-primary {
    width: 100%;
    padding: 12px;
    border-radius: 8px;
    border: none;
    background: #00ffff;
    color: #000;
    font-weight: bold;
    cursor: pointer;
}

.btn-primary:hover {
    box-shadow: 0 0 15px #00ffff,
                0 0 30px #00ffff;
}

.alert {
    text-align: center;
    margin-top: 10px;
    padding: 10px;
    border-radius: 6px;
    color: #ff8080;
    font-size: 13px;
}

.footer {
    text-align: center;
    margin-top: 10px;
    font-size: 12px;
    color: #aaa;
}
</style>
</head>

<body>

<div class="card">

    <h5>Create User Account</h5>

    <form:form action="register" method="post" modelAttribute="userDto">

        <label class="form-label">Name</label>
        <form:input
            path="name"
            cssClass="form-control"
            required="true"
            minlength="3"
            maxlength="50"
            pattern="[A-Za-z ]+"
            title="Enter valid name (only letters)"
        />

        <label class="form-label">Email</label>
        <form:input
            path="username"
            type="email"
            cssClass="form-control"
            required="true"
        />

        <label class="form-label">Roles</label>
        <form:select
            path="roles"
            multiple="true"
            cssClass="form-control"
            required="true"
        >
            <form:options
                items="${roles}"
                itemValue="identifier"
                itemLabel="identifier"
            />
        </form:select>

        <div class="form-text">
            Hold Ctrl (Windows/Linux) or Cmd (Mac) to select multiple roles
        </div>

        <label class="form-label">Phone Number</label>
        <form:input
            path="phoneNo"
            type="tel"
            cssClass="form-control"
            pattern="[0-9]{10}"
            maxlength="10"
            minlength="10"
            required="true"
            title="Enter a valid 10-digit number"
        />

        <label class="form-label">Password</label>
        <form:password
            path="password"
            cssClass="form-control"
            required="true"
            minlength="6"
            maxlength="20"
            pattern="^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d@$!%*?&]{6,20}$"
            title="Minimum 6 characters, include letters and numbers"
        />

        <button type="submit" class="btn-primary">
            Register User
        </button>

        <c:if test="${not empty message}">
            <div class="alert">
                ${message}
            </div>
        </c:if>

    </form:form>

    <div class="footer">
        POS Management System
    </div>

</div>

</body>
</html>