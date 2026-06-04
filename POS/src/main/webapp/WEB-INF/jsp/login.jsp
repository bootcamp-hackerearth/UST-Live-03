<!DOCTYPE html>
<html>
<head>
    <title>Login</title>

    <style>
        body {
            margin: 0;
            font-family: 'Segoe UI', sans-serif;
            background: #f2f4f8;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }

        .container {
            width: 750px;
            height: 420px;
            display: flex;
            border-radius: 10px;
            overflow: hidden;
            box-shadow: 0 10px 30px rgba(0,0,0,0.15);
            background: white;
        }

        .left {
            width: 50%;
            padding: 40px;
            display: flex;
            flex-direction: column;
            justify-content: center;
        }

        .left h2 {
            font-size: 18px;
            margin-bottom: 20px;
            color: #444;
        }

        input {
            width: 100%;
            padding: 10px;
            margin-bottom: 12px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 13px;
        }

        input:focus {
            border-color: #007bff;
            outline: none;
        }

        .btn {
            background: #007bff;
            color: white;
            padding: 10px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
            font-size: 13px;
        }

        .btn:hover {
            background: #0056b3;
        }

        .link {
            margin-top: 10px;
            font-size: 12px;
            text-align: center;
        }

        .link a {
            color: #007bff;
            text-decoration: none;
        }

        .right {
            width: 50%;
            background: #007bff;
            color: white;
            display: flex;
            justify-content: center;
            align-items: center;
            text-align: center;
        }

        .right h1 {
            font-size: 26px;
            line-height: 1.4;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="left">
        <h2>Sign In</h2>

        <form action="/login" method="post">
            <input type="text" name="username" placeholder="Username" required />
            <input type="password" name="password" placeholder="Password" required />
            <button class="btn">Login</button>
        </form>

        <div class="link">
            Don't have account? <a href="/register">Register</a>
        </div>
    </div>

    <div class="right">
        <h1>POS<br>Application</h1>
    </div>

</div>

</body>
</html>