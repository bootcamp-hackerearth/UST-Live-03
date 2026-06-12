<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<head>
    <meta charset="UTF-8">
    <title>Login</title>

  <style>
         body {
             margin: 0;
             min-height: 100vh;
             font-family: system-ui, -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif;
             background: linear-gradient(to bottom, #ffffff, #e5e5e5, #bbbbbb);
             display: flex;
             justify-content: center;
             align-items: center;
         }

         form {
             background: #ffffff;
             width: 320px;
             padding: 20px 22px;
             border: 1px solid #dcdcdc;
             border-radius: 6px;
         }

         h2 {
             margin: 0 0 16px 0;
             font-size: 18px;
             font-weight: 600;
             color: #222;
             text-align: center;
         }

         form div {
             margin-bottom: 12px;
         }

         label {
             display: block;
             font-size: 12px;
             font-weight: 500;
             color: #444;
             margin-bottom: 3px;
         }

         input {
             width: 100%;
             padding: 7px 8px;
             font-size: 13px;
             border-radius: 4px;
             border: 1px solid #cfcfcf;
             color: #222;
         }

         input:focus {
             outline: none;
             border-color: #4b6cb7;
         }

         button {
             margin-top: 8px;
             width: 100%;
             padding: 9px;
             background: #4b6cb7;
             color: #fff;
             border: none;
             border-radius: 4px;
             font-size: 13px;
             font-weight: 500;
             cursor: pointer;
         }

         button:hover {
             background: #3f5fa7;
         }

         .signup-text {
             margin-top: 10px;
             font-size: 12px;
             color: #555;
             text-align: center;
         }

         a {
             display: block;
             margin-top: 6px;
             padding: 8px;
             text-align: center;
             text-decoration: none;
             font-size: 13px;
             color: #4b6cb7;
             border: 1px solid #4b6cb7;
             border-radius: 4px;
         }

         a:hover {
             background: #4b6cb7;
             color: #fff;
         }

.error-box {
    background: #f8d7da;
    color: #721c24;
    font-size: 12px;
    padding: 8px;
    border: 1px solid #f5c6cb;
    border-radius: 4px;
    margin-bottom: 12px;
    text-align: center;
}

.page-wrapper {
    display: flex;
    width: 760px;
}

.pos-banner {
    width: 320px;
    background: linear-gradient(to bottom, #2f3b52, #1f2937);
    color: #fff;
    border-radius: 8px 0 0 8px;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;
    gap: 20px;
}

.pos-banner h1 {
    font-size: 22px;
    font-weight: 600;
    letter-spacing: 0.5px;
}

.banner-register {
    color: #ffffff;
    text-decoration: none;
    font-size: 13px;
    padding: 8px 18px;
    border: 1px solid rgba(255,255,255,0.5);
    border-radius: 4px;
}

.banner-register:hover {
    background: rgba(255,255,255,0.15);
}

     </style>

</head>
<body>


<div class="page-wrapper">


    <div class="pos-banner">
        <h1>POS Application</h1>
        <a href="/register" class="banner-register">Sign Up</a>
    </div>


<form th:action="/login" method="post">
    <h2>Login</h2>

<c:if test="${param.error != null}">
    <div class="error-box">
        Invalid username or password
    </div>
</c:if>

    <div>
        <label>Username:</label>
        <input type="text" name="username" required />
    </div>

    <div>
        <label>Password:</label>
        <input type="password" name="password" required />
    </div>

    <button type="submit">Login</button>

</form>
</div>

</body>
</html>