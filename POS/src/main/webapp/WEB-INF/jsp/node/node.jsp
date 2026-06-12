<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Node</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f4f7f6;
            padding: 40px;
            color: #333;
        }
        .container {
            max-width: 450px;
            margin: 0 auto;
            background: white;
            padding: 30px;
            border-radius: 8px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.1);
        }
        h2 {
            text-align: center;
            margin-bottom: 25px;
            border-bottom: 2px solid #eee;
            padding-bottom: 10px;
        }
        .form-group {
            margin-bottom: 20px;
        }
        label {
            display: block;
            margin-bottom: 8px;
            font-weight: bold;
            font-size: 14px;
            color: #555;
        }
        input[type="text"], select {
            width: 100%;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
        }
        select[multiple] {
            height: 100px;
        }
        .hint {
            font-size: 12px;
            color: #777;
            margin-top: 5px;
        }
        .error {
            color: #dc3545;
            background-color: #f8d7da;
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 15px;
            text-align: center;
        }
        .actions {
            text-align: center;
            margin-top: 20px;
        }
        .btn {
            padding: 10px 20px;
            border-radius: 4px;
            border: none;
            cursor: pointer;
            font-weight: bold;
            text-decoration: none;
        }
        .btn-update {
            background-color: #007bff;
            color: white;
        }
        .btn-cancel {
            background-color: #6c757d;
            color: white;
            margin-left: 10px;
        }
    </style>
</head>
<body>

<div class="container">

    <h2>Edit Node Details</h2>

    <!-- Error -->
    <c:if test="${not empty error}">
        <div class="error">${error}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/node/update" method="post">

        <!-- Hidden ID -->
        <input type="hidden" name="id" value="${node.id}" />

        <!-- Identifier -->
        <div class="form-group">
            <label>Node Name</label>
            <input type="text" name="identifier"
                   value="${node != null && node.identifier != null ? node.identifier : ''}"
                    readonly="true"/>

        </div>

        <!-- Path -->
        <div class="form-group">
            <label>Path</label>
            <input type="text" name="path"
                   value="${node != null && node.path != null ? node.path : ''}" required />
        </div>

        <!-- Roles -->
        <div class="form-group">
            <label>Assigned Roles</label>

            <select name="roles" multiple>
                <c:if test="${not empty roles}">
                    <c:forEach var="role" items="${roles}">
                        <option value="${role.identifier}"
                            <c:if test="${node != null and node.roles != null and node.roles.contains(role.identifier)}">
                                selected
                            </c:if>>
                            ${role.identifier}
                        </option>
                    </c:forEach>
                </c:if>
            </select>

            <div class="hint">Hold Ctrl (Win) or Cmd (Mac) to select multiple</div>
        </div>

        <!-- Actions -->
        <div class="actions">
            <button type="submit" class="btn btn-update">Update Node</button>
            <a href="${pageContext.request.contextPath}/node/list" class="btn btn-cancel">Cancel</a>
        </div>

    </form>

</div>

</body>
</html>