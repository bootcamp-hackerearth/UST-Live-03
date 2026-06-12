<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Warehouse</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

    <style>
        body {
            background-color: #f5f6f7;
            font-family: "Segoe UI", Roboto, Arial;
        }

        .card {
            border-radius: 10px;
            border: 1px solid #ddd;
            max-width: 500px;
            margin: 60px auto;
        }

        .card-header {
            background-color: #000;
            color: #fff;
            text-align: center;
            font-weight: 600;
            padding: 12px;
        }

        .form-label {
            font-weight: 600;
            font-size: 14px;
        }

        .form-control,
        .form-select {
            border-radius: 6px;
        }
    </style>
</head>

<body>

<div class="card shadow">

    <div class="card-header">
        Edit Warehouse
    </div>

    <div class="card-body">

        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>

        <form method="post"
              action="${pageContext.request.contextPath}/warehouse/update">

            <!-- Hidden ID -->
            <input type="hidden" name="id" value="${warehouse.id}"/>

            <!-- Identifier -->
            <div class="mb-3">
                <label class="form-label">Warehouse Name</label>
                <input type="text"
                       name="identifier"
                       class="form-control"
                       value="${warehouse.identifier}"
                       readonly>
            </div>

            <!-- Location -->
            <div class="mb-3">
                <label class="form-label">Location</label>
                <input type="text"
                       name="location"
                       class="form-control"
                       value="${warehouse.location}"
                       required>
            </div>

            <!-- Capacity -->
            <div class="mb-3">
                <label class="form-label">Capacity</label>
                <input type="number"
                       name="capacity"
                       class="form-control"
                       min="1"
                       value="${warehouse.capacity}"
                       required>
            </div>

            <!-- Status -->
            <div class="mb-4">
                <label class="form-label">Status</label>
                <select name="status" class="form-select">
                    <option value="true"
                        <c:if test="${warehouse.status}">selected</c:if>>
                        Active
                    </option>
                    <option value="false"
                        <c:if test="${!warehouse.status}">selected</c:if>>
                        Inactive
                    </option>
                </select>
            </div>

            <div class="d-grid gap-2">

                <button type="submit" class="btn btn-success">
                    <i class="bi bi-check-circle"></i> Update Warehouse
                </button>

                <a href="${pageContext.request.contextPath}/warehouse/list"
                   class="btn btn-secondary">
                    Back
                </a>

            </div>

        </form>

    </div>
</div>

</body>
</html>