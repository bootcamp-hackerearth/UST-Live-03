<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Update User</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: #E9EEF5;
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
        <span class="navbar-brand fw-bold">User Management</span>
        <a href="/user/list" class="btn btn-outline-light btn-sm">Back</a>
    </div>
</nav>

<div class="container d-flex justify-content-center align-items-center" style="min-height: 100vh;">
    <div class="card shadow p-4" style="width: 500px;">

        <h3 class="text-center mb-4 fw-bold">Update User</h3>

        <c:if test="${empty userDto}">
            <div class="alert alert-danger text-center">
                User not found
            </div>
        </c:if>

        <c:if test="${not empty userDto}">

            <form:form action="/user/update"
                       method="post"
                       modelAttribute="userDto"
                       id="userForm">

                <form:hidden path="id"/>
                <form:hidden path="oldUsername"/>

                <div class="mb-3">
                    <label class="form-label fw-semibold">Name</label>
                    <form:input path="name"
                                cssClass="form-control"
                                required="required"/>
                </div>

                <div class="mb-3">
                    <label class="form-label fw-semibold">Email</label>
                    <form:input path="username"
                                cssClass="form-control"
                                required="required"
                                type="email"
                                readonly="true"/>
                </div>

                 <div class="mb-3">
                    <label class="form-label">Phone Number</label>
                      <form:input path="phoneNo"
                                  cssClass="form-control"
                                  maxlength="10"
                                  pattern="^[0-9]{10}$"
                                  oninput="this.value=this.value.replace(/[^0-9]/g,'')"
                                  required="required"/>
                  </div>

                <div class="mb-4">
                    <label class="form-label fw-semibold">Roles</label>

                    <div class="border rounded p-2">

                        <c:forEach items="${roles}" var="role">
                            <div class="form-check">

                                <form:checkbox path="roles"
                                               value="${role.identifier}"
                                               cssClass="form-check-input role-check"
                                               id="role_${role.identifier}"/>

                                <label class="form-check-label"
                                       for="role_${role.identifier}">
                                    ${role.identifier}
                                </label>

                            </div>
                        </c:forEach>

                    </div>

                    <small id="roleError" class="text-danger d-none">
                        Please select at least one role
                    </small>
                </div>

                <div class="d-flex gap-2">
                    <button type="submit" class="btn btn-primary w-100">
                        Update
                    </button>

                    <a href="/user/list" class="btn btn-outline-secondary w-100">
                        Cancel
                    </a>
                </div>

            </form:form>

        </c:if>

    </div>
</div>

<script>
document.addEventListener("DOMContentLoaded", function () {

    const form = document.getElementById("userForm");
    const checks = document.querySelectorAll(".role-check");
    const error = document.getElementById("roleError");

    function isRoleSelected() {
        return Array.from(checks).some(c => c.checked);
    }

    function hideError() {
        if (isRoleSelected()) {
            error.classList.add("d-none");
        }
    }

    checks.forEach(c => {
        c.addEventListener("change", hideError);
    });

    form.addEventListener("submit", function (e) {

        if (!isRoleSelected()) {
            e.preventDefault();
            error.classList.remove("d-none");
        }

    });

});
</script>

</body>
</html>