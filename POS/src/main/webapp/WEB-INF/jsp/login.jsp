<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>RetailPOS — Login</title>
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display:wght@700&family=Outfit:wght@300;400;500;600&display=swap" rel="stylesheet">
    <style>
        *, *::before, *::after { box-sizing: border-box; margin: 0; padding: 0; }

        :root {
            --bg:      #0d0d0d;
            --panel:   #161616;
            --gold:    #c9a84c;
            --gold2:   #e8c97a;
            --text:    #f0ece4;
            --muted:   #6b6860;
            --border:  #2a2a2a;
            --input:   #1e1e1e;
            --red:     #e05555;
        }

        body {
            font-family: 'Outfit', sans-serif;
            background: var(--bg);
            color: var(--text);
            min-height: 100vh;
            display: flex;
        }


        .left {
            width: 45%;
            position: relative;
            overflow: hidden;
            display: flex;
            flex-direction: column;
            justify-content: flex-end;
            padding: 52px 48px;
            background: #111;
        }


        .strips {
            position: absolute;
            top: 0; left: 0; right: 0; bottom: 0;
            display: flex;
            flex-direction: column;
            gap: 3px;
            opacity: .04;
            pointer-events: none;
        }
        .strip { background: white; flex-shrink: 0; }

        .left::before {
            content: '';
            position: absolute;
            top: -80px; right: -80px;
            width: 340px; height: 340px;
            background: radial-gradient(circle, #c9a84c40 0%, transparent 70%);
            border-radius: 50%;
        }

        .bg-num {
            position: absolute;
            top: 50%; left: 50%;
            transform: translate(-50%, -50%);
            font-family: 'Playfair Display', serif;
            font-size: 260px;
            color: rgba(201,168,76,.05);
            user-select: none;
            white-space: nowrap;
            pointer-events: none;
        }

        .left-content { position: relative; z-index: 2; }

        .tag {
            display: inline-flex;
            align-items: center;
            gap: 7px;
            background: rgba(201,168,76,.12);
            border: 1px solid rgba(201,168,76,.25);
            color: var(--gold);
            font-size: 11px;
            font-weight: 600;
            letter-spacing: 2px;
            text-transform: uppercase;
            padding: 6px 14px;
            border-radius: 100px;
            margin-bottom: 28px;
        }
        .tag::before {
            content: '';
            width: 6px; height: 6px;
            background: var(--gold);
            border-radius: 50%;
            animation: blink 1.4s ease infinite;
        }
        @keyframes blink {
            0%,100% { opacity: 1; }
            50%      { opacity: .2; }
        }

        .left h2 {
            font-family: 'Playfair Display', serif;
            font-size: clamp(32px, 3.5vw, 46px);
            font-weight: 700;
            line-height: 1.15;
            color: var(--text);
            margin-bottom: 18px;
        }
        .left h2 em {
            font-style: italic;
            color: var(--gold);
        }

        .left p {
            font-size: 14px;
            font-weight: 300;
            color: var(--muted);
            line-height: 1.75;
            max-width: 280px;
            margin-bottom: 36px;
        }

        .metrics {
            display: flex;
            gap: 0;
            border-top: 1px solid var(--border);
            padding-top: 28px;
        }
        .metric {
            flex: 1;
            padding-right: 20px;
        }
        .metric + .metric {
            padding-left: 20px;
            border-left: 1px solid var(--border);
        }
        .metric-val {
            font-family: 'Playfair Display', serif;
            font-size: 26px;
            color: var(--gold2);
        }
        .metric-label {
            font-size: 11px;
            color: var(--muted);
            margin-top: 3px;
            letter-spacing: .5px;
        }


        .right {
            flex: 1;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 48px 40px;
            background: var(--panel);
            position: relative;
        }
        .right::before {
            content: '';
            position: absolute;
            top: 0; left: 0; right: 0;
            height: 3px;
            background: linear-gradient(90deg, var(--gold), var(--gold2), transparent);
        }

        .form-box {
            width: 100%;
            max-width: 360px;
            animation: fadeUp .5s ease both;
        }
        @keyframes fadeUp {
            from { opacity: 0; transform: translateY(20px); }
            to   { opacity: 1; transform: translateY(0); }
        }

        .form-top {
            margin-bottom: 36px;
        }
        .logo-row {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 28px;
        }
        .logo-icon {
            width: 36px; height: 36px;
            background: linear-gradient(135deg, var(--gold), var(--gold2));
            border-radius: 8px;
            display: flex; align-items: center; justify-content: center;
        }
        .logo-icon svg { width: 18px; height: 18px; fill: #0d0d0d; }
        .logo-text {
            font-family: 'Playfair Display', serif;
            font-size: 18px;
            color: var(--text);
        }
        .logo-text span { color: var(--gold); }

        .form-top h3 {
            font-size: 22px;
            font-weight: 600;
            color: var(--text);
            margin-bottom: 6px;
        }
        .form-top p {
            font-size: 13px;
            color: var(--muted);
            font-weight: 300;
        }

        .field { margin-bottom: 18px; }

        .field label {
            display: block;
            font-size: 11px;
            font-weight: 600;
            letter-spacing: 1.5px;
            text-transform: uppercase;
            color: var(--muted);
            margin-bottom: 8px;
        }

        .field input {
            width: 100%;
            background: var(--input);
            border: 1.5px solid var(--border);
            border-radius: 9px;
            padding: 12px 14px;
            font-family: 'Outfit', sans-serif;
            font-size: 14px;
            color: var(--text);
            outline: none;
            transition: border-color .2s, box-shadow .2s;
        }
        .field input::placeholder { color: #3a3a3a; }
        .field input:focus {
            border-color: var(--gold);
            box-shadow: 0 0 0 3px rgba(201,168,76,.12);
        }

        .hint-row {
            display: flex;
            justify-content: flex-end;
            margin-bottom: 24px;
        }
        .hint-row a {
            font-size: 12px;
            color: var(--gold);
            text-decoration: none;
            font-weight: 500;
        }
        .hint-row a:hover { text-decoration: underline; }

        .btn {
            width: 100%;
            padding: 13px;
            background: linear-gradient(135deg, var(--gold) 0%, var(--gold2) 100%);
            color: #0d0d0d;
            font-family: 'Outfit', sans-serif;
            font-size: 14px;
            font-weight: 700;
            letter-spacing: 1px;
            text-transform: uppercase;
            border: none;
            border-radius: 9px;
            cursor: pointer;
            transition: opacity .2s, transform .15s;
        }
        .btn:hover  { opacity: .88; transform: translateY(-1px); }
        .btn:active { transform: translateY(0); }

        .register {
            text-align: center;
            margin-top: 20px;
            font-size: 13px;
            color: var(--muted);
        }
        .register a {
            color: var(--gold);
            font-weight: 500;
            text-decoration: none;
        }
        .register a:hover { text-decoration: underline; }

        .error {
            background: rgba(224,85,85,.12);
            border: 1px solid rgba(224,85,85,.3);
            color: #f08080;
            font-size: 13px;
            padding: 10px 14px;
            border-radius: 8px;
            margin-bottom: 18px;
            display: flex;
            align-items: center;
            gap: 8px;
        }
        .error::before {
            content: '✕';
            font-size: 11px;
            background: rgba(224,85,85,.25);
            color: var(--red);
            width: 20px; height: 20px;
            border-radius: 50%;
            display: flex; align-items: center; justify-content: center;
            flex-shrink: 0;
            font-weight: 700;
        }

        @media (max-width: 680px) {
            .left { display: none; }
            .right { background: var(--bg); }
        }
    </style>
</head>
<body>

<div class="left">
    <div class="strips">
        <c:forEach begin="1" end="120">
            <div class="strip" style="height:${Math.random()>0.7 ? 6 : 2}px"></div>
        </c:forEach>
    </div>
    <div class="bg-num">₹</div>

    <div class="left-content">
        <div class="tag">Live System</div>
        <h2>Your store,<br><em>fully in control.</em></h2>
        <p>Manage billing, inventory and daily sales from one seamless point-of-sale platform.</p>
        <div class="metrics">
            <div class="metric">
                <div class="metric-val">₹2.4L</div>
                <div class="metric-label">Today's Revenue</div>
            </div>
            <div class="metric">
                <div class="metric-val">847</div>
                <div class="metric-label">Transactions</div>
            </div>
        </div>
    </div>
</div>
<div class="right">
    <div class="form-box">

        <div class="form-top">
            <div class="logo-row">
                <div class="logo-icon">
                    <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                        <path d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-1.5 7h13L17 13M9 21a1 1 0 100-2 1 1 0 000 2zm10 0a1 1 0 100-2 1 1 0 000 2z"/>
                    </svg>
                </div>
                <div class="logo-text">Retail<span>POS</span></div>
            </div>
            <h3>Welcome back</h3>
            <p>Sign in to continue to your dashboard</p>
        </div>

        <c:if test="${param.error == 'true'}">
            <div class="error">Invalid username or password.</div>
        </c:if>

        <form action="${pageContext.request.contextPath}/login" method="post">

            <div class="field">
                <label>Email</label>
                <input type="email" name="username" placeholder="you@store.com" required />
            </div>

            <div class="field">
                <label>Password</label>
                <input type="password" name="password" placeholder="••••••••" required />
            </div>

            <div class="hint-row">
                <a href="${pageContext.request.contextPath}/forgot-password">Forgot password?</a>
            </div>

            <button type="submit" class="btn">Sign In</button>
        </form>

        <div class="register">
            New here? <a href="${pageContext.request.contextPath}/register">Create an account</a>
        </div>

    </div>
</div>

</body>
</html>
