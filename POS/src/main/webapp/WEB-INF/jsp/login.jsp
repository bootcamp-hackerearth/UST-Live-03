<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: linear-gradient(135deg, #e6f0ff, #f5faff);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .container {
            background: #ffffff;
            width: 420px;
            padding: 32px;
            border-radius: 14px;
            box-shadow: 0 12px 32px rgba(0, 84, 166, 0.18);
            border-top: 6px solid #1d4ed8;
        }

        h2 {
            text-align: center;
            margin-bottom: 22px;
            color: #1d4ed8;
            font-size: 22px;
            font-weight: 700;
        }

        .form-group {
            margin-bottom: 16px;
        }

        label {
            font-size: 13px;
            font-weight: 600;
            color: #1e3a8a;
            display: block;
            margin-bottom: 6px;
        }

        input {
            width: 100%;
            padding: 10px;
            border-radius: 6px;
            border: 1px solid #bfdbfe;
            font-size: 14px;
        }

        input:focus {
            outline: none;
            border-color: #1d4ed8;
            box-shadow: 0 0 0 3px rgba(29, 78, 216, 0.18);
        }

        /*  ERROR MESSAGE */
        .error-msg {
            color: #b91c1c;
            background: #fee2e2;
            border: 1px solid #fecaca;
            padding: 8px;
            border-radius: 6px;
            margin-bottom: 14px;
            text-align: center;
            font-size: 12px;
            font-weight: 600;
        }

        .btn-group {
            display: flex;
            gap: 10px;
            margin-top: 22px;
        }

        .btn {
            flex: 1;
            padding: 8px;
            border-radius: 6px;
            font-size: 13px;
            font-weight: 600;
            cursor: pointer;
            border: none;
            text-align: center;
        }

        .btn-login {
            background: linear-gradient(135deg, #2563eb, #1d4ed8);
            color: #ffffff;
        }

        .btn-login:hover {
            background: linear-gradient(135deg, #1e40af, #1d4ed8);
        }

        .btn-register {
            background-color: #eff6ff;
            color: #1d4ed8;
            border: 1px solid #bfdbfe;
            text-decoration: none;
            line-height: 26px;
        }

        .btn-register:hover {
            background-color: #dbeafe;
        }

    </style>
</head>
<body>
<div class="container">
    <h2>Login</h2>
 <c:if test="${param.error == 'true'}">
     <div id="loginError" class="error-msg">
         Invalid username or password
     </div>
 </c:if>
    <form action="${pageContext.request.contextPath}/login" method="post">
        <div class="form-group">
            <label>Username</label>
            <input type="text"
                   name="username"
                   placeholder="Enter username"
                   required>
        </div>
        <div class="form-group">
            <label>Password</label>
            <input type="password"
                   name="password"
                   placeholder="Enter password"
                   required>
        </div>
        <div class="btn-group">
            <button type="submit" class="btn btn-login">
                Login
            </button>
            <a href="${pageContext.request.contextPath}/register"
               class="btn btn-register">
                Register
            </a>
        </div>
    </form>
</div>
</body>
</html>