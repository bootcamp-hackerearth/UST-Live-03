<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>POS | Register User</title>

<style>
body {
    margin: 0;
    font-family: 'Segoe UI', sans-serif;
    background: white;
    min-height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
}

.card {
    width: 420px;
    background: rgba(255,255,255,0.95);
    border-radius: 18px;
    padding: 35px 40px;
    box-shadow: 0 20px 40px rgba(0,0,0,0.3);
}

.back {
    text-decoration: none;
    font-size: 13px;
    color: #0f766e;
    display: inline-block;
    margin-bottom: 10px;
}

.card h2 {
    text-align: center;
    margin-bottom: 20px;
    color: #0f172a;
    font-size: 22px;
}

.error-message {
    background: #fee2e2;
    color: #991b1b;
    padding: 10px;
    border-radius: 8px;
    font-size: 13px;
    text-align: center;
    margin-bottom: 15px;
}

label {
    font-size: 13px;
    font-weight: 600;
    color: #334155;
}

input, select {
    width: 100%;
    padding: 12px;
    margin-top: 8px;
    margin-bottom: 18px;
    border-radius: 10px;
    border: 1px solid #cbd5e1;
    font-size: 14px;
    outline: none;
    transition: 0.2s;
}

input:focus, select:focus {
    border-color: #0f766e;
    box-shadow: 0 0 0 2px rgba(15,118,110,0.2);
}

select[multiple] {
    height: 120px;
}

button {
    width: 100%;
    padding: 13px;
    border: none;
    border-radius: 25px;
    background: #0f172a;
    color: white;
    font-size: 15px;
    font-weight: bold;
    cursor: pointer;
    transition: 0.3s;
}

button:hover {
    transform: translateY(-2px);
    box-shadow: 0 12px 25px rgba(15,118,110,0.5);
}
</style>

<script>
function validateForm() {

    const name = document.getElementById("name").value.trim();
    const email = document.getElementById("username").value.trim();
    const roles = document.getElementById("roles").selectedOptions.length;
    const phone = document.getElementById("phoneNo").value.trim();
    const password = document.getElementById("password").value.trim();

    const gmailRegex = /^[a-zA-Z0-9._%+-]+@gmail\.com$/;
    const phoneRegex = /^[0-9]{10}$/;

    if (!name || !email || !phone || !password || roles === 0) {
        alert("Please fill all fields");
        return false;
    }

    if (!gmailRegex.test(email)) {
        alert("Email should be name@gmail.com");
        return false;
    }

    if (!phoneRegex.test(phone)) {
        alert("Mobile number must be 10 digits");
        return false;
    }

    return true;
}
</script>

</head>
<body>

<div class="card">

    <a href="${pageContext.request.contextPath}/user/list" class="back">← Back</a>

    <h2>User Registration</h2>

    <c:if test="${not empty message}">
        <div class="error-message">${message}</div>
    </c:if>

    <form:form action="register"
               method="post"
               modelAttribute="userDto"
               onsubmit="return validateForm();">

        <label>Name</label>
        <form:input path="name" id="name"/>

        <label>Email</label>
        <form:input path="username"
                    id="username"
                    pattern="^[a-zA-Z0-9._%+-]+@gmail\.com$"
                    title="Format: name@gmail.com"/>

        <label>Mobile Number</label>
        <form:input path="phoneNo"
                    id="phoneNo"
                    maxlength="10"
                    pattern="[0-9]{10}"
                    title="Enter 10-digit number"/>

        <label>Password</label>
        <form:password path="password" id="password" minlength="6"/>

        <label>Select Roles</label>
        <form:select path="roles" id="roles" multiple="true">
            <form:options items="${roles}"
                          itemValue="identifier"
                          itemLabel="identifier"/>
        </form:select>

        <button type="submit">Register</button>

    </form:form>
</div>
</body>
</html>