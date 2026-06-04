<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update User</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"
          rel="stylesheet">

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

        .form-control,
        .form-select {
            border-radius: 8px;
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
</head>

<body class="container py-4">

<div class="page-header">
    <h4 class="mb-0">
        <i class="bi bi-person-check me-2"></i> Update User
    </h4>
</div>

<div class="form-wrapper">
    <div class="card shadow-sm">
        <div class="card-body">

            <c:if test="${not empty message}">
                <div class="alert alert-danger text-center">
                    ${message}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/user/update" method="post">

                <input type="hidden" name="oldUsername" value="${user.username}">
                <input type="hidden" name="id" value="${user.id}">

                <div class="mb-3">
                    <label>Name</label>
                    <input type="text"
                           name="name"
                           class="form-control"
                           value="${user.name}"
                           required>
                </div>

                <div class="mb-3">
                    <label>Email</label>
                    <input type="email"
                           name="username"
                           class="form-control"
                           value="${user.username}"
                           required>
                </div>

                <div class="mb-3">
                    <label>Phone Number</label>
                    <input type="text"
                           name="phoneNo"
                           class="form-control"
                           value="${user.phoneNo}"
                           required>
                </div>

                <div class="mb-3">
                    <label>Assign Roles</label>
                    <select name="roles"
                            id="roles"
                            class="form-select"
                            multiple
                            size="5"
                            required>
                        <c:forEach items="${roles}" var="role">
                            <option value="${role.identifier}"
                                <c:if test="${user.roles.contains(role.identifier)}">
                                    selected
                                </c:if>>
                                ${role.identifier}
                            </option>
                        </c:forEach>
                    </select>
                    <small class="text-muted">
                        Hold Ctrl (Windows/Linux) or Cmd (Mac) to select multiple roles
                    </small>
                </div>

                <div class="d-flex justify-content-end gap-2 mt-4">
                    <a href="${pageContext.request.contextPath}/user/list"
                       class="btn btn-outline-secondary">
                        Cancel
                    </a>

                    <button type="submit" class="btn btn-gradient">
                        <i class="bi bi-save me-1"></i> Update User
                    </button>
                </div>

            </form>

        </div>
    </div>
</div>

<script>
    document.querySelector("form").addEventListener("submit", function (e) {

        const name = this.name.value.trim();
        const phone = this.phoneNo.value.trim();
        const roles = document.getElementById("roles");

        if (!/^[A-Za-z ]{3,}$/.test(name)) {
            alert("Name must contain at least 3 letters.");
            e.preventDefault();
            return;
        }

        if (!/^[0-9]{10}$/.test(phone)) {
            alert("Phone number must be exactly 10 digits.");
            e.preventDefault();
            return;
        }

        if ([...roles.options].filter(option => option.selected).length === 0) {
            alert("Please select at least one role.");
            e.preventDefault();
        }
    });
</script>

</body>
</html>