<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Registration</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

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

        .card-footer {
            background-color: #f8f9fa;
            text-align: center;
            padding: 15px;
            color: #6c757d;
            border-top: 1px solid #dee2e6;
        }
    </style>
</head>

<body>

<div class="card">

    <div class="card-header">
        <h5>Create User Account</h5>
    </div>

    <div class="card-body">

        <form:form action="register" method="post" modelAttribute="userDto">

            <div class="mb-3">
                <label class="form-label">Name</label>
                <form:input path="name"
                            cssClass="form-control"
                            required="true"
                            pattern="[A-Za-z ]{3,50}"
                            title="Name must contain only letters and spaces and be at least 3 characters long"/>
            </div>

            <div class="mb-3">
                <label class="form-label">Email</label>
                <form:input path="username"
                            type="email"
                            cssClass="form-control"
                            required="true"/>
            </div>

            <div class="mb-3">
                <label class="form-label">Roles</label>
                <form:select path="roles" multiple="true" cssClass="form-control" required="true">
                    <form:options items="${roles}"
                                  itemValue="identifier"
                                  itemLabel="identifier"/>
                </form:select>
                <div class="form-text">
                    Hold Ctrl (Windows/Linux) or Cmd (Mac) to select multiple roles
                </div>
            </div>

            <div class="mb-3">
                <label class="form-label">Phone Number</label>
                <form:input path="phoneNo"
                            type="tel"
                            cssClass="form-control"
                            pattern="[0-9]{10}"
                            minlength="10"
                            maxlength="10"
                            title="Enter a valid 10-digit mobile number"
                            required="true"/>
            </div>

            <div class="mb-3">
                <label class="form-label">Password</label>
                <form:password path="password"
                               cssClass="form-control"
                               required="true"
                               minlength="8"
                               maxlength="20"
                               pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&]).{8,20}$"
                               title="Password must contain 8-20 characters, one uppercase letter, one lowercase letter, one number and one special character"/>
                <div class="form-text">
                    Password must contain uppercase, lowercase, number and special character.
                </div>
            </div>

            <div class="d-grid mt-4">
                <button type="submit" class="btn btn-primary">
                    Register User
                </button>
            </div>

            <c:if test="${not empty message}">
                <div class="alert alert-danger text-center mt-3">
                    ${message}
                </div>
            </c:if>

        </form:form>

    </div>

    <div class="card-footer">
        POS Management System
    </div>

</div>

</body>
</html>