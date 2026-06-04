<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Node</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: linear-gradient(135deg, #667eea, #764ba2);
            min-height: 100vh;
        }
        .card {
            border-radius: 12px;
        }
        .form-control, .form-select {
            border-radius: 8px;
        }
    </style>
</head>
<body>

<div class="container d-flex justify-content-center align-items-center mt-5">
    <div class="col-md-6">

        <div class="card shadow-lg">
            <div class="card-header text-center bg-primary text-white">
                <h4 class="mb-0">Add New Node</h4>
            </div>

            <div class="card-body">

                <!-- Success message -->
                 <!-- ✅ ERROR MESSAGE (shows when node already exists) -->
                    <c:if test="${not empty message}">
                        <div class="alert alert-danger text-center">
                            ${message}
                        </div>
                    </c:if>

                <form:form method="post"
                           action="/node/add"
                           modelAttribute="nodeDto">

                    <!-- Identifier -->
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Node Identifier</label>
                        <form:input path="identifier"
                                    cssClass="form-control"
                                    placeholder="Enter node identifier" />
                    </div>

                    <!-- Path -->
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Node Path</label>
                        <form:input path="path"
                                    cssClass="form-control"
                                    placeholder="/admin/dashboard" />
                    </div>

                    <!-- Roles (Multi-select) -->
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Assign Roles</label>
                        <form:select path="roles"
                                     cssClass="form-select"
                                     multiple="true">
                            <c:forEach var="role" items="${roles}">
                                <option value="${role.identifier}">
                                    ${role.identifier}
                                </option>
                            </c:forEach>
                        </form:select>
                        <small class="text-muted">
                            Hold Ctrl (Windows) / Cmd (Mac) to select multiple roles
                        </small>
                    </div>

                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary btn-lg">
                            Add Node
                        </button>
                    </div>

                </form:form>

            </div>

            <div class="card-footer text-center text-muted small">
                POS Management System
            </div>
        </div>

    </div>
</div>

</body>
</html>