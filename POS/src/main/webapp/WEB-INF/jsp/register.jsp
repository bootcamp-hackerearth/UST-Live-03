<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
<title>User Registration</title>

<link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;600&display=swap"
      rel="stylesheet">

<style>
body {
    margin: 0;
    font-family: 'Inter', sans-serif;
    background: #d1d5db;
}

/* BACK ARROW */
.back-arrow {
    position: fixed;
    top: 20px;
    left: 20px;
    width: 45px;
    height: 45px;
    border-radius: 50%;
    background: #f1f5f9;
    display: flex;
    align-items: center;
    justify-content: center;
    font-size: 18px;
    text-decoration: none;
    color: #334155;
    box-shadow: 0 5px 15px rgba(0,0,0,0.1);
    transition: 0.25s;
}

.back-arrow:hover {
    background: #e2e8f0;
    transform: translateY(-2px);
}

/* CONTAINER */
.register-card {
    width: 430px;
    margin: 100px auto;
    background: #f1f5f9;
    padding: 35px 40px;
    border-radius: 16px;
    box-shadow: 0 10px 25px rgba(0,0,0,0.1);
}

/* TITLE */
h2 {
    text-align: center;
    margin-bottom: 20px;
    font-size: 22px;
    color: #0891b2;
    font-weight: 600;
}

/* ERROR MESSAGE */
.error-message {
    margin-bottom: 15px;
    padding: 10px 12px;
    background-color: #fee2e2;
    color: #b91c1c;
    border: 1px solid #fecaca;
    border-radius: 8px;
    font-size: 13px;
    text-align: center;
}

/* LABEL */
label {
    margin-top: 16px;
    display: block;
    font-weight: 600;
    font-size: 13px;
    color: #334155;
}

/* INPUT */
input, select {
    width: 100%;
    margin-top: 6px;
    padding: 10px;
    border: 1px solid #cbd5e1;
    border-radius: 8px;
    font-size: 13px;
    outline: none;
    transition: 0.2s;
    box-sizing: border-box; /* Ensures nested inputs stay perfectly within bounds */
}

input:focus, select:focus {
    border-color: #0891b2;
    box-shadow: 0 0 0 2px rgba(8,145,178,0.2);
}

select[multiple] {
    height: 130px;
}

/* BUTTON */
.btn-submit {
    margin-top: 28px;
    width: 100%;
    padding: 12px;
    background: linear-gradient(135deg, #0891b2, #0e7490);
    color: white;
    border: none;
    border-radius: 20px;
    font-weight: 600;
    cursor: pointer;
    transition: 0.25s;
}

.btn-submit:hover {
    background: linear-gradient(135deg, #0e7490, #075985);
    transform: translateY(-2px);
    box-shadow: 0 6px 15px rgba(8,145,178,0.4);
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

    if (!name || !email || roles === 0 || !phone || !password) {
        alert("Please fill all the details before registering");
        return false;
    }

    if (!gmailRegex.test(email)) {
        alert("Email must be in the format abc@gmail.com");
        return false;
    }

    return true;
}
</script>

</head>

<body>

<a href="${pageContext.request.contextPath}/user/list" class="back-arrow">
    &#8592;
</a>

<div class="register-card">

<h2>User Registration</h2>

<c:if test="${not empty message}">
    <div class="error-message">
        ${message}
    </div>
</c:if>

<form:form action="register"
           method="post"
           modelAttribute="userDto"
           onsubmit="return validateForm();">

    <label for="name">
        Name
        <form:input path="name" id="name" required="true"/>
    </label>

    <label for="username">
        Email
        <form:input
                path="username"
                id="username"
                required="true"
                pattern="^[a-zA-Z0-9._%+-]+@gmail\.com$"
                title="Email must be in the format name@gmail.com"/>
    </label>

    <label for="roles">
        Roles
        <form:select path="roles" id="roles" multiple="true" required="true">
            <form:options items="${roles}"
                          itemValue="identifier"
                          itemLabel="identifier"/>
        </form:select>
    </label>

    <div class="form-group">
        <label for="phoneNo">
            Mobile number
            <form:input path="phoneNo"
                        id="phoneNo"
                        type="tel"
                        pattern="[0-9]{10}"
                        maxlength="10"
                        required="true"
                        title="Enter a valid 10-digit mobile number"/>
        </label>
    </div>

    <label for="password">
        Password
        <form:password path="password" id="password" required="true"/>
    </label>

    <input type="submit" value="Register" class="btn-submit"/>

</form:form>

</div>

</body>
</html>