<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Node</title>

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
            padding: 40px 16px;
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

            border-radius: var(--radius);
            border: 1px solid var(--border);

            box-shadow: var(--shadow);
            padding: 28px;
        }

        h2 {
            text-align: center;
            margin-bottom: 22px;
            font-weight: 600;
        }

        /* FORM */
        label {
            font-size: 13px;
            color: var(--muted);
            font-weight: 500;
            margin-bottom: 4px;
        }

        .form-control, select {
            border-radius: 10px;
            border: 1px solid var(--border);
            padding: 10px;
            font-size: 14px;
            background: rgba(255,255,255,0.9);
        }

        .form-control:focus, select:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(37,99,235,0.2);
            background: #fff;
        }

        select[multiple] {
            height: 120px;
        }

        /* BUTTON */
        .btn-submit {
            margin-top: 22px;
            background: var(--primary);
            color: white;
            border-radius: 10px;
            padding: 10px;
            font-weight: 600;
            border: none;
            width: 100%;
            transition: 0.2s;
        }

        .btn-submit:hover {
            background: var(--primary-hover);
            transform: translateY(-1px);
        }
    </style>

    <script>
        function validateNode() {
            let path = document.getElementsByName("path")[0].value.trim();
            let roles = document.getElementsByName("roles")[0];

            if (path === "") {
                alert("Path is required!");
                return false;
            }

            if (roles.selectedOptions.length === 0) {
                alert("Select at least one role!");
                return false;
            }

            return true;
        }
    </script>
</head>

<body>

<!-- BACK -->
<a href="${pageContext.request.contextPath}/node/list" class="back-arrow">←</a>

<div class="form-card">

    <h2>Edit Node</h2>

    <form action="${pageContext.request.contextPath}/node/update"
          method="post"
          onsubmit="return validateNode()">

        <input type="hidden" name="id" value="${node.id}" />
        <input type="hidden" name="identifier" value="${node.identifier}" />

        <div class="mb-3">
            <label>Path</label>
            <input type="text"
                   name="path"
                   value="${node.path}"
                   class="form-control"
                   required />
        </div>

        <div class="mb-3">
            <label>Roles</label>
            <select name="roles" multiple class="form-control" required>
                <c:forEach var="role" items="${roles}">
                    <option value="${role.identifier}"
                        <c:if test="${node.roles.contains(role.identifier)}">selected</c:if>>
                        ${role.identifier}
                    </option>
                </c:forEach>
            </select>
        </div>

        <button type="submit" class="btn-submit">
            Update Node
        </button>

    </form>

</div>

</body>
</html>