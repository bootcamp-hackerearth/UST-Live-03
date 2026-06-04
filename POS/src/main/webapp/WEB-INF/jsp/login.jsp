<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>POS | Login</title>

<style>
body {
    margin: 0;
    font-family: "Segoe UI", Arial, sans-serif;
    background: white;
    height: 100vh;
    display: flex;
    justify-content: center;
    align-items: center;
}

.card {
    width: 380px;
    background: white;
    border-radius: 18px;
    padding: 35px 40px;
    box-shadow: 0 20px 40px rgba(0,0,0,0.3);
}

.card h2 {
    text-align: center;
    margin-bottom: 20px;
    color: #0f172a;
}

.error {
    background: #fee2e2;
    color: #991b1b;
    padding: 10px;
    border-radius: 8px;
    font-size: 13px;
    text-align: center;
    margin-bottom: 15px;
}

.success {
    background: #dcfce7;
    color: #166534;
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

input {
    width: 100%;
    padding: 12px;
    margin-top: 8px;
    margin-bottom: 18px;
    border-radius: 10px;
    border: 1px solid #cbd5e1;
}

input:focus {
    border-color: #0f766e;
    box-shadow: 0 0 0 2px rgba(15,118,110,0.2);
}

button {
    width: 100%;
    padding: 13px;
    border: none;
    border-radius: 25px;
    background: #0f172a;
    color: white;
    font-weight: bold;
    cursor: pointer;
}

button:hover {
    transform: translateY(-2px);
}

.register-link {
    text-align: center;
    margin-top: 15px;
    font-size: 13px;
    color: #475569;
}

.register-link a {
    color: #0f766e;
    font-weight: bold;
    text-decoration: none;
}
</style>
</head>
<body>

<div class="card">

    <h2>Login</h2>

    <c:if test="${param.error != null}">
        <div class="error">Invalid username or password</div>
    </c:if>

    <c:if test="${param.logout != null}">
        <div class="success">You have logged out successfully</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/login" method="post">

        <label>Email</label>
        <input type="text" name="username" required />

        <label>Password</label>
        <input type="password" name="password" required />

        <button type="submit">Login</button>
    </form>

    <div class="register-link">
        New user?
        <a href="${pageContext.request.contextPath}/register">Register here</a>
    </div>

</div>

</body>
</html>