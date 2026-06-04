<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Role</title>

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

            --radius: 16px;
            --shadow: 0 20px 40px rgba(2,6,23,0.08);
        }

        * {
            font-family: 'Inter', sans-serif;
            box-sizing: border-box;
        }

        body {
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            background: var(--bg);
            padding: 20px;
            position: relative;
            color: var(--text);
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

        .container-box {
            width: 100%;
            max-width: 480px;
        }

        /* CARD */
        .card {
            padding: 28px;

            border-radius: var(--radius);

            background: var(--glass);
            backdrop-filter: blur(16px);

            border: 1px solid var(--border);
            box-shadow: var(--shadow);
        }

        h2 {
            text-align: center;
            font-size: 18px;
            font-weight: 600;
            margin-bottom: 20px;
        }

        /* FORM */
        label {
            font-size: 13px;
            color: var(--muted);
            margin-top: 12px;
            display: block;
        }

        .form-control {
            margin-top: 6px;
            border-radius: 10px;
            padding: 10px;
            border: 1px solid var(--border);
            font-size: 14px;
        }

        .form-control:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(37,99,235,0.15);
        }

        /* READONLY FIELD (better UX) */
        .readonly-field {
            background: #f1f5f9;
            color: var(--muted);
            border: 1px dashed var(--border);
            cursor: not-allowed;
        }

        /* BUTTON */
        .btn-update {
            margin-top: 20px;
            width: 100%;

            padding: 11px;
            border-radius: 10px;

            border: none;
            background: var(--primary);
            color: white;

            font-weight: 600;
            transition: 0.2s;
        }

        .btn-update:hover {
            background: var(--primary-hover);
            transform: translateY(-1px);
        }

        /* ERROR TEXT */
        .error-msg {
            color: var(--danger);
            text-align: center;
            font-size: 13px;
            margin-bottom: 12px;
        }
    </style>
</head>

<body>

<a href="${pageContext.request.contextPath}/role/list" class="back-arrow">
    ←
</a>

<div class="container-box">
    <div class="card">

        <h2>Edit Role</h2>

        <c:if test="${empty role}">
            <div class="error-msg">
                Role not found
            </div>
        </c:if>

        <c:if test="${not empty role}">

            <form:form action="${pageContext.request.contextPath}/role/update"
                       method="post"
                       modelAttribute="role">

                <form:hidden path="id"/>
                <form:hidden path="identifier"/>

                <label>Role Name</label>
                <input type="text"
                       class="form-control readonly-field"
                       value="${role.identifier}"
                       readonly />

                <label>Description</label>
                <form:input path="description"
                            cssClass="form-control"
                            required="true"/>

                <button type="submit" class="btn-update">
                    Update Role
                </button>

            </form:form>

        </c:if>

    </div>
</div>

</body>
</html>