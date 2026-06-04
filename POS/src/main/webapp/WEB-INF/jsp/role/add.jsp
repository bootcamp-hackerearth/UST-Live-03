<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Role</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: linear-gradient(135deg, #eef2ff, #e0e7ff);
            min-height: 100vh;
        }

        .card {
            border-radius: 15px;
        }

        .form-control {
            border-radius: 8px;
        }

        .form-label {
            font-weight: 500;
        }

        .input-error {
            border: 2px solid #dc3545 !important;
            background-color: #fff5f5;
        }

        .btn-grey {
            background: #6c757d;
            border: none;
            color: white;
        }

        .btn-grey:hover {
            background: #5a6268;
        }
    </style>
</head>

<body>

<div class="container mt-5 d-flex justify-content-center">
    <div class="col-md-5">

        <div class="card shadow-lg">

            <div class="card-body">

                <h3 class="text-center mb-4">Add Role</h3>

                <c:if test="${not empty error}">
                    <div class="alert alert-danger text-center">
                        ${error}
                    </div>
                </c:if>

                <form action="${pageContext.request.contextPath}/role/add" method="post">

                    <div class="mb-3">
                        <label class="form-label">Role Identifier</label>
                        <input type="text"
                               name="identifier"
                               placeholder="e.g. ADMIN, USER"
                               value="${param.identifier}"
                               class="form-control ${not empty error && error.contains('already') ? 'input-error' : ''}"
                               required>
                    </div>

                    <div class="mb-3">
                        <label class="form-label">Description</label>
                        <textarea name="description"
                                  class="form-control"
                                  rows="3"
                                  placeholder="Describe role responsibilities">${param.description}</textarea>
                    </div>

                    <div class="d-flex gap-2 mt-4">

                        <button type="submit" class="btn btn-grey w-100">
                            Add Role
                        </button>

                        <a href="${pageContext.request.contextPath}/user/list"
                           class="btn btn-grey w-100 text-center">
                            Cancel
                        </a>

                    </div>

                </form>

            </div>

            <div class="card-footer text-center text-muted small">
                Role Management System
            </div>

        </div>

    </div>
</div>

</body>
</html>