<%@ page contentType="text/html;charset=UTF-8" %>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Category</title>

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
            justify-content: center;
            align-items: center;
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
            transition: all 0.25s ease;
        }

        .back-arrow:hover {
            background: #eef2ff;
            color: var(--primary);
            transform: translateX(-2px);
        }

        /* CARD */
        .form-card {
            width: 480px;
            background: var(--glass);
            backdrop-filter: blur(16px);
            padding: 32px;
            border-radius: var(--radius);
            border: 1px solid var(--border);
            box-shadow: var(--shadow);
            animation: fadeUp 0.45s ease;
        }

        @keyframes fadeUp {
            from {
                opacity: 0;
                transform: translateY(12px);
            }
            to {
                opacity: 1;
                transform: translateY(0);
            }
        }

        h2 {
            text-align: center;
            margin-bottom: 24px;
            font-weight: 600;
        }

        label {
            font-size: 13px;
            color: var(--muted);
            font-weight: 500;
            margin-bottom: 5px;
        }

        .form-control,
        .form-select {
            border-radius: 10px;
            border: 1px solid var(--border);
            padding: 10px;
            font-size: 14px;
        }

        .form-control:focus,
        .form-select:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(37,99,235,0.2);
        }

        .btn-submit {
            margin-top: 24px;
            background: linear-gradient(135deg, var(--primary), var(--primary-hover));
            color: white;
            border-radius: 10px;
            padding: 11px;
            font-weight: 600;
            border: none;
            width: 100%;
            transition: 0.2s;
        }

        .btn-submit:hover {
            transform: translateY(-1px);
            box-shadow: 0 12px 30px rgba(37,99,235,0.35);
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

<a href="${pageContext.request.contextPath}/category/list" class="back-arrow">←</a>

<div class="form-card">

    <h2>Add Category</h2>

    <c:if test="${not empty message}">
        <div class="server-msg error">
            ${message}
        </div>
    </c:if>

    <form:form
        action="${pageContext.request.contextPath}/category/add"
        method="post"
        modelAttribute="categoryDto">

        <!-- CATEGORY NAME -->
        <div class="mb-3">
            <label>Category Name</label>
            <form:input
                path="identifier"
                cssClass="form-control"
                required="true"/>
        </div>

        <!-- SUPER CATEGORY -->
        <div class="mb-3">
            <label>Super Category (Optional)</label>
            <form:select path="superCategory" cssClass="form-select">
                <form:option value="">-- No Super Category --</form:option>

                <c:forEach var="sc" items="${superCategories}">
                    <form:option value="${sc.identifier}">
                        ${sc.identifier}
                    </form:option>
                </c:forEach>
            </form:select>
        </div>

        <button type="submit" class="btn-submit">
            Add Category
        </button>

    </form:form>

</div>

</body>
</html>