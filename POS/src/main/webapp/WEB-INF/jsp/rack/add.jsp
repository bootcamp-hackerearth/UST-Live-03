<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Rack</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

    <style>
        body {
            background-color: #f5f6f7;
            font-family: "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
            color: #2e2e2e;
        }

        .card {
            border-radius: 10px;
            border: 1px solid #e0e0e0;
        }

        .card.shadow {
            box-shadow: 0 6px 18px rgba(0, 0, 0, 0.06);
        }

        .card-header {
            background-color: #000000;
            color: #ffffff;
            font-weight: 600;
            text-align: center;
            padding: 14px;
        }

        .form-label {
            font-weight: 500;
        }

        .form-control, .form-select {
            border-radius: 6px;
        }

        .btn-success {
            background-color: #198754;
            border-color: #198754;
        }

        .btn-secondary {
            background-color: #6c757d;
            border-color: #6c757d;
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">

            <div class="card shadow">
                <div class="card-header">
                    Add Rack
                </div>

                <div class="card-body">

                    <c:if test="${not empty message}">
                        <div class="alert alert-danger">
                            ${message}
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/rack/add"
                          method="post"
                          class="needs-validation"
                          novalidate>

                        <div class="mb-3">
                            <label class="form-label">Rack Name</label>
                            <input type="text"
                                   name="identifier"
                                   class="form-control"
                                   placeholder="Enter rack name"
                                   required
                                   minlength="2">
                            <div class="invalid-feedback">
                                Please enter rack name (minimum 2 characters).
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Assign Shelf</label>
                            <select name="shelfIdentifier"
                                    class="form-select"
                                    required>
                                <option value="" disabled selected>Select Shelf</option>

                                <c:forEach items="${shelves}" var="shelf">
                                    <option value="${shelf.identifier}">
                                        ${shelf.identifier}
                                    </option>
                                </c:forEach>
                            </select>

                            <div class="invalid-feedback">
                                Please select a shelf.
                            </div>

                            <c:if test="${empty shelves}">
                                <small class="text-muted">
                                    No shelves available
                                </small>
                            </c:if>
                        </div>

                        <div class="mb-4">
                            <label class="form-label">Rack Status</label>
                            <select name="status"
                                    class="form-select"
                                    required>
                                <option value="" disabled>Select Status</option>
                                <option value="true" selected>ACTIVE</option>
                                <option value="false">INACTIVE</option>
                            </select>
                            <div class="invalid-feedback">
                                Please select a status.
                            </div>
                        </div>

                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-success">
                                <i class="bi bi-check-circle"></i> Save Rack
                            </button>

                            <a href="${pageContext.request.contextPath}/rack/list"
                               class="btn btn-secondary">
                                Cancel
                            </a>
                        </div>

                    </form>

                </div>
            </div>

        </div>
    </div>
</div>

<script>
(() => {
    'use strict';
    const forms = document.querySelectorAll('.needs-validation');

    forms.forEach(form => {
        form.addEventListener('submit', event => {

            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }

            form.classList.add('was-validated');

        }, false);
    });
})();
</script>

</body>
</html>