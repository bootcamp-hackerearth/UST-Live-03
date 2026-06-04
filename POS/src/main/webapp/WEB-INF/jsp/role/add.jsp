<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Role</title>

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
            --danger-border: #fca5a5;

            --success: #16a34a;
            --success-bg: #dcfce7;
            --success-border: #86efac;

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
            justify-content: center;
            align-items: center;
            padding: 20px;
            position: relative;
        }

        /* BACK BUTTON */
        .back-arrow {
            position: absolute;
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
        .card {
            width: 440px;
            padding: 28px;

            border-radius: var(--radius);

            background: var(--glass);
            backdrop-filter: blur(16px);

            border: 1px solid var(--border);
            box-shadow: var(--shadow);
        }

        h5 {
            text-align: center;
            margin-bottom: 20px;
            font-weight: 600;
            color: var(--text);
        }

        /* FORM */
        label {
            font-size: 13px;
            color: var(--muted);
            margin-bottom: 4px;
            display: block;
        }

        .form-control {
            border-radius: 10px;
            border: 1px solid var(--border);
            padding: 10px;
            font-size: 14px;
        }

        .form-control:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(37,99,235,0.15);
        }

        /* BUTTON */
        .btn-submit {
            margin-top: 20px;
            width: 100%;

            background: var(--primary);
            color: white;

            border: none;
            border-radius: 10px;

            padding: 11px;
            font-weight: 600;

            transition: 0.2s;
        }

        .btn-submit:hover {
            background: var(--primary-hover);
            transform: translateY(-1px);
        }

        /* SERVER MESSAGE */
        .server-msg {
            text-align: center;
            padding: 10px;
            border-radius: 10px;
            margin-bottom: 14px;
            font-size: 13px;
        }

        .server-msg.error {
            background: var(--danger-bg);
            color: var(--danger);
            border: 1px solid var(--danger-border);
        }

        .server-msg.success {
            background: var(--success-bg);
            color: var(--success);
            border: 1px solid var(--success-border);
        }
    </style>
</head>

<body>

<a href="${pageContext.request.contextPath}/role/list" class="back-arrow">←</a>

<div class="card">

    <h5>Add Role</h5>

    <c:if test="${not empty message}">
        <div class="server-msg ${messageType}">
            ${message}
        </div>
    </c:if>

    <form:form modelAttribute="roleDto"
               method="post"
               action="${pageContext.request.contextPath}/role/add">

        <div class="mb-3">
            <label>Role Name</label>
            <form:input path="identifier" cssClass="form-control" required="true"/>
        </div>

        <div class="mb-3">
            <label>Description</label>
            <form:input path="description" cssClass="form-control" required="true"/>
        </div>

        <button class="btn-submit">
            Add Role
        </button>

    </form:form>

</div>

</body>
</html>