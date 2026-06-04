<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Login | POS</title>

    <style>
        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: 'Inter', 'Segoe UI', sans-serif;
            background: linear-gradient(135deg, #f6f8fb, #eef2f7);
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .container {
            width: 750px;
            height: 420px;
            display: flex;
            border-radius: 16px;
            overflow: hidden;
            background: #ffffff;
            box-shadow: 0 30px 80px rgba(0,0,0,0.08);
        }

        .left {
            width: 50%;
            padding: 48px 42px;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }

        .left h2 {
            font-size: 22px;
            margin-bottom: 6px;
            color: #1f2937;
        }

        .left p {
            font-size: 13px;
            color: #6b7280;
            margin-bottom: 22px;
        }

        input {
            width: 100%;
            padding: 12px 14px;
            margin-bottom: 14px;
            border-radius: 10px;
            border: 1px solid #d1d5db;
            font-size: 13px;
        }

        input:focus {
            outline: none;
            border-color: #6366f1;
            box-shadow: 0 0 0 2px rgba(99,102,241,0.15);
        }

        .btn {
            margin-top: 6px;
            width: 100%;
            padding: 12px;
            background: #4f46e5;
            color: #ffffff;
            border: none;
            border-radius: 10px;
            font-size: 14px;
            cursor: pointer;
        }

        .btn:hover {
            background: #4338ca;
        }

        .link {
            margin-top: 14px;
            font-size: 12px;
            text-align: center;
            color: #6b7280;
        }

        .link a {
            color: #4f46e5;
            text-decoration: none;
            font-weight: 500;
        }

        .right {
            width: 50%;
            background: linear-gradient(135deg, #4f46e5, #6366f1);
            color: #ffffff;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            text-align: center;
            padding: 40px;
        }

        .right .logo {
            width: 64px;
            height: 64px;
            border-radius: 16px;
            background: rgba(255,255,255,0.15);
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 26px;
            font-weight: 600;
            margin-bottom: 16px;
        }

        .right h1 {
            font-size: 26px;
            margin: 0;
            line-height: 1.3;
        }

        .right p {
            margin-top: 10px;
            font-size: 13px;
            opacity: 0.9;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="left">
        <h2>Welcome Back</h2>
        <p>Sign in to manage your POS system</p>

        <form action="/login" method="post">
            <input type="text" name="username" placeholder="Username" required />
            <input type="password" name="password" placeholder="Password" required />
            <button class="btn">Login</button>
        </form>

        <div class="link">
            Don't have an account? <a href="/register">Register</a>
        </div>
    </div>

    <div class="right">
        <div class="logo">POS</div>
        <h1>Point of Sale</h1>
        <p>Inventory | Sales | Billing</p>
    </div>

</div>

</body>
</html>