<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Update User</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <style>
        body {
            min-height: 100vh;
            background-color: #f1f3f6;
            font-family: "Segoe UI", sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card {
            width: 460px;
            border-radius: 12px;
            border: none;
        }

        .card-header {
            background-color: #1e272e;
            color: #ffffff;
            border-radius: 12px 12px 0 0;
        }

        .form-label {
            font-size: 0.9rem;
            font-weight: 600;
        }

        select[multiple] {
            height: 120px;
        }

        .badge {
            font-size: 0.75rem;
        }
    </style>
</head>

<body>

<div class="card shadow-sm">

    <div class="card-header text-center py-3">
        <h5 class="mb-0">Update User</h5>
    </div>

    <div class="card-body p-4">

        <c:if test="${not empty message}">
            <div class="alert alert-info text-center">
                ${message}
            </div>
        </c:if>

        <form:form action="/user/update" method="post" modelAttribute="userDto">

            <form:hidden path="id"/>

            <div class="mb-3">
                <label class="form-label">Name</label>
                <form:input
                    path="name"
                    cssClass="form-control"
                    required="true"
                    minlength="3"
                    maxlength="50"
                    placeholder="Enter full name"
                />
            </div>

            <div class="mb-3">
                <label class="form-label">Email</label>
                <form:input
                    path="username"
                    cssClass="form-control"
                    type="email"
                    readonly="true"
                />
                <div class="form-text">
                    Email cannot be modified
                </div>
            </div>

            <div class="mb-3">
                <label class="form-label">Phone Number</label>
                <form:input
                    path="phoneNo"
                    cssClass="form-control"
                    required="true"
                    pattern="[0-9]{10}"
                    maxlength="10"
                    placeholder="Enter 10 digit number"
                />
                <div class="form-text">
                    Enter valid 10-digit number
                </div>
            </div>

            <div class="mb-3">
                <label class="form-label">Roles</label>

                <div class="mb-2 text-muted small">
                    Current:
                    <c:forEach var="r" items="${userDto.roles}">
                        <span class="badge bg-secondary me-1">${r}</span>
                    </c:forEach>
                </div>

                <form:select
                    path="roles"
                    multiple="true"
                    cssClass="form-control"
                    required="true"
                >
                    <form:options
                        items="${roles}"
                        itemValue="identifier"
                        itemLabel="identifier"
                    />
                </form:select>

                <div class="form-text">
                    Hold Ctrl (Windows/Linux) or Cmd (Mac) to select multiple roles
                </div>
            </div>

            <div class="d-grid mt-4">
                <button type="submit" class="btn btn-primary">
                    Update User
                </button>
            </div>

        </form:form>

    </div>

    <div class="card-footer text-center bg-light small">
        <a href="/user/list" class="text-decoration-none">
            ← Back to User List
        </a>
    </div>

</div>

</body>
</html>
