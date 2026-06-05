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
            margin: 0;
            min-height: 100vh;
            background-color: #f1f3f6;
            display: flex;
            justify-content: center;
            align-items: center;
            font-family: "Segoe UI", sans-serif;
            padding: 20px;
        }

        .card {
            width: 650px;
            border: none;
            border-radius: 12px;
            overflow: hidden;
            box-shadow: 0 2px 12px rgba(0, 0, 0, 0.12);
        }

        .card-header {
            background-color: #1e272e;
            color: #ffffff;
            text-align: center;
            padding: 25px;
        }

        .card-header h5 {
            margin: 0;
            font-size: 2rem;
            font-weight: 600;
        }

        .card-body {
            background-color: #ffffff;
            padding: 35px;
        }

        .form-label {
            font-size: 1rem;
            font-weight: 600;
            color: #212529;
            margin-bottom: 8px;
        }

        .form-control {
            height: 52px;
            border-radius: 8px;
            border: 1px solid #ced4da;
            font-size: 1rem;
        }

        .form-control:focus {
            border-color: #0d6efd;
            box-shadow: none;
        }

        select[multiple] {
            height: 120px !important;
        }

        .btn-primary {
            background-color: #0d6efd;
            border: none;
            height: 54px;
            font-size: 1.1rem;
            border-radius: 8px;
            font-weight: 500;
        }

        .btn-primary:hover {
            background-color: #0b5ed7;
        }

        .alert {
            border-radius: 8px;
        }

        .form-text {
            font-size: 0.8rem;
            color: #6c757d;
        }

        .badge {
            font-size: 0.75rem;
        }

        .card-footer {
            background-color: #f8f9fa;
            text-align: center;
            padding: 15px;
            border-top: 1px solid #dee2e6;
            color: #6c757d;
        }

        .card-footer a {
            text-decoration: none;
        }
    </style>
</head>

<body>

<div class="card">

    <div class="card-header">
        <h5>Update User</h5>
    </div>

    <div class="card-body">

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
                    pattern="[A-Za-z ]{3,50}"
                    minlength="3"
                    maxlength="50"
                    title="Name must contain only letters and spaces"
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
                    minlength="10"
                    maxlength="10"
                    title="Enter a valid 10-digit mobile number"
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

    <div class="card-footer">
        <a href="/user/list">
            ← Back to User List
        </a>
    </div>

</div>

</body>
</html>