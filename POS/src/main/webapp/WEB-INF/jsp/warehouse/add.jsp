<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Warehouse</title>

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

        .form-control {
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
                    Add Warehouse
                </div>

                <div class="card-body">

                    <c:if test="${not empty message}">
                        <div class="alert alert-danger">
                            ${message}
                        </div>
                    </c:if>

                    <form method="post"
                          action="${pageContext.request.contextPath}/warehouse/add"
                          class="needs-validation"
                          novalidate>

                        <div class="mb-3">
                            <label class="form-label">Warehouse Name</label>
                            <input type="text"
                                   name="identifier"
                                   class="form-control"
                                   placeholder="Warehouse Name"
                                   required>
                            <div class="invalid-feedback">
                                Warehouse name is required.
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Location</label>
                            <input type="text"
                                   name="location"
                                   class="form-control"
                                   placeholder="City / Address"
                                   required>
                            <div class="invalid-feedback">
                                Location is required.
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Capacity</label>
                            <input type="number"
                                   name="capacity"
                                   class="form-control"
                                   min="1"
                                   required>
                            <div class="invalid-feedback">
                                Capacity must be greater than zero.
                            </div>
                        </div>

                        <div class="mb-4">
                            <label class="form-label">Status</label>
                            <select name="status" class="form-select">
                                <option value="true" selected>Active</option>
                                <option value="false">Inactive</option>
                            </select>
                        </div>

                        <div class="d-grid gap-2">
                            <button type="submit" class="btn btn-success">
                                <i class="bi bi-check-circle"></i> Save Warehouse
                            </button>
                            <a href="${pageContext.request.contextPath}/warehouse/list"
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