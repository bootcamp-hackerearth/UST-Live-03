<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Node</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: linear-gradient(135deg, #eef2ff, #e0e7ff);
            min-height: 100vh;
        }

        .card {
            border-radius: 15px;
        }

        .form-control {
            border-radius: 8px;
        }

        .form-label {
            font-weight: 500;
        }

        .input-error {
            border: 2px solid #dc3545 !important;
            background-color: #fff5f5;
        }

        .btn-grey {
            background: #6c757d;
            border: none;
            color: white;
        }

        .btn-grey:hover {
            background: #5a6268;
        }

        .dropdown-check {
            position: relative;
            width: 100%;
        }

        .dropdown-btn {
            width: 100%;
            height: 38px;
            padding: 0 12px;
            border: 1px solid #ccc;
            border-radius: 8px;
            background: #fff;
            cursor: pointer;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .dropdown-content {
            display: none;
            position: absolute;
            width: 100%;
            background: #fff;
            border: 1px solid #ddd;
            border-radius: 8px;
            margin-top: 5px;
            max-height: 180px;
            overflow-y: auto;
            z-index: 10;
        }

        .dropdown-check.active .dropdown-content {
            display: block;
        }

        .dropdown-content label {
            display: flex;
            align-items: center;
            padding: 8px 10px;
            gap: 8px;
            cursor: pointer;
        }

        .dropdown-content label:hover {
            background: #f1f1f1;
        }
    </style>
</head>

<body>

<div class="container mt-5 d-flex justify-content-center">
    <div class="col-md-5">

        <div class="card shadow-lg">

            <div class="card-body">

                <h3 class="text-center mb-4">Add Node</h3>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger text-center">
                        ${error}
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/node/add" method="post">

                    <div class="mb-3">
                        <label class="form-label">Node Identifier</label>
                        <input type="text"
                               name="identifier"
                               placeholder="e.g. NODE_VIEW"
                               value="${param.identifier}"
                               class="form-control ${not empty error && error.contains('already') ? 'input-error' : ''}"
                               required>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Node Path</label>
                        <input type="text"
                               name="path"
                               placeholder="/node/list"
                               value="${param.path}"
                               class="form-control"
                               required>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Assign Roles</label>

                        <div class="dropdown-check" id="roleDropdown">

                            <div class="dropdown-btn" onclick="toggleDropdown()">
                                <span id="selectedRoles">Select Roles</span>
                                <span>  ▼</span>
                            </div>

                            <div class="dropdown-content">
                                <c:forEach items="${roles}" var="role">
                                    <label>
                                        <input type="checkbox"
                                               name="roles"
                                               value="${role.identifier}">
                                        ${role.identifier}
                                    </label>
                                </c:forEach>
                            </div>

                        </div>
                    </div>

                    <div class="d-flex gap-2 mt-4">

                        <button type="submit" class="btn btn-grey w-100">
                            Add Node
                        </button>

                        <a href="${pageContext.request.contextPath}/node/list"
                           class="btn btn-grey w-100 text-center">
                            Cancel
                        </a>

                    </div>

                </form>

            </div>

            <div class="card-footer text-center text-muted small">
                Node Management System
            </div>

        </div>

    </div>
</div>

<script>
    function toggleDropdown() {
        document.getElementById("roleDropdown").classList.toggle("active");
    }

    document.addEventListener("click", function (e) {
        let dropdown = document.getElementById("roleDropdown");
        if (!dropdown.contains(e.target)) {
            dropdown.classList.remove("active");
        }
    });
</script>

</body>
</html>