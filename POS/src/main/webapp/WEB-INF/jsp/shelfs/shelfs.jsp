<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Shelf Management | Edit Shelf</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <style>
        body {
            background-color: #f4f7f6;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            min-height: 100vh;
            display: flex;
            align-items: center;
        }

        .container {
            margin-top: 30px;
            margin-bottom: 30px;
        }

        .card-edit {
            background: #ffffff;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            overflow: hidden;
            border: none;
        }

        .card-header {
            background-color: #0d6efd; /* Primary Blue matching Warehouse style */
            color: white;
            padding: 20px;
            border: none;
        }

        .form-label {
            font-weight: 600;
            color: #495057;
        }

        .form-control, .form-select {
            border-radius: 8px;
            padding: 10px 12px;
        }

        .readonly-field {
            background-color: #e9ecef;
            cursor: not-allowed;
        }

        .btn-update {
            padding: 10px 25px;
            border-radius: 8px;
            font-weight: 600;
        }

        .identifier-badge {
            background-color: #eef3ff;
            color: #0d6efd;
            border: 1px dashed #0d6efd;
            padding: 5px 15px;
            border-radius: 6px;
            display: inline-block;
            font-weight: 700;
        }
    </style>
</head>

<body>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-6">

            <div class="card card-edit">
                <div class="card-header text-center">
                    <h3 class="mb-0"><i class="bi bi-pencil-square me-2"></i> Edit Shelf</h3>
                </div>

                <div class="card-body p-4">

                    <c:if test="${not empty message}">
                        <div class="alert ${success ? 'alert-success' : 'alert-danger'} alert-dismissible fade show text-center" role="alert">
                            <i class="bi ${success ? 'bi-check-circle-fill' : 'bi-exclamation-triangle-fill'} me-2"></i>
                            ${message}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>

                    <form:form action="${pageContext.request.contextPath}/shelfs/update"
                               method="post"
                               modelAttribute="shelves">

                        <form:hidden path="id" />

                        <div class="mb-4 text-center">
                            <label class="form-label d-block text-start">Shelf Identifier</label>
                            <div class="input-group">
                                <i class="bi bi-tag-fill me-2"></i>
                                <c:out value="${shelves.identifier}" />
                            </div>
                            <form:hidden path="identifier" />
                            <div class="form-text text-start mt-1">Unique identifier used for tracking.</div>
                        </div>

                        <div class="mb-4">
                            <label class="form-label">Shelf Availability Status</label>
                            <div class="input-group">
                                <span class="input-group-text bg-light"><i class="bi bi-toggle-on"></i></span>
                                <form:select path="status" class="form-select">
                                    <form:option value="true">Active</form:option>
                                    <form:option value="false">Inactive</form:option>
                                </form:select>
                            </div>
                        </div>

                        <div class="d-flex justify-content-between align-items-center pt-2">
                            <a href="${pageContext.request.contextPath}/shelfs/list" class="btn btn-outline-secondary">
                                <i class="bi bi-x-circle"></i> Cancel
                            </a>
                            <button type="submit" class="btn btn-success btn-update">
                                <i class="bi bi-save me-1"></i> Update Shelf
                            </button>
                        </div>

                    </form:form>
                </div>

                <div class="card-footer text-center text-muted small bg-light p-3">
                    Shelf Management Module &copy; 2026
                </div>
            </div>

        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>