<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Warehouse</title>

    <!-- Bootstrap CSS -->
    <link
        href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
        rel="stylesheet"/>

    <!-- Google Font -->
    <link
        href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap"
        rel="stylesheet"/>

    <!-- CSRF -->
    <meta name="_csrf" content="${_csrf.token}"/>

    <style>
        :root {
            --primary:#2563eb;
            --primary-hover:#1e40af;
            --bg:#f8fafc;
            --glass:rgba(255,255,255,.78);
            --text:#0f172a;
            --muted:#64748b;
            --border:#e2e8f0;
            --radius:16px;
            --shadow:0 20px 40px rgba(2,6,23,.08);
        }

        * {
            box-sizing:border-box;
            font-family:'Inter',sans-serif;
        }

        body {
            background:var(--bg);
            min-height:100vh;
            display:flex;
            justify-content:center;
            align-items:center;
            padding:40px 20px;
        }

        /* BACK BUTTON */
        .back-arrow {
            position:fixed;
            top:20px;
            left:20px;
            width:42px;
            height:42px;
            display:flex;
            align-items:center;
            justify-content:center;
            border-radius:50%;
            background:var(--glass);
            backdrop-filter:blur(10px);
            border:1px solid var(--border);
            color:var(--text);
            text-decoration:none;
            font-size:18px;
            box-shadow:var(--shadow);
            transition:.25s;
        }

        .back-arrow:hover {
            background:#eef2ff;
            color:var(--primary);
            transform:translate(-2px,-2px);
        }

        /* CARD */
        .form-card {
            width:460px;
            padding:32px;
            background:var(--glass);
            border-radius:var(--radius);
            backdrop-filter:blur(16px);
            box-shadow:var(--shadow);
            animation:fadeUp .4s ease;
        }

        @keyframes fadeUp {
            from { opacity:0; transform:translateY(12px); }
            to   { opacity:1; transform:translateY(0); }
        }

        h4 {
            text-align:center;
            font-weight:600;
            margin-bottom:22px;
        }

        label {
            font-size:13px;
            color:var(--muted);
            margin-top:14px;
            font-weight:500;
        }

        .form-control {
            margin-top:6px;
            padding:10px 12px;
            border-radius:10px;
            border:1px solid var(--border);
        }

        .form-control:focus {
            border-color:var(--primary);
            box-shadow:0 0 0 3px rgba(37,99,235,.2);
        }

        .btn-submit {
            margin-top:28px;
            width:100%;
            padding:12px;
            border:none;
            border-radius:10px;
            background:linear-gradient(135deg,var(--primary),var(--primary-hover));
            color:white;
            font-weight:600;
        }

        .btn-submit:hover {
            transform:translateY(-2px);
            box-shadow:0 12px 26px rgba(37,99,235,.35);
        }
    </style>
</head>

<body>

<!-- BACK -->
<a href="${pageContext.request.contextPath}/warehouse/list" class="back-arrow">
    ←
</a>

<div class="form-card">

    <h4>Add Warehouse</h4>

    <c:if test="${not empty message}">
        <div class="alert alert-danger text-center">
            ${message}
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/warehouse/add" method="post">
        <input type="hidden" name="_csrf" value="${_csrf.token}"/>

        <label>Identifier</label>
        <input name="identifier" class="form-control" required/>

        <label>Name</label>
        <input name="name" class="form-control" required/>

        <label>Location</label>
        <input name="location" class="form-control" required/>

        <button type="submit" class="btn-submit">
            Save Warehouse
        </button>
    </form>

</div>

</body>
</html>