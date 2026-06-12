<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Node</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        body {
            background-color: #f5f6f7;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
        }

        .card {
            border-radius: 12px;
            border: 1px solid #ddd;
            max-width: 480px;
            margin: 60px auto;
            box-shadow: 0 6px 18px rgba(0,0,0,.08);
        }

        .card-header {
            background-color: #000;
            color: #fff;
            text-align: center;
            font-weight: 600;
            padding: 14px;
            border-radius: 12px 12px 0 0;
        }

        .form-control {
            border-radius: 6px;
        }

        .form-label {
            font-weight: 500;
        }

        .input-error {
            border: 2px solid #dc3545 !important;
            background-color: #fff5f5;
        }

        .btn {
            border-radius: 6px;
        }

        .dropdown-check {
            position: relative;
        }

        .dropdown-btn {
            width: 100%;
            height: 38px;
            padding: 0 12px;
            border: 1px solid #ccc;
            border-radius: 6px;
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
            border-radius: 6px;
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
            padding: 6px 10px;
            gap: 8px;
        }

        .dropdown-content label:hover {
            background: #f1f1f1;
        }
    </style>
</head>

<body>

<div class="card">

    <div class="card-header">
        Add Node
    </div>

    <div class="card-body">

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
                       value="${param.identifier}"
                       class="form-control ${not empty error && error.contains('already') ? 'input-error' : ''}"
                       placeholder="e.g. NODE_VIEW"
                       required>
            </div>

            <div class="mb-3">
                <label class="form-label">Node Path</label>
                <input type="text"
                       name="path"
                       value="${param.path}"
                       class="form-control"
                       placeholder="/node/list"
                       required>
            </div>

            <div class="mb-3">
                <label class="form-label">Assign Roles</label>

                <div class="dropdown-check" id="roleDropdown">

                    <div class="dropdown-btn" onclick="toggleDropdown()">
                        <span id="selectedRoles">Select Roles</span>
                        <span>▼</span>
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

            <div class="d-grid gap-2 mt-4">
                <button type="submit" class="btn btn-success">
                    Add Node
                </button>

                <a href="${pageContext.request.contextPath}/node/list"
                   class="btn btn-secondary">
                    Back
                </a>
            </div>

        </form>

    </div>

    <div class="card-footer text-center text-muted small">
        Node Management System
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

const checkboxes = document.querySelectorAll("input[name='roles']");
const selectedText = document.getElementById("selectedRoles");

checkboxes.forEach(cb => {
    cb.addEventListener("change", () => {
        let selected = Array.from(checkboxes)
            .filter(c => c.checked)
            .map(c => c.value);

        selectedText.innerText = selected.length > 0
            ? selected.join(", ")
            : "Select Roles";
    });
});
</script>

</body>
</html>