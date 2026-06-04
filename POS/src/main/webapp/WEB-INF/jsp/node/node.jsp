<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Node</title>

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        :root {
            --bg: #f6fff8;
            --card: #ffffff;

            --text: #1f2937;
            --muted: #6b7280;

            --primary: #28a745;
            --primary-hover: #218838;

            --accent: #ffc107;

            --border: #e5e7eb;

            --radius: 14px;
            --shadow: 0 10px 30px rgba(0,0,0,0.08);
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
        }

        .back-arrow {
            position: absolute;
            top: 20px;
            left: 20px;
            background: var(--card);
            border: 1px solid var(--border);
            border-radius: 50%;
            width: 42px;
            height: 42px;
            display: flex;
            align-items: center;
            justify-content: center;
            text-decoration: none;
            color: var(--text);
            font-size: 18px;
            box-shadow: var(--shadow);
            transition: 0.2s;
        }

        .back-arrow:hover {
            background: var(--accent);
            color: black;
        }

        .container-box {
            width: 100%;
            max-width: 480px;
        }

        .card {
            background: var(--card);
            border-radius: var(--radius);
            box-shadow: var(--shadow);
            padding: 24px;
        }

        h2 {
            text-align: center;
            font-size: 18px;
            font-weight: 600;
            margin-bottom: 18px;
            color: var(--text);
        }

        label {
            font-size: 13px;
            color: var(--muted);
            margin-top: 10px;
        }

        .form-control, select {
            margin-top: 6px;
            border-radius: 10px;
            padding: 10px;
            border: 1px solid var(--border);
        }

        .form-control:focus, select:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(40,167,69,0.15);
        }

        select[multiple] {
            height: 120px;
        }

        .btn-update {
            margin-top: 18px;
            width: 100%;
            padding: 10px;
            border-radius: 10px;
            border: none;
            background: var(--primary);
            color: white;
            font-weight: 600;
        }

        .btn-update:hover {
            background: var(--primary-hover);
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

<a href="${pageContext.request.contextPath}/node/list" class="back-arrow">
    ←
</a>

<div class="container-box">
    <div class="card">

        <h2>Edit Node</h2>

        <form action="${pageContext.request.contextPath}/node/update"
              method="post"
              onsubmit="return validateNode()">

            <input type="hidden" name="id" value="${node.id}" />
            <input type="hidden" name="identifier" value="${node.identifier}" />

            <label>Path</label>
            <input type="text"
                   name="path"
                   value="${node.path}"
                   class="form-control"
                   required />

            <label>Roles</label>
            <select name="roles" multiple class="form-control" required>
                <c:forEach var="role" items="${roles}">
                    <option value="${role.identifier}"
                        <c:if test="${node.roles.contains(role.identifier)}">selected</c:if>>
                        ${role.identifier}
                    </option>
                </c:forEach>
            </select>

            <button type="submit" class="btn-update">
                Update Node
            </button>

        </form>

    </div>
</div>

</body>
</html>