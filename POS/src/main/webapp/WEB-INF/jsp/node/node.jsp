<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Node</title>
    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Arial, sans-serif;
            background-color: #f4f6fb;
            background-image:
                radial-gradient(circle at top right, rgba(78,115,223,0.12), transparent 45%),
                radial-gradient(circle at bottom left, rgba(78,115,223,0.08), transparent 45%);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card {
            background-color: #ffffff;
            border-radius: 16px;
            padding: 32px;
            width: 420px;
            box-shadow: 0 20px 45px rgba(0, 0, 0, 0.15);
        }

        h3 {
            text-align: center;
            color: #4e73df;
            font-weight: 700;
            margin-bottom: 26px;
        }

        label {
            font-size: 14px;
            font-weight: 600;
            color: #444;
            margin-bottom: 6px;
            display: block;
        }

        .form-group {
            margin-bottom: 18px;
        }

        .form-control {
            width: 100%;
            padding: 10px 12px;
            border-radius: 10px;
            border: 1px solid #ccd3e0;
            font-size: 14px;
            background: #f9fafc;
        }

        .form-control:focus {
            outline: none;
            border-color: #4e73df;
            box-shadow: 0 0 0 3px rgba(78,115,223,0.25);
            background: #ffffff;
        }

        input[readonly] {
            background-color: #e9ecef;
        }

        .role-box {
            border: 1px solid #ccd3e0;
            border-radius: 10px;
            padding: 12px;
            background: #f9fafc;
            max-height: 160px;
            overflow-y: auto;
        }

        .role-item {
            display: flex;
            align-items: center;
            margin-bottom: 8px;
        }

        .role-item input {
            margin-right: 10px;
            accent-color: #4e73df;
        }

        .btn-primary {
            width: 100%;
            margin-top: 24px;
            padding: 12px;
            font-size: 16px;
            font-weight: 700;
            color: #ffffff;
            border-radius: 30px;
            border: none;
            background: linear-gradient(135deg, #4e73df, #2e59d9);
            cursor: pointer;
            box-shadow: 0 12px 25px rgba(78,115,223,0.35);
        }

        .btn-primary:hover {
            background: #2e59d9;
        }

        .btn-back {
            margin-top: 14px;
            width: 100%;
            padding: 10px;
            font-size: 14px;
            font-weight: 600;
            border-radius: 30px;
            border: 2px solid #4e73df;
            background: transparent;
            color: #4e73df;
            cursor: pointer;
        }

        .btn-back:hover {
            background-color: #4e73df;
            color: #ffffff;
        }
    </style>
</head>
<body>
<div class="card">
    <h3>Update Node</h3>
    <form action="/node/update" method="post">
        <div class="form-group">
            <label>Id</label>
            <input type="text" class="form-control"
                   name="id" value="${node.id}" readonly>
        </div>
        <div class="form-group">
            <label>Node Identifier</label>
            <input type="text" class="form-control"
                   name="identifier" value="${node.identifier}" readonly>
        </div>
        <div class="form-group">
            <label>Path</label>
            <input type="text" class="form-control"
                   name="path" value="${node.path}" required>
        </div>
        <div class="form-group">
            <label>Primary Roles</label>
            <div class="role-box">
                <c:forEach items="${roles}" var="role">
                    <c:set var="checked" value="false"/>
                    <c:forEach items="${node.roles}" var="nr">
                        <c:if test="${nr == role.identifier}">
                            <c:set var="checked" value="true"/>
                        </c:if>
                    </c:forEach>
                    <div class="role-item">
                        <input type="checkbox"
                               name="roles"
                               value="${role.identifier}"
                               <c:if test="${checked}">checked</c:if>>
                        <span>${role.identifier}</span>
                    </div>
                </c:forEach>
            </div>
        </div>
        <button type="submit" class="btn-primary">
            Update and Save
        </button>
    </form>
    <div class="text-center">
        <a href="/node/list">
            <button type="button" class="btn-back">Back</button>
        </a>
    </div>
</div>
</body>
</html>