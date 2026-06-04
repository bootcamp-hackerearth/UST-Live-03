<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
<title>Register</title>

<style>
body {
    margin: 0;
    font-family: Arial;
    background: #F6F7F9;
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
}

.card {
    width: 380px;
    padding: 25px;
    background: #FFFFFF;
    border-radius: 12px;
    border: 1px solid #E5E7EB;
}

h2 {
    text-align: center;
    color: #111827;
}

label {
    font-weight: 600;
    color: #111827;
}

input, select {
    width: 100%;
    padding: 10px;
    border-radius: 8px;
    border: 1px solid #E5E7EB;
    margin-bottom: 10px;
}

input:focus, select:focus {
    border-color: #2B2B2B;
    outline: none;
}

button {
    width: 100%;
    padding: 10px;
    background: #2B2B2B;
    color: white;
    border: none;
    border-radius: 8px;
}

button:hover {
    background: #111111;
}

.msg {
    background: #DCFCE7;
    color: #166534;
    padding: 10px;
    border-radius: 8px;
    text-align: center;
}

.error {
    color: #B91C1C;
    font-size: 12px;
}

.back-btn {
    width: 100%;
    margin-top: 10px;
    padding: 10px;
    background: transparent;
    color: #2B2B2B;
    border: 1px solid #2B2B2B;
    border-radius: 8px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.2s ease;
}

.back-btn:hover {
    background: #2B2B2B;
    color: #FFFFFF;
}

</style>
</head>

<body>

<div class="card">

<h2>User Registration</h2>

<c:if test="${not empty message}">
<div class="msg">${message}</div>
</c:if>

<form:form method="post" action="register" modelAttribute="userDto">

<label>Name</label>
<form:input path="name" required="true"/>
<form:errors path="name" cssClass="error"/>

<label>Email</label>
<form:input path="username"
            type="email"
            required="true" />
<form:errors path="username" cssClass="error" />

<label>Roles</label>
<form:select path="roles" multiple="true">
    <form:options items="${roles}" itemValue="identifier" itemLabel="identifier"/>
</form:select>

<label>Phone</label>
<form:input path="phoneNo"
    inputmode="numeric"
    maxlength="10"
    pattern="^[0-9]{10}$"
    oninput="this.value=this.value.replace(/[^0-9]/g,'')"
    required="true"/>
<form:errors path="phoneNo" cssClass="error"/>

<label>Password</label>
        <form:password path="password"
                       required="true"
                       minlength="8"
                       pattern="(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,}"/>
        <form:errors path="password" cssClass="error"/>


    <button type="submit">Register</button>

<a href= "/user/list">
    <button type="button" class="back-btn">Back</button>
</a>

</form:form>

</div>
</body>
</html>
