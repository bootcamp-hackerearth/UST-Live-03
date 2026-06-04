<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>User Registration</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap"
          rel="stylesheet">

    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Poppins', sans-serif;
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            background: #111;
            padding: 20px;
        }

        .back-btn {
            position: fixed;
            top: 20px;
            left: 20px;
            width: 42px;
            height: 42px;
            border-radius: 50%;
            background: #fff;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            transition: 0.3s ease;
        }

        .back-btn:hover {
            transform: scale(1.05);
        }

        .back-btn::before {
            content: '';
            width: 10px;
            height: 10px;
            border-left: 3px solid #111;
            border-bottom: 3px solid #111;
            transform: rotate(45deg);
            margin-left: 5px;
        }

        .register-card {
            width: 430px;
            background: #fff;
            padding: 35px 40px;
            border-radius: 16px;
            box-shadow: 0 15px 40px rgba(255, 255, 255, 0.08);
        }

        h2 {
            text-align: center;
            margin-bottom: 28px;
            color: #111;
            font-size: 28px;
            font-weight: 600;
        }

        .form-group {
            margin-bottom: 18px;
        }

        label {
            display: block;
            margin-bottom: 7px;
            font-size: 14px;
            font-weight: 500;
            color: #111;
        }

        input[type="text"],
        input[type="email"],
        input[type="tel"],
        input[type="password"] {
            width: 100%;
            padding: 12px 14px;
            border: 1px solid #ccc;
            border-radius: 10px;
            font-size: 14px;
            outline: none;
            transition: 0.3s ease;
            background: #f8f8f8;
        }

        input:focus {
            border-color: #000;
            background: #fff;
        }

        .error {
            color: red;
            font-size: 12px;
            margin-top: 5px;
            display: none;
        }

        #roleList {
            border: 1px solid #ccc;
            border-radius: 10px;
            padding: 10px;
            background: #f8f8f8;
        }

        .role-item {
            margin-bottom: 10px;
        }

        .role-item:last-child {
            margin-bottom: 0;
        }

        .role-item label {
            display: flex;
            align-items: center;
            gap: 10px;
            margin: 0;
        }

        .role-item input {
            width: 16px;
            height: 16px;
        }

        .btn-submit {
            width: 100%;
            padding: 13px;
            margin-top: 10px;
            border: none;
            border-radius: 10px;
            background: #111;
            color: #fff;
            font-size: 16px;
            font-weight: 600;
            cursor: pointer;
            transition: 0.3s ease;
        }

        .btn-submit:hover {
            background: #333;
        }

        @media (max-width: 500px) {
            .register-card {
                width: 100%;
                padding: 28px 22px;
            }
        }
    </style>

</head>

<body>

    <div class="back-btn" onclick="history.back()"></div>

    <div class="register-card">

        <h2>User Registration</h2>

        <form action="${pageContext.request.contextPath}/register"
              method="post"
              onsubmit="return validateForm();">

            <input type="hidden"
                   name="${_csrf.parameterName}"
                   value="${_csrf.token}" />

            <div class="form-group">
                <label>Name</label>
                <input type="text" id="name" name="name" placeholder="Enter full name">
                <span class="error" id="nameError">Name must contain only alphabets</span>
            </div>

            <div class="form-group">
                <label>Email</label>
                <input type="email" id="email" name="username" placeholder="Enter email">
                <span class="error" id="emailError">Enter valid email address</span>
            </div>
            <div class="form-group">
                <label>Roles</label>
                <div id="roleList">
                    <c:forEach var="role" items="${roles}">
                        <div class="role-item">
                            <label>
                                <input type="checkbox"
                                       name="roles"
                                       value="${role.identifier}" />
                                ${role.identifier}
                            </label>
                        </div>
                    </c:forEach>
                </div>
                <span class="error" id="roleError">
                    Select at least one role
                </span>
            </div>

            <div class="form-group">
                <label>Phone Number</label>
                <input type="tel" id="phone" name="phoneNo" placeholder="Enter 10 digit number">
                <span class="error" id="phoneError">Phone number must contain 10 digits</span>
            </div>

            <div class="form-group">
                <label>Password</label>
                <input type="password" id="password" name="password" placeholder="Enter password">
                <span class="error" id="passwordError">Password must contain minimum 6 characters</span>
            </div>

            <div class="form-group">
                <label>Confirm Password</label>
                <input type="password" id="confirmPassword" placeholder="Confirm password">
                <span class="error" id="confirmPasswordError">Passwords do not match</span>
            </div>

            <input type="submit" value="Register" class="btn-submit">

        </form>

    </div>

    <script>
        function showError(id) {
            document.getElementById(id).style.display = 'block';
        }

        function hideError(id) {
            document.getElementById(id).style.display = 'none';
        }

        function validateForm() {

            let valid = true;

            const name = document.getElementById('name').value.trim();
            const nameRegex = /^[A-Za-z ]+$/;

            if (name === '' || !nameRegex.test(name)) {
                showError('nameError');
                valid = false;
            } else {
                hideError('nameError');
            }

            const email = document.getElementById('email').value.trim();
            const emailRegex = /^[^ ]+@[^ ]+\.[a-z]{2,3}$/;

            if (email === '' || !emailRegex.test(email)) {
                showError('emailError');
                valid = false;
            } else {
                hideError('emailError');
            }

            const roles = document.querySelectorAll('input[name="roles"]');
            let roleSelected = false;

            roles.forEach(role => {
                if (role.checked) {
                    roleSelected = true;
                }
            });

            if (!roleSelected) {
                showError('roleError');
                valid = false;
            } else {
                hideError('roleError');
            }

            const phone = document.getElementById('phone').value.trim();
            const phoneRegex = /^[0-9]{10}$/;

            if (!phoneRegex.test(phone)) {
                showError('phoneError');
                valid = false;
            } else {
                hideError('phoneError');
            }

            const password = document.getElementById('password').value;

            if (password.length < 6) {
                showError('passwordError');
                valid = false;
            } else {
                hideError('passwordError');
            }

            const confirmPassword = document.getElementById('confirmPassword').value;

            if (password !== confirmPassword) {
                showError('confirmPasswordError');
                valid = false;
            } else {
                hideError('confirmPasswordError');
            }

            return valid;
        }
    </script>

</body>
</html>