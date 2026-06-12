<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Add Warehouse</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"
          rel="stylesheet">

    <style>
        body {
            background-color: #f7f9fc;
            color: #1e293b;
        }

        /* Common page header */
        .page-header {
            background: linear-gradient(to right, #0f766e, #134e4a);
            color: #ffffff;
            padding: 20px;
            border-radius: 10px;
            margin: 20px auto;
            max-width: 480px;
            text-align: center;
        }

        .form-wrapper {
            max-width: 480px;
            margin: 0 auto;
        }

        .card-custom {
            border-radius: 12px;
            box-shadow: 0 10px 28px rgba(0,0,0,0.08);
            border: none;
        }

        label {
            font-weight: 600;
            font-size: 14px;
        }

        .form-control,
        .form-select {
            border-radius: 8px;
            padding: 10px 12px;
        }

        .btn-gradient {
            background: linear-gradient(to right, #0f766e, #134e4a);
            color: #ffffff;
            border: none;
            padding: 10px;
        }

        .btn-gradient:hover {
            background: linear-gradient(to right, #134e4a, #0f766e);
            color: #ffffff;
        }
    </style>
</head>

<body class="container py-4">

<div class="page-header">
    <h4 class="mb-0">
        <i class="bi bi-buildings-fill me-2"></i> Add Warehouse
    </h4>
</div>

<div class="form-wrapper">
    <div class="card card-custom">
        <div class="card-body p-4">

            <!-- Error Message -->
            <c:if test="${not empty message}">
                <div class="alert alert-danger text-center">
                    <i class="bi bi-exclamation-triangle me-1"></i>
                    ${message}
                </div>
            </c:if>

            <form method="post"
                  action="${pageContext.request.contextPath}/warehouse/add">

                <!-- Warehouse Name -->
                <div class="mb-3">
                    <label>Warehouse Name</label>
                    <input type="text"
                           name="identifier"
                           class="form-control"
                           placeholder="Enter warehouse name"
                           required>
                </div>

                <!-- Location -->
                <div class="mb-3">
                    <label>Location</label>
                    <input type="text"
                           name="location"
                           class="form-control"
                           placeholder="City / Address"
                           required>
                </div>

                <!-- Capacity -->
                <div class="mb-3">
                    <label>Capacity</label>
                    <input type="number"
                           name="capacity"
                           class="form-control"
                           min="1"
                           placeholder="Capacity"
                           required>
                </div>

                <!-- Status -->
                <div class="mb-4">
                    <label>Status</label>
                    <select name="status" class="form-select" required>
                        <option value="true" selected>ACTIVE</option>
                        <option value="false">INACTIVE</option>
                    </select>
                </div>

                <!-- Buttons -->
                <div class="d-flex justify-content-end gap-2 mt-4">
                    <a href="${pageContext.request.contextPath}/warehouse/list"
                       class="btn btn-outline-secondary">
                        Cancel
                    </a>

                    <button type="submit" class="btn btn-gradient">
                        <i class="bi bi-check-circle me-1"></i>
                        Save Warehouse
                    </button>
                </div>

            </form>

        </div>
    </div>
</div>

</body>
</html>