<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>User Registration</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            height: 100vh;
            overflow: hidden;
        }

        .main-container {
            display: flex;
            height: 100vh;
            position: relative;
        }

        .left-panel {
            flex: 1;
            background: linear-gradient(135deg, #cee9e9, #8fcfcd);
            color: #1f3b3b;
            display: flex;
            flex-direction: column;
            justify-content: center;
            padding: 80px;
            position: relative;
        }

        .left-panel h1 {
            font-size: 42px;
            font-weight: 700;
            margin: 0;
        }

        .left-panel p {
            font-size: 20px;
            margin-top: 20px;
            line-height: 1.5;
        }

        .login-btn {
            margin-top: 30px;
            display: inline-block;
            padding: 10px 18px;
            background: #4aa6a3;
            color: white;
            text-decoration: none;
            border-radius: 8px;
            font-weight: 500;
            width: fit-content;
        }

        .curve {
            position: absolute;
            top: 0;
            right: 0;
            height: 100%;
            width: 120px;
            z-index: 2;
        }

        .curve svg {
            height: 100%;
            width: 100%;
        }

        .right-panel {
            flex: 1;
            background: #f7f7fb;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .form-box {
            display: flex;
            flex-direction: column;
            align-items: center;
            width: 360px;
            background: white;
            padding: 40px;
            border-radius: 18px;
            box-shadow: 0 20px 50px rgba(0,0,0,0.15);
        }

        .form-box input[type="text"],
        .form-box input[type="number"],
        .form-box input[type="password"],
        .form-box input[type="email"] {
            max-width: 334px;
        }

        .form-box h2 {
            text-align: center;
            margin-bottom: 20px;
            color: #4aa6a3;
            font-weight: 700;
            letter-spacing: 1px;
        }

        .form-box input[type="text"],
        .form-box input[type="number"],
        .form-box input[type="password"],
        .form-box input[type="email"] {
            width: 100%;
            padding: 12px;
            margin-bottom: 12px;
            border-radius: 10px;
            border: 1px solid #ddd;
            outline: none;
            transition: 0.2s;
        }

        .form-box input:focus {
            border-color: #4aa6a3;
        }

        .form-box button {
            width: 100%;
            padding: 12px;
            border: none;
            border-radius: 10px;
            background: linear-gradient(135deg, #cee9e9, #4aa6a3);
            box-shadow: 0 12px 30px rgba(74, 166, 163, 0.35);
            transition: all 0.2s ease;
            color: white;
            font-weight: 600;
            cursor: pointer;
        }

        .form-box button:hover {
            transform: translateY(-2px);
            box-shadow:
                    0 12px 30px rgba(74, 166, 163, 0.35),
                    0 0 15px rgba(74, 166, 163, 0.2);
        }

        .checkbox {
            font-size: 12px;
            display: flex;
            gap: 6px;
            margin-bottom: 10px;
        }

        #messageBox {
            text-align: center;
            margin-bottom: 10px;
        }

        .roles-group {
            width: 100%;
            margin-bottom: 12px;
        }

        .roles-label {
            font-size: 12px;
            font-weight: 500;
            color: #aaa;
            margin-bottom: 8px;
            display: block;
            letter-spacing: 0.5px;
        }

        .checkbox-list {
            display: flex;
            flex-direction: column;
            gap: 7px;
        }

        .checkbox-item {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 10px 12px;
            border: 1px solid #ddd;
            border-radius: 10px;
            cursor: pointer;
            transition: border-color 0.2s, background 0.2s;
        }

        .checkbox-item:hover {
            border-color: #4aa6a3;
            background: #f0fafa;
        }

        .checkbox-item input[type="checkbox"] {
            width: 16px;
            height: 16px;
            min-width: 16px;
            margin: 0;
            padding: 0;
            accent-color: #4aa6a3;
            cursor: pointer;
        }

        .checkbox-item span {
            font-size: 13px;
            color: #444;
            font-weight: 500;
        }

        .checkbox-item:has(input:checked) {
            border-color: #4aa6a3;
            background: #eaf6f6;
        }

        .toast {
            position: fixed;
            bottom: 30px;
            left: 50%;
            transform: translateX(-50%) translateY(20px);
            min-width: 260px;
            max-width: 80%;
            padding: 14px 18px;
            border-radius: 14px;
            text-align: center;
            font-size: 14px;
            font-weight: 500;
            color: rgba(31, 59, 59, 0.9);
            background: rgba(255, 255, 255, 0.18);
            backdrop-filter: blur(18px) saturate(180%);
            -webkit-backdrop-filter: blur(18px) saturate(180%);
            background-image: linear-gradient(
                135deg,
                rgba(255, 255, 255, 0.25),
                rgba(255, 255, 255, 0.08)
            );
            border: 1px solid rgba(255, 255, 255, 0.35);
            box-shadow:
                0 12px 30px rgba(0, 0, 0, 0.12),
                inset 0 1px 0 rgba(255, 255, 255, 0.4);
            z-index: 9999;
            opacity: 0;
            animation: toastIn 0.4s ease forwards;
        }

        .toast::before {
            content: "";
            position: absolute;
            inset: -2px;
            border-radius: inherit;
            background: radial-gradient(
                circle at center,
                rgba(74, 166, 163, 0.25),
                transparent 70%
            );
            z-index: -1;
            filter: blur(12px);
        }

        @keyframes toastIn {
            from {
                opacity: 0;
                transform: translateX(-50%) translateY(20px);
            }
            to {
                opacity: 1;
                transform: translateX(-50%) translateY(0);
            }
        }

        .toast.hide {
            opacity: 0;
            transform: translateX(-50%) translateY(20px);
            transition: all 0.4s ease;
        }

        small {
            color: red;
        }
    </style>
</head>

<body>

<div class="main-container">

    <div class="left-panel">
        <h1>POS Application</h1>
        <p>Register to continue</p>

        <a href="/login" class="login-btn">LOG IN</a>

        <div class="curve">
            <svg viewBox="0 0 100 100" preserveAspectRatio="none">
                <path d="M0,0 H100 V100 H0 C60,80 60,20 0,0 Z" fill="#f7f7fb"></path>
            </svg>
        </div>
    </div>

    <div class="right-panel">

        <div class="form-box">

            <h2>SIGN UP</h2>

            <c:if test="${not empty message}">
                <div id="toast" class="toast">
                    ${message}
                </div>
            </c:if>

            <form:form action="register" method="post" modelAttribute="userDto">

                <form:input path="name" placeholder="Full Name" required="true"/>

                <form:input path="username"
                            type="email"
                            placeholder="Email Address"
                            required="true"
                            pattern="^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$"
                            title="Enter valid email like example@gmail.com"/>

                <div class="roles-group">
                    <span class="roles-label">SELECT ROLE(S)</span>
                    <div class="checkbox-list">
                        <c:forEach var="role" items="${roles}">
                            <label for="${role.identifier}" class="checkbox-item">
                                <form:checkbox path="roles" id="${role.identifier}" value="${role.identifier}"/>
                                <span>${role.identifier}</span>
                            </label>
                        </c:forEach>
                    </div>
                </div>

                <form:input path="phoneNo" type="number" placeholder="Phone Number" required="true"
                pattern="^[6-9][0-9]{9}$" title="Enter valid 10-digit Indian mobile number"/>

                <form:password path="password" placeholder="Password" required="true" minlength="6"/>

                <button type="submit">SIGN UP</button>

            </form:form>

        </div>

    </div>

</div>
<script>
window.addEventListener("DOMContentLoaded", function () {
    const toast = document.getElementById("toast");

    if (toast) {
        setTimeout(() => {
            toast.classList.add("hide");

            setTimeout(() => {
                toast.remove();
            }, 400);
        }, 3500);
    }
});
</script>
<script>
document.addEventListener("DOMContentLoaded", () => {

    const form = document.querySelector("form");

    form.addEventListener("submit", function (e) {

        document.querySelectorAll(".validation-error")
            .forEach(el => el.remove());

        const name = document.querySelector('input[name="name"]');
        const email = document.querySelector('input[name="username"]');
        const phone = document.querySelector('input[name="phoneNo"]');
        const password = document.querySelector('input[name="password"]');
        const roleCheckboxes =
            document.querySelectorAll('input[name="roles"]');

        function showError(element, message) {

            const small = document.createElement("small");
            small.className = "validation-error";
            small.innerText = message;

            element.parentNode.appendChild(small);

            element.focus();

            e.preventDefault();

            return false;
        }

        const nameRegex = /^[A-Za-z\s]+$/;

        if (name.value.trim().length < 3) {
            return showError(
                name,
                "Name must be at least 3 characters"
            );
        }

        if (!nameRegex.test(name.value.trim())) {
            return showError(
                name,
                "Name should contain only letters"
            );
        }

        const emailRegex =
            /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;

        if (!emailRegex.test(email.value.trim())) {
            return showError(
                email,
                "Enter valid email address"
            );
        }

        const roleSelected =
            [...roleCheckboxes].some(cb => cb.checked);

        if (!roleSelected) {

            const roleGroup =
                document.querySelector(".roles-group");

            return showError(
                roleGroup,
                "Select at least one role"
            );
        }

        const phoneRegex = /^[6-9][0-9]{9}$/;

        if (!phoneRegex.test(phone.value.trim())) {
            return showError(
                phone,
                "Enter valid 10-digit Indian mobile number"
            );
        }

        if (password.value.length < 6) {
            return showError(
                password,
                "Password must be at least 6 characters"
            );
        }

    });
});
</script>
</body>
</html>
