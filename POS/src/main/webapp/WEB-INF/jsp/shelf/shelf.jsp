<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Shelf</title>

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Font -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">

    <style>
        :root {
            --primary: #2563eb;
            --primary-hover: #1e40af;

            --bg: #f8fafc;
            --glass: rgba(255,255,255,0.75);

            --text: #0f172a;
            --muted: #64748b;
            --border: #e2e8f0;

            --danger: #dc2626;
            --danger-bg: #fee2e2;
            --danger-border: #fecaca;

            --radius: 16px;
            --shadow: 0 20px 40px rgba(2,6,23,0.08);
        }

        * {
            font-family: 'Inter', sans-serif;
            box-sizing: border-box;
        }

        body {
            background: var(--bg);
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            padding: 40px 20px;
            color: var(--text);
        }

        /* BACK BUTTON */
        .back-arrow {
            position: fixed;
            top: 20px;
            left: 20px;
            width: 42px;
            height: 42px;
            display: flex;
            align-items: center;
            justify-content: center;
            border-radius: 50%;
            background: var(--glass);
            backdrop-filter: blur(10px);
            border: 1px solid var(--border);
            color: var(--text);
            text-decoration: none;
            font-size: 18px;
            box-shadow: var(--shadow);
            transition: transform .2s ease, box-shadow .2s ease, background .2s ease;
        }

        .back-arrow:hover {
            background: #eef2ff;
            color: var(--primary);
            transform: translate(-2px, -2px);
            box-shadow: 0 12px 28px rgba(37,99,235,.25);
        }

        /* CARD */
        .form-card {
            width: 460px;
            background: var(--glass);
            backdrop-filter: blur(16px);
            padding: 30px;
            border-radius: var(--radius);
            border: 1px solid var(--border);
            box-shadow: var(--shadow);
        }

        h2 {
            text-align: center;
            margin-bottom: 22px;
            font-weight: 600;
        }

        label {
            font-size: 13px;
            color: var(--muted);
            font-weight: 500;
            margin-bottom: 4px;
        }

        .form-control {
            border-radius: 10px;
            border: 1px solid var(--border);
            padding: 10px;
            font-size: 14px;
        }

        .form-control:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(37,99,235,0.2);
        }

        .btn-submit {
            margin-top: 22px;
            background: linear-gradient(135deg, var(--primary), var(--primary-hover));
            color: white;
            border-radius: 10px;
            padding: 11px;
            font-weight: 600;
            border: none;
            width: 100%;
            transition: .2s;
        }

        .btn-submit:hover {
            transform: translateY(-1px);
            box-shadow: 0 10px 25px rgba(37,99,235,0.3);
        }

        .server-msg {
            padding: 10px;
            border-radius: 10px;
            font-size: 13px;
            text-align: center;
            margin-bottom: 14px;
        }

        .server-msg.error {
            background: var(--danger-bg);
            border: 1px solid var(--danger-border);
            color: var(--danger);
        }
    </style>
</head>

<body>

<!-- BACK -->
<a href="${pageContext.request.contextPath}/shelf/list" class="back-arrow">←</a>

<div class="form-card">

    <h2>Edit Shelf</h2>

    <c:if test="${not empty message}">
        <div class="server-msg error">
            ${message}
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/shelf/update" method="post">

        <input type="hidden" name="id" value="${shelf.id}"/>

        <div class="mb-3">
            <label>Shelf Name</label>
            <input type="text"
                   name="identifier"
                   value="${shelf.identifier}"
                   class="form-control"
                   required/>
        </div>

        <button type="submit" class="btn-submit">
            Update Shelf
        </button>

    </form>

</div>

</body>
</html>