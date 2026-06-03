
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>User Registration</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            font-family: "Segoe UI", sans-serif;
            background: linear-gradient(135deg, #e0e7ff, #f8fafc);
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card {
            width: 420px;
            border-radius: 16px;
            background: rgba(255, 255, 255, 0.85);
            backdrop-filter: blur(12px);
            border: 1px solid rgba(255,255,255,0.4);
            box-shadow: 0 20px 40px rgba(0,0,0,0.1);
        }

        .card-header {
            background: transparent;
            color: #1e293b;
            border-bottom: 1px solid #e2e8f0;
            border-top-left-radius: 16px;
            border-top-right-radius: 16px;
        }

        h5 {
            font-weight: 600;
            letter-spacing: 0.3px;
        }

        .form-label {
            font-size: 0.85rem;
            font-weight: 600;
            color: #475569;
        }

        .form-control {
            border-radius: 10px;
            border: 1px solid #cbd5e1;
            padding: 10px;
            font-size: 0.9rem;
            transition: 0.25s;
        }

        .form-control:focus {
            border-color: #6366f1;
            box-shadow: 0 0 0 3px rgba(99,102,241,0.15);
        }

        select[multiple] {
            height: 110px;
        }

        .form-text {
            font-size: 0.75rem;
            color: #64748b;
        }

        .btn-primary {
            background: linear-gradient(135deg, #6366f1, #4f46e5);
            border: none;
            border-radius: 10px;
            padding: 10px;
            font-weight: 500;
            letter-spacing: 0.3px;
            transition: 0.3s;
        }

        .btn-primary:hover {
            transform: translateY(-1px);
            box-shadow: 0 8px 18px rgba(79,70,229,0.3);
        }

        .alert {
            border-radius: 10px;
            font-size: 0.85rem;
        }

        .card-footer {
            background: transparent;
            border-top: 1px solid #e2e8f0;
            color: #64748b;
        }
    </style>
</head>

<body>

<div class="card">
    <div class="card-header text-center py-3">
        <h5 class="mb-0">Create User Account</h5>
    </div>

    <div class="card-body p-4">

        <form:form action="register" method="post" modelAttribute="userDto">
            <div class="mb-3">
                <label class="form-label">Name</label>
                <form:input path="name" cssClass="form-control" required="true"/>
            </div>

            <div class="mb-3">
                <label class="form-label">Email</label>
                <form:input path="username" type="email"
                            cssClass="form-control" required="true"/>
            </div>

            <div class="mb-3">
                <label class="form-label">Roles</label>
                <form:select path="roles" multiple="true" cssClass="form-control">
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
                            maxlength="10"
                            title="Enter a valid 10-digit mobile number"
                            required="true"/>
            </div>

            <div class="mb-3">
                <label class="form-label">Password</label>
                <form:password path="password"
                               cssClass="form-control"
                               required="true"/>
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

    <div class="card-footer text-center small">
        POS Management System
    </div>

</div>
</body>
</html>
