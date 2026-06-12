<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>POS Management Registration</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        body {
            background-color: #E9EEF5;
        }

        .card {
            width: 380px;
            border: none;
            border-radius: 12px;
        }

        h3 {
            font-weight: 600;
        }

        .form-label {
            font-size: 13px;
        }

        .role-box {
            background: #f8f9fa;
            padding: 10px;
            border-radius: 8px;
            max-height: 120px;
            overflow-y: auto;
        }
    </style>
</head>

<body class="d-flex align-items-start justify-content-center min-vh-100 pt-5">

<div class="card shadow p-4">

    <h3 class="text-center mb-4">POS Management Registration</h3>

    <c:if test="${not empty message}">
        <div id="errorAlert" class="alert alert-danger text-center py-2 mb-3">
            ${message}
        </div>
    </c:if>

    <form:form action="register" method="post" modelAttribute="userDto">

        <div class="mb-3">
            <label class="form-label">Full Name</label>
            <form:input path="name" cssClass="form-control" required="required"/>
        </div>

        <div class="mb-3">
            <label class="form-label">Email</label>

            <input type="email"
                   name="username"
                   class="form-control"
                   required
                   pattern="^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$"
                   title="Enter a valid email like example@gmail.com" />
        </div>

        <div class="mb-3">
            <label class="form-label">Roles</label>

            <div class="role-box">

                <c:forEach items="${roles}" var="role">
                    <div class="form-check">

                        <input type="checkbox"
                               name="roles"
                               value="${role.identifier}"
                               class="form-check-input role-check"
                               id="role_${role.identifier}" />

                        <label class="form-check-label" for="role_${role.identifier}">
                            ${role.identifier}
                        </label>

                    </div>
                </c:forEach>

            </div>

            <small id="roleError" class="text-danger d-none">
                Please select at least one role
            </small>
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

        <div class="mb-3">
            <label class="form-label">Password</label>

            <form:password path="password"
                           cssClass="form-control"
                           minlength="6"
                           required="required"
                           title="Password must be at least 6 characters long"/>
        </div>

        <button type="submit" class="btn btn-success w-100">
            Register
        </button>

        <div class="text-center mt-3">
            <small>
                Already have an account?
                <a href="/login" class="text-decoration-none">Login</a>
            </small>
        </div>

    </form:form>
</div>

<script>
document.addEventListener("DOMContentLoaded", function () {
    const checks = document.querySelectorAll(".role-check");
    const error = document.getElementById("roleError");
    function validateRoles() {
        let selected = false;
        checks.forEach(c => { if (c.checked) selected = true; });
        if (selected) {
            error.classList.add("d-none");
        }
    }

    checks.forEach(c => { c.addEventListener("change", validateRoles); });

    document.querySelector("form").addEventListener("submit", function (e) {

        let selected = false;
        checks.forEach(c => { if (c.checked) selected = true; });
        if (!selected) {
            e.preventDefault();
            error.classList.remove("d-none");
        }
    });
});
</script>

</body>
</html>