<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Rack</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"
          rel="stylesheet">

    <style>
        body {
            background-color: #f7f9fc;
        }

        .page-header {
            background: linear-gradient(to right, #0f766e, #134e4a);
            color: #ffffff;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 20px;
            max-width: 480px;
            margin-left: auto;
            margin-right: auto;
            text-align: center;
        }

        .form-wrapper {
            max-width: 480px;
            margin: 0 auto;
        }

        label {
            font-weight: 600;
            font-size: 14px;
        }

        .form-control,
        .form-select {
            border-radius: 8px;
        }

        .btn-gradient {
            background: linear-gradient(to right, #0f766e, #134e4a);
            color: #ffffff;
            border: none;
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
        <i class="bi bi-columns-gap me-2"></i> Add Rack
    </h4>
</div>

<div class="form-wrapper">
    <div class="card shadow-sm">
        <div class="card-body">

            <!-- Error Message -->
            <c:if test="${not empty message}">
                <div class="alert alert-danger text-center">
                    ${message}
                </div>
            </c:if>

            <form action="${pageContext.request.contextPath}/racks/add"
                  method="post">

                <div class="mb-3">
                    <label>Rack Name</label>
                    <input type="text"
                           name="identifier"
                           class="form-control"
                           placeholder="Enter rack name"
                           required>
                </div>

                <div class="mb-3">
                    <label>Assign Shelf</label>
                    <select name="shelfIdentifier"
                            class="form-select"
                            required>
                        <option value="">-- Select Shelf --</option>
                        <c:forEach items="${shelfs}" var="shelf">
                            <option value="${shelf.identifier}">
                                ${shelf.identifier}
                            </option>
                        </c:forEach>
                    </select>

                    <c:if test="${empty shelfs}">
                        <small class="text-muted">
                            No shelves available
                        </small>
                    </c:if>
                </div>

                <div class="mb-3">
                    <label>Rack Status</label>
                    <select name="status"
                            class="form-select"
                            required>
                        <option value="true" selected>ACTIVE</option>
                        <option value="false">INACTIVE</option>
                    </select>
                </div>

                <div class="d-flex justify-content-end gap-2 mt-4">
                    <a href="${pageContext.request.contextPath}/racks/list"
                       class="btn btn-outline-secondary">
                        Cancel
                    </a>

                    <button type="submit"
                            class="btn btn-gradient">
                        <i class="bi bi-save me-1"></i> Save Rack
                    </button>
                </div>

            </form>

        </div>
    </div>
</div>

</body>
</html>
