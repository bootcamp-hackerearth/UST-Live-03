<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Node</title>

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

            --success: #16a34a;
            --success-bg: #dcfce7;
            --success-border: #86efac;

            --radius: 16px;
            --shadow: 0 20px 40px rgba(2,6,23,0.08);
        }

        * {
            font-family:'Inter',sans-serif;
            box-sizing:border-box;
        }

        body {
            background: var(--bg);
            min-height:100vh;
            display:flex;
            justify-content:center;
            align-items:center;
            padding:40px 20px;
            color:var(--text);
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
            transition: 0.2s;
        }

        .back-arrow:hover {
            background: #eef2ff;
            color: var(--primary);
        }

        /* CARD */
        .form-card {
            width: 520px;

            background: var(--glass);
            backdrop-filter: blur(16px);

            padding: 30px;
            border-radius: var(--radius);

            border: 1px solid var(--border);
            box-shadow: var(--shadow);
        }

        h2 {
            text-align:center;
            margin-bottom:22px;
            font-weight:600;
        }

        /* FORM */
        label {
            font-size:13px;
            color:var(--muted);
            font-weight:500;
            margin-bottom:4px;
        }

        .form-control {
            border-radius:10px;
            border:1px solid var(--border);
            padding:10px;
            font-size:14px;
        }

        .form-control:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(37,99,235,0.2);
        }

        select[multiple] {
            height:120px;
        }

        /* BUTTON */
        .btn-submit {
            margin-top:22px;
            background: linear-gradient(135deg, var(--primary), var(--primary-hover));
            color:white;
            border-radius:10px;
            padding:11px;
            font-weight:600;
            border:none;
            width:100%;
            transition: 0.2s;
        }

        .btn-submit:hover {
            transform: translateY(-1px);
            box-shadow: 0 10px 25px rgba(37,99,235,0.3);
        }

        /* SERVER MSG */
        .server-msg {
            padding:10px;
            border-radius:10px;
            font-size:13px;
            text-align:center;
            margin-bottom:14px;
        }

        .server-msg.error {
            background: var(--danger-bg);
            border:1px solid var(--danger-border);
            color: var(--danger);
        }

        .server-msg.success {
            background: var(--success-bg);
            border:1px solid var(--success-border);
            color: var(--success);
        }
    </style>
</head>

<body>

<a href="${pageContext.request.contextPath}/node/list" class="back-arrow">←</a>

<div class="form-card">

    <h2>Add Node</h2>

    <c:if test="${not empty message}">
        <div class="server-msg ${messageType}">
            ${message}
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/node/add" method="post">

        <div class="mb-3">
            <label>Identifier</label>
            <input type="text"
                   name="identifier"
                   value="${nodeDto.identifier}"
                   class="form-control"
                   required>
        </div>

        <div class="mb-3">
            <label>Path</label>
            <input type="text"
                   name="path"
                   value="${nodeDto.path}"
                   class="form-control"
                   required>
        </div>

        <div class="mb-3">
            <label>Roles</label>
            <select name="roles" multiple class="form-control" required>
                <c:forEach items="${roles}" var="role">
                    <option value="${role.identifier}">
                        ${role.identifier}
                    </option>
                </c:forEach>
            </select>
        </div>

        <button type="submit" class="btn-submit">
            Save Node
        </button>

    </form>

</div>

</body>
</html>