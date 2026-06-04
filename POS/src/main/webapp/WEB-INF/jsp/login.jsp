<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title>Login</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap" rel="stylesheet">

    <style>
        body {
            position: relative;
            margin: 0;
            font-family: 'Poppins', sans-serif;
            height: 100vh;
            overflow: hidden;
        }

        .main-container {
            display: flex;
            flex-direction: row-reverse;
            height: 100vh;
            position: relative;
        }

        .left-panel {
            flex: 1;
            background: linear-gradient(135deg, #dbeafe, #93c5fd);
            color: #1f3b3b;
            display: flex;
            flex-direction: column;
            justify-content: center;
            text-align: center;
            align-items: flex-end;
            padding: 70px;
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

        .btn {
            margin-top: 30px;
            display: inline-block;
            padding: 10px 18px;
            background: #2563eb;
            color: white;
            text-decoration: none;
            border-radius: 8px;
            font-weight: 500;
            width: fit-content;
        }

        .curve {
            position: absolute;
            top: 0;
            left: 50%;
            transform: translateX(-50%);
            height: 100%;
            width: 140px;
            z-index: 5;
            pointer-events: none;
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
            background: rgba(255, 255, 255, 0.9);
                backdrop-filter: blur(10px);
            padding: 40px;
            border-radius: 18px;
            box-shadow: 0 20px 50px rgba(0,0,0,0.15);
        }

        .form-box input {
            max-width: 334px;
        }

        .form-box h2 {
            text-align: center;
            margin-bottom: 20px;
            color: #2563eb;
            font-weight: 700;
            letter-spacing: 1px;
        }

        .form-box input {
            width: 100%;
            padding: 12px;
            margin-bottom: 12px;
            border-radius: 10px;
            border: 1px solid #ddd;
            outline: none;
        }

        .form-box input:focus {
            border-color: #3b82f6;
        }

        .form-box button {
            width: 100%;
            padding: 12px;
            border: none;
            border-radius: 10px;
            background: linear-gradient(135deg, #93c5fd, #3b82f6);
            box-shadow: 0 8px 20px rgba(37, 99, 235, 0.25);
            transition: all 0.2s ease;
            color: white;
            font-weight: 600;
            cursor: pointer;
        }

        .form-box button:hover {
            transform: translateY(-2px);
            box-shadow: 0 12px 25px rgba(37, 99, 235, 0.35);
        }

        .toast {
            position: fixed !important;
            bottom: 30px;
            left: 50%;

            transform: translateX(-50%);
            opacity: 0;

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

            animation: toastUp 0.5s ease forwards;
        }

        .toast::before {
            content: "";
            position: absolute;
            inset: -2px;
            border-radius: inherit;
            background: radial-gradient(
                circle at center,
                rgba(59, 130, 246, 0.25),
                transparent 70%
            );
            z-index: -1;
            filter: blur(12px);
        }

        @keyframes toastUp {
            from {
                opacity: 0;
                transform: translateX(-50%) translateY(60px);
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

<c:if test="${not empty message}">
    <div id="toast" class="toast">
        ${message}
    </div>
    <c:remove var="message" scope="session"/>
</c:if>

<div class="main-container">

    <div class="left-panel">
        <h1>POS Application.</h1>
        <p>Welcome Back</p>

        <a href="/register" class="btn">REGISTER</a>
    </div>

    <div class="curve">
        <svg viewBox="0 0 100 100" preserveAspectRatio="none">
            <path d="M100,0 H0 V100 H100 C40,80 40,20 100,0 Z" fill="#f7f7fb"></path>
        </svg>
    </div>

    <div class="right-panel">

        <div class="form-box">

            <h2>LOGIN</h2>

            <form action="login" method="post">

                <input type="text" name="username" placeholder="Email Address" required />
                <input type="password" name="password" placeholder="Password" required />

                <button type="submit">LOGIN</button>

            </form>

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

</body>
</html>