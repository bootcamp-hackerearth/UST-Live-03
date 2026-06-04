<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Node</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

    <style>
        body {
            background-color: #f7f9fc;
        }

        .page-header {
            background: linear-gradient(to right, #0f766e, #134e4a);
            color: #ffffff;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 20px;
            max-width: 480px;
            margin-left: auto;
            margin-right: auto;
            text-align: center;
        }

        .form-wrapper {
            max-width: 480px;
            margin: 0 auto;
        }

        label {
            font-weight: 600;
            font-size: 14px;
        }

        .form-control {
            border-radius: 8px;
        }

        .dropdown-box {
            position: relative;
        }

        .dropdown-btn {
            width: 100%;
            padding: 10px 12px;
            border-radius: 8px;
            border: 1px solid #ced4da;
            background: #ffffff;
            cursor: pointer;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .dropdown-content {
            position: absolute;
            top: 110%;
            width: 100%;
            background: #ffffff;
            border: 1px solid #ced4da;
            border-radius: 8px;
            max-height: 200px;
            overflow-y: auto;
            display: none;
            z-index: 100;
            padding: 8px;
        }

        .dropdown-box.active .dropdown-content {
            display: block;
        }

        .role-item {
            padding: 6px 10px;
            border-radius: 6px;
        }

        .role-item:hover {
            background: #f1f5f9;
        }

        .btn-gradient {
            background: linear-gradient(to right, #0f766e, #134e4a);
            color: #ffffff;
            border: none;
        }

        .btn-gradient:hover {
            background: linear-gradient(to right, #134e4a, #0f766e);
            color: #ffffff;
        }
    </style>

    <script>
        function toggleRoles() {
            document.getElementById("roleDropdown").classList.toggle("active");
        }

        function updateRolesText() {
            const checked = document.querySelectorAll("input[name='roles']:checked");
            const values = Array.from(checked).map(c => c.value);
            document.getElementById("selectedRoles").innerText =
                values.length ? values.join(", ") : "Select Roles";
        }

        document.addEventListener("click", function (e) {
            const dropdown = document.getElementById("roleDropdown");
            if (!dropdown.contains(e.target)) {
                dropdown.classList.remove("active");
            }
        });
    </script>
</head>

<body class="container py-4">

<div class="page-header">
    <h4 class="mb-0">
        <i class="bi bi-diagram-3-fill me-2"></i> Add Node
    </h4>
</div>

<div class="form-wrapper">
    <div class="card shadow-sm">
        <div class="card-body">

            <c:if test="${not empty error}">
                <div class="alert alert-danger text-center">
                    ${error}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/node/add"
                  method="post">

                <div class="mb-3">
                    <label>Node Identifier</label>
                    <input type="text"
                           name="identifier"
                           class="form-control"
                           value="${param.identifier}"
                           required>
                </div>

                <div class="mb-3">
                    <label>Node Path</label>
                    <input type="text"
                           name="path"
                           class="form-control"
                           value="${param.path}"
                           required>
                </div>

                <div class="mb-3">
                    <label>Assign Roles</label>

                    <div class="dropdown-box" id="roleDropdown">
                        <div class="dropdown-btn" onclick="toggleRoles()">
                            <span id="selectedRoles">Select Roles</span>
                            <i class="bi bi-chevron-down"></i>
                        </div>

                        <div class="dropdown-content">
                            <c:forEach items="${roles}" var="role">
                                <div class="form-check role-item">
                                    <input class="form-check-input"
                                           type="checkbox"
                                           name="roles"
                                           value="${role.identifier}"
                                           onchange="updateRolesText()">
                                    <label class="form-check-label">
                                        ${role.identifier}
                                    </label>
                                </div>
                            </c:forEach>

                            <c:if test="${empty roles}">
                                <div class="text-muted px-2">
                                    No roles available
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>

                <div class="d-flex justify-content-end gap-2 mt-4">
                    <a href="${pageContext.request.contextPath}/node/list"
                       class="btn btn-outline-secondary">
                        Cancel
                    </a>

                    <button type="submit" class="btn btn-gradient">
                        <i class="bi bi-save me-1"></i> Save Node
                    </button>
                </div>

            </form>

        </div>
    </div>
</div>
</body>
</html>