<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<html>
<head>
<title>Login</title>

<style>
body {
    margin: 0;
    height: 100vh;
    font-family: Arial, sans-serif;
    background: #F6F7F9;
    display: flex;
}

.left-panel {
    width: 55%;
    background: #2B2B2B;
    color: white;
    display: flex;
    flex-direction: column;
    justify-content: center;
    padding: 70px;
}

.right-panel {
    width: 45%;
    display: flex;
    justify-content: center;
    align-items: center;
}

form {
    width: 340px;
    padding: 30px;
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

input {
    width: 100%;
    padding: 10px;
    margin-bottom: 12px;
    border: 1px solid #E5E7EB;
    border-radius: 8px;
}

input:focus {
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

.error-box {
    background: #FEE2E2;
    color: #B91C1C;
    padding: 10px;
    border-radius: 8px;
    text-align: center;
}

a {
    color: #2B2B2B;
    text-decoration: none;
    font-weight: 600;
}
</style>
</head>

<body>

<div class="left-panel">
    <h1>POS Management System</h1>
    <p>Secure, fast and structured enterprise system for managing users, roles and nodes.</p>
</div>

<div class="right-panel">

<form method="post">

<h2>Login</h2>

<c:if test="${param.error != null}">
    <div class="error-box">Invalid username or password</div>
</c:if>

<label>Username</label>
<input type="email" name="username" required="true" pattern="^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"/>

<label>Password</label>
<input type="password" name="password" required="true"/>

<button type="submit">Login</button>

<div style="text-align:center;margin-top:12px;">
New user? <a href="/register">Register</a>
</div>

</form>

</div>

</body>
</html>
