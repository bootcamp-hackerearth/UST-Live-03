<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login</title>

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Font -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">

    <style>
       :root {
           --bg: #ede9fe;
           --card: #ffffff;
           --text: #4c1d95;
           --muted: #6b7280;
           --primary: #7c3aed;
           --primary-hover: #6d28d9;
           --border: #ddd6fe;
           --radius: 14px;
           --shadow: 0 15px 35px rgba(76, 29, 149, 0.18);
       }

       * {
           font-family: 'Inter', Arial, sans-serif;
           box-sizing: border-box;
       }

       body {
           min-height: 100vh;
           display: flex;
           justify-content: center;
           align-items: center;
           background: linear-gradient(135deg, #ede9fe, #ddd6fe);
           margin: 0;
       }

       .login-card {
           width: 380px;
           background: var(--card);
           padding: 32px;
           border-radius: var(--radius);
           box-shadow: var(--shadow);
           border-top: 5px solid var(--primary);
       }

       h2 {
           text-align: center;
           margin-bottom: 22px;
           color: var(--primary);
           font-weight: 600;
       }

       label {
           font-size: 13px;
           color: var(--text);
           margin-bottom: 5px;
           display: block;
       }

       .form-control {
           border-radius: 10px;
           padding: 10px;
           border: 1px solid var(--border);
       }

       .form-control:focus {
           border-color: var(--primary);
           box-shadow: 0 0 0 3px rgba(124, 58, 237, 0.25);
       }

       .btn-login {
           width: 100%;
           padding: 10px;
           background: var(--primary);
           color: white;
           border-radius: 10px;
           border: none;
           font-weight: 600;
           margin-top: 10px;
       }

       .btn-login:hover {
           background: var(--primary-hover);
       }

       .register-text {
           text-align: center;
           margin-top: 14px;
           font-size: 13px;
           color: var(--muted);
       }

       .register-text a {
           color: var(--primary);
           font-weight: 600;
           text-decoration: none;
       }

       .register-text a:hover {
           text-decoration: underline;
       }
    </style>

    <script>
        function validateLoginForm() {
            let username = document.getElementsByName("username")[0].value.trim();
            let password = document.getElementsByName("password")[0].value.trim();

            if (username === "" || password === "") {
                alert("Please fill in all fields.");
                return false;
            }

            let emailPattern = /^[^ ]+@[^ ]+\.[a-z]{2,}$/;
            if (!username.match(emailPattern)) {
                alert("Enter a valid email address.");
                return false;
            }
            return true;
        }
    </script>
</head>

<body>

<div class="login-card">

    <h2>Login</h2>

    <c:if test="${not empty errorMsg}">
        <div class="alert alert-danger text-center py-2">
            ${errorMsg}
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/login"
          method="post"
          onsubmit="return validateLoginForm()">

        <div class="mb-3">
            <label>Email</label>
            <input type="email" name="username" class="form-control" required />
        </div>

        <div class="mb-3">
            <label>Password</label>
            <input type="password" name="password" class="form-control" required />
        </div>

        <button type="submit" class="btn-login">Login</button>
    </form>


    <div class="register-text">
        Don’t have an account?
        <a href="${pageContext.request.contextPath}/register">Register</a>
    </div>

</div>

</body>
</html>
