<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>POS User Registration</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">
    <style>
        body {
            background-color: #f3f5f7;
            font-family: "Segoe UI", sans-serif;
        }
        .page-container {
            max-width: 500px;
            margin: 25px auto;
        }
        .card {
            border: none;
            border-radius: 12px;
            box-shadow: 0 2px 12px rgba(0,0,0,0.08);
            overflow: hidden;
        }
        .card-header {
            background: white;
            border-bottom: 1px solid #e9ecef;
            padding: 14px 18px;
        }
        .card-header h3 {
            margin: 0;
            color: #2c3e50;
            font-weight: 600;
            font-size: 18px;
        }
        .card-body {
            padding: 18px;
        }
        .card-footer {
            background: white;
            border-top: 1px solid #e9ecef;
            text-align: center;
            padding: 10px;
            color: #6c757d;
            font-size: 12px;
        }
        .form-label {
            font-weight: 600;
            color: #495057;
            margin-bottom: 4px;
            font-size: 13px;
        }
        .form-control {
            border-radius: 6px;
            padding: 6px 10px;
            font-size: 14px;
        }
        .form-control:focus {
            box-shadow: none;
            border-color: #0d6efd;
        }
        .mb-4 {
            margin-bottom: 10px !important;
        }
        .error-box {
            background-color: #f8d7da;
            color: #842029;
            border: 1px solid #f5c2c7;
            padding: 8px;
            border-radius: 6px;
            margin-bottom: 12px;
            text-align: center;
            font-size: 13px;
        }
        .hint {
            font-size: 11px;
            color: #6c757d;
            margin-top: 4px;
        }
        .btn {
            min-width: 90px;
            padding: 6px 14px;
            font-size: 14px;
        }
    </style>
</head>
<body>
<div class="container-fluid">
    <div class="page-container">
        <div class="card">
            <div class="card-header">
                <h3>POS User Registration</h3>
            </div>
            <div class="card-body">
                <c:if test="${not empty message}">
                    <div class="error-box">
                        ${message}
                    </div>
                </c:if>
                <form:form action="register"
                           method="post"
                           modelAttribute="userDto">
                    <div class="mb-4">
                        <label class="form-label">
                            Full Name
                        </label>
                        <form:input path="name"
                                    cssClass="form-control"
                                    required="true"/>
                    </div>
                    <div class="mb-4">
                        <label class="form-label">
                            Email Address
                        </label>
                        <form:input path="username"
                                    type="email"
                                    cssClass="form-control"
                                    required="true"
                                    pattern="^[a-zA-Z0-9._%+-]+@gmail\.com$"
                                    title="Enter a valid Gmail address"/>
                    </div>
                    <div class="mb-4">
                        <label class="form-label">
                            Assigned Roles
                        </label>
                        <form:select path="roles"
                                     multiple="true"
                                     required="true"
                                     cssClass="form-control"
                                     size="2">
                            <form:options items="${roles}"
                                          itemValue="identifier"
                                          itemLabel="identifier"/>
                        </form:select>
                        <div class="hint">
                            Hold Ctrl (Windows) or Cmd (Mac) for multiple selection.
                        </div>
                    </div>
                    <div class="mb-4">
                        <label class="form-label">
                            Mobile Number
                        </label>
                        <form:input path="phoneNo"
                                    type="tel"
                                    cssClass="form-control"
                                    required="true"
                                    pattern="[0-9]{10}"
                                    maxlength="10"
                                    title="Enter a valid 10-digit mobile number"/>
                    </div>
                    <div class="mb-4">
                        <label class="form-label">
                            Password
                        </label>
                        <form:password path="password"
                                       cssClass="form-control"
                                       required="true"
                                       pattern=".{6,}"
                                       title="Password must be at least 6 characters"/>
                    </div>
                    <div class="d-flex justify-content-between mt-3">
                        <a href="${pageContext.request.contextPath}/user/list"
                           class="btn btn-secondary">
                            Cancel
                        </a>
                        <button type="submit"
                                class="btn btn-primary">
                            Register
                        </button>
                    </div>
                </form:form>
            </div>
            <div class="card-footer">
                POS Management System
            </div>
        </div>
    </div>
</div>
</body>
</html>