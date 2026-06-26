<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Model</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css">

    <style>
        body {
            background: linear-gradient(to right, #eef2f7, #f9fbfd);
            font-family: "Segoe UI", Roboto, Arial;
        }

        .card {
            border-radius: 14px;
            max-width: 520px;
            margin: 70px auto;
            border: none;
            box-shadow: 0 8px 22px rgba(0,0,0,0.08);
        }

        .card-header {
            background: linear-gradient(to right, #000000, #343a40);
            color: #fff;
            text-align: center;
            font-weight: 600;
            padding: 16px;
            border-top-left-radius: 14px;
            border-top-right-radius: 14px;
            font-size: 18px;
        }

        .card-body {
            padding: 25px;
        }

        label {
            font-weight: 600;
            margin-bottom: 5px;
        }

        .form-control,
        .form-select {
            border-radius: 8px;
            padding: 10px;
        }

        .form-control:focus,
        .form-select:focus {
            border-color: #4e73df;
            box-shadow: none;
        }

        input[readonly] {
            background-color: #e9ecef;
            cursor: not-allowed;
        }

        .btn-primary {
            border-radius: 8px;
            padding: 10px;
        }

        .btn-secondary {
            border-radius: 8px;
        }
    </style>
</head>

<body>

<div class="card">
    <div class="card-header">
        <i class="bi bi-box-seam me-2"></i> Edit Model
    </div>

    <div class="card-body">

        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/model/update" method="post">

            <input type="hidden" name="id" value="${model.id}"/>

            <div class="mb-3">
                <label>Model Name</label>
                <input type="text"
                       name="identifier"
                       class="form-control"
                       value="${model.identifier}"
                       readonly>
            </div>

            <div class="mb-4">
                <label>Status</label>
                <select name="status" class="form-select" required>
                    <option value="true" ${model.status ? "selected" : ""}>Active</option>
                    <option value="false" ${!model.status ? "selected" : ""}>Inactive</option>
                </select>
            </div>

            <div class="d-grid gap-2">
                <button type="submit" class="btn btn-success">
                    <i class="bi bi-save me-1"></i> Update Model
                </button>

                <a href="${pageContext.request.contextPath}/model/list"
                   class="btn btn-secondary">
                    Cancel
                </a>
            </div>

        </form>

    </div>
</div>

</body>
</html>