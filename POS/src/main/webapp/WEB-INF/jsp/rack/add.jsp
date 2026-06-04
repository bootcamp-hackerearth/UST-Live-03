<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Rack</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap"
          rel="stylesheet">

    <style>
        :root {
            --primary: #2563eb;
            --primary-hover: #1e40af;

            --bg: #f8fafc;
            --glass: rgba(255, 255, 255, 0.75);

            --text: #0f172a;
            --muted: #64748b;
            --border: #e2e8f0;

            --danger: #dc2626;
            --danger-bg: #fee2e2;

            --radius: 16px;
            --shadow: 0 20px 40px rgba(2, 6, 23, 0.08);
        }

        * {
            box-sizing: border-box;
            font-family: 'Inter', sans-serif;
        }

        body {
            display: flex;
            align-items: center;
            justify-content: center;

            min-height: 100vh;
            background: var(--bg);
        }

        .back-arrow {
            position: absolute;
            top: 20px;
            left: 20px;

            display: flex;
            align-items: center;
            justify-content: center;

            width: 42px;
            height: 42px;

            color: var(--text);
            text-decoration: none;

            background: var(--glass);
            border-radius: 50%;
        }

        .card {
            width: 520px;
            padding: 28px;

            background: var(--glass);
            border-radius: var(--radius);
            box-shadow: var(--shadow);
        }

        label {
            display: block;
            margin-top: 12px;

            color: var(--muted);
            font-size: 13px;
        }

        .form-control {
            margin-top: 6px;
            border-radius: 10px;
        }

        .btn-save {
            width: 100%;
            margin-top: 20px;
            padding: 11px;

            color: #fff;
            font-weight: 600;

            background: var(--primary);
            border: none;
            border-radius: 10px;
        }

        .server-msg {
            margin-bottom: 14px;
            padding: 10px;

            font-size: 13px;
            text-align: center;

            border-radius: 10px;
        }

        .server-msg.error {
            color: var(--danger);
            background: var(--danger-bg);
        }
    </style>
</head>

<body>

<a href="${pageContext.request.contextPath}/rack/list"
   class="back-arrow">
    ←
</a>

<div class="card">

    <h5 class="text-center">Add Rack</h5>

    <c:if test="${not empty message}">
        <div class="server-msg ${messageType}">
            ${message}
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/rack/add"
          method="post">

        <label>Rack Name</label>

        <input type="text"
               name="identifier"
               value="${rackDto.identifier}"
               class="form-control"
               required>

        <label>Assign Shelves</label>

        <select name="shelfIdentifiers"
                class="form-control"
                multiple
                required>

            <c:forEach items="${shelves}" var="shelf">
                <option value="${shelf.identifier}">
                    ${shelf.identifier}
                </option>
            </c:forEach>

        </select>

        <button type="submit"
                class="btn-save">
            Save Rack
        </button>

    </form>

</div>

</body>
</html>