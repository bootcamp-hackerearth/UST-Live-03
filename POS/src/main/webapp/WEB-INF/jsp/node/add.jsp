<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Node</title>
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
        input[type="text"],
        select {
            width: 100%;
            padding: 10px;
            border: 1px solid #ccc;
            border-radius: 4px;
            font-size: 14px;
        }

        .input-error {
            border: 2px solid #dc3545 !important;
            background-color: #fff5f5;
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
            font-size: 14px;
        }

        .actions {
            margin-top: 30px;
            text-align: center;
        }

        .btn {
            padding: 10px 20px;
            border-radius: 4px;
            border: none;
            cursor: pointer;
            font-size: 14px;
            font-weight: bold;
            text-decoration: none;
            display: inline-block;
        }

        .btn-save {
            background-color: #28a745;
            color: white;
            margin-right: 10px;
        }

        .btn-cancel {
            background-color: #6c757d;
            color: white;
        }
    </style>
</head>

<body>

<div class="container">
    <h2>Add New Node</h2>

    <!-- ERROR MESSAGE -->
    <c:if test="${not empty error}">
        <div class="error">${error}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/node/add" method="post">

        <!-- Node Name -->
        <div class="form-group">
            <label>Node Name</label>
            <input type="text"
                   name="identifier"
                   placeholder="e.g. Dashboard"
                   value="${param.identifier}"
                   class="${not empty error && error.contains('already') ? 'input-error' : ''}"
                   required />
        </div>

        <!-- Path -->
        <div class="form-group">
            <label>Path</label>
            <input type="text"
                   name="path"
                   placeholder="e.g. /admin/home"
                   value="${param.path}"
                   required />
        </div>

        <!-- Roles -->
        <div class="form-group">
            <label>Roles</label>
            <select name="roles" multiple>
                <c:forEach var="role" items="${roles}">
                    <option value="${role.identifier}">
                        ${role.identifier}
                    </option>
                </c:forEach>
            </select>
            <div class="hint">Hold Ctrl (Win) or Cmd (Mac) to select multiple</div>
        </div>

        <div class="actions">
            <button type="submit" class="btn btn-save">Save Node</button>
            <a href="${pageContext.request.contextPath}/node/list" class="btn btn-cancel">Cancel</a>
        </div>

    </form>
</div>

</body>
</html>