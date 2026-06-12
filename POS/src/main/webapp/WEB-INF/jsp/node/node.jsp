<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Create / Edit Node</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #ffffff;
            margin: 0;
            padding: 40px 0;
            color: #333;
            min-height: 100vh;
        }

        .container {
            max-width: 450px;
            background: #ffffff;
            margin: 0 auto;
            padding: 25px;
            border-radius: 14px;
            box-shadow: 0 10px 25px rgba(0,0,0,0.2);
        }

        h2 {
            text-align: center;
            margin-bottom: 20px;
            font-size: 20px;
            font-weight: 600;
            color: #333;
        }

        label {
            display: block;
            font-size: 14px;
            font-weight: bold;
            margin-bottom: 6px;
            color: #444;
        }

        input[type="text"], select {
            width: 100%;
            padding: 8px;
            border: 1px solid #bbb;
            margin-bottom: 15px;
            font-size: 14px;
            border-radius: 6px;
        }

        input[readonly] {
            background-color: #f0f0f0;
            cursor: not-allowed;
        }

        input:focus,
        select:focus {
            outline: none;
            border-color: #667eea;
        }

        select[multiple] {
            min-height: 120px;
        }

        .btn-group {
            display: flex;
            gap: 10px;
            margin-top: 10px;
        }

        .btn-primary {
            flex: 1;
            padding: 10px;
            border: none;
            background: #667eea;
            color: #ffffff;
            font-size: 14px;
            cursor: pointer;
            border-radius: 6px;
        }

        .btn-primary:hover {
            background: #5a6fdc;
        }

        .btn-secondary {
            flex: 1;
            padding: 10px;
            border: 1px solid #999;
            background: #f3f3f3;
            font-size: 14px;
            cursor: pointer;
            border-radius: 6px;
            text-align: center;
            text-decoration: none;
            color: #333;
        }

        .btn-secondary:hover {
            background: #e6e6e6;
        }
    </style>
</head>

<body>

<div class="container">

    <h2>Create / Edit Node</h2>

    <!-- ✅ Node Form -->
    <form action="${pageContext.request.contextPath}/node/update" method="post">

        <!-- ✅ Hidden DB ID -->
        <input type="hidden" name="id" value="${node.id}" />

        <!-- ✅ Identifier (READ-ONLY) -->
        <label for="identifier">Identifier</label>
        <input type="text"
               id="identifier"
               name="identifier"
               value="${node.identifier}"
               readonly />

        <!-- ✅ Path (Editable) -->
        <label for="path">Path</label>
        <input type="text"
               id="path"
               name="path"
               value="${node.path}"
               placeholder="/example/path"
               required />

        <!-- ✅ Roles -->
        <label for="roles">Roles</label>
        <select id="roles" name="roles" multiple required>
            <c:forEach var="role" items="${roles}">
                <option value="${role.identifier}"
                    <c:if test="${node.roles != null && node.roles.contains(role.identifier)}">
                        selected
                    </c:if>>
                    ${role.identifier}
                </option>
            </c:forEach>
        </select>

        <!-- ✅ Buttons -->
        <div class="btn-group">
            <button type="submit" class="btn-primary">
                Save
            </button>

            <a href="${pageContext.request.contextPath}/node/list"
               class="btn-secondary">
                Cancel
            </a>
        </div>

    </form>

</div>

</body>
</html>