<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Unit</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

    <style>
        body {
            background-color: #f5f6f7;
            font-family: "Segoe UI", Roboto, Helvetica, Arial, sans-serif;
        }

        .card {
            border-radius: 10px;
            border: 1px solid #e0e0e0;
        }

        .card-header {
            background-color: #000000;
            color: #ffffff;
            font-weight: 600;
            text-align: center;
            padding: 14px;
        }

        .form-control, .form-select {
            border-radius: 6px;
        }
    </style>
</head>

<body>

<div class="container mt-5">
    <div class="row justify-content-center">
        <div class="col-md-6">

            <div class="card shadow">
                <div class="card-header">
                    Edit Unit
                </div>

                <div class="card-body">

                    <c:if test="${not empty message}">
                        <div class="alert alert-danger text-center">
                            ${message}
                        </div>
                    </c:if>

                    <form method="post"
                          action="${pageContext.request.contextPath}/unit/update"
                          class="needs-validation"
                          novalidate>

                        <input type="hidden" name="id" value="${unit.id}" />

                        <div class="mb-3">
                            <label class="form-label">Unit Name</label>
                            <input type="text"
                                   name="identifier"
                                   class="form-control"
                                   value="${unit.identifier}"
                                   readonly>
                        </div>

                        <div class="mb-4">
                            <label class="form-label">Status</label>
                            <select name="status" class="form-select" required>
                                <option value="">Select Status</option>
                                <option value="true"
                                    <c:if test="${unit.status}">selected</c:if>>
                                    ACTIVE
                                </option>
                                <option value="false"
                                    <c:if test="${!unit.status}">selected</c:if>>
                                    INACTIVE
                                </option>
                            </select>
                            <div class="invalid-feedback">
                                Please select a status.
                            </div>
                        </div>

                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-success">
                                <i class="bi bi-check-circle"></i> Update Unit
                            </button>

                            <a href="${pageContext.request.contextPath}/unit/list"
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

    Array.from(forms).forEach(form => {
        form.addEventListener('submit', event => {
            if (!form.checkValidity()) {
                event.preventDefault();
                event.stopPropagation();
            }
            form.classList.add('was-validated');
        });
    });
})();
</script>

</body>
</html>