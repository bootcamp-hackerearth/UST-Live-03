<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">
    <title>Add Node</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>

        body {
            background-color: #E9EEF5;
            min-height: 100vh;
        }

        .card {
            border-radius: 16px;
        }

        .form-control {
            border-radius: 10px;
        }

        .btn {
            border-radius: 10px;
        }

    </style>

</head>

<body>

<nav class="navbar navbar-dark bg-dark shadow">
    <div class="container-fluid">
        <span class="navbar-brand fw-bold">
            Node Management
        </span>

        <a href="/node/list"
           class="btn btn-outline-light btn-sm">
            Back
        </a>

    </div>

</nav>

<div class="container d-flex justify-content-center align-items-center"
     style="min-height: 100vh;">

    <div class="card shadow p-4"
         style="width: 500px;">

        <h3 class="text-center mb-4 fw-bold">
            Add Node
        </h3>

        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>

        <form action="/node/add"
              method="post"
              onsubmit="return validateRoles()">

            <div class="mb-3">

                <label class="form-label">
                    Identifier
                </label>

                <input type="text"
                       name="identifier"
                       class="form-control"
                       placeholder="Enter identifier"
                       maxlength="50"
                       required>
            </div>

            <div class="mb-3">

                <label class="form-label">
                    Path
                </label>

                <input type="text"
                       name="path"
                       class="form-control"
                       placeholder="/example/example"
                       maxlength="100"
                       required>
            </div>

            <div class="mb-4">

                <label class="form-label fw-semibold">
                    Roles (Select at least one)
                </label>

                <div class="border rounded p-3">
                    <c:if test="${empty roles}">
                        <span class="text-danger">
                            No roles available
                        </span>
                    </c:if>

                    <c:forEach items="${roles}" var="r">
                        <div class="form-check">
                            <input class="form-check-input"
                                   type="checkbox"
                                   name="roles"
                                   value="${r.identifier}"
                                   id="role_${r.identifier}">

                            <label class="form-check-label" for="role_${r.identifier}">
                                ${r.identifier}
                            </label>
                        </div>
                    </c:forEach>
                </div>

                <div id="roleError" class="text-danger mt-2" style="display:none;">
                    Please select at least one role
                </div>
            </div>

            <div class="d-flex gap-2">

                <button type="submit" class="btn btn-primary w-100">
                    Save
                </button>

                <a href="/node/list" class="btn btn-outline-secondary w-100">
                    Cancel
                </a>
            </div>
        </form>
    </div>
</div>
<script>

function validateRoles() {
    const roles = document.querySelectorAll('input[name="roles"]:checked');
    const error = document.getElementById("roleError");
    if (roles.length === 0) {
        error.style.display = "block";
        return false;
    }
    error.style.display = "none";
   return true;
}
</script>

</body>
</html>