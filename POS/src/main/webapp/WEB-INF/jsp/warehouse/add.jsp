<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Warehouse Management | Add Warehouse</title>

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

        .card-add {
            background: #ffffff;
            border-radius: 15px;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            overflow: hidden;
            border: none;
        }

        .card-header {
            background-color: #0d6efd;
            color: white;
            padding: 20px;
            border: none;
        }

        .form-label {
            font-weight: 600;
            color: #495057;
        }

        .form-control {
            border-radius: 8px;
            padding: 10px 12px;
        }

        .form-control:focus {
            box-shadow: 0 0 0 0.25rem rgba(13, 110, 253, 0.1);
            border-color: #0d6efd;
        }

        .btn-submit {
            padding: 10px 25px;
            border-radius: 8px;
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="container">
    <div class="row justify-content-center">
        <div class="col-md-6">

            <div class="card card-add">
                <div class="card-header text-center">
                    <h3 class="mb-0"><i class="bi bi-building-add me-2"></i> Add Warehouse</h3>
                </div>

                <div class="card-body p-4">

                    <c:if test="${not empty message}">
                        <div class="alert ${success ? 'alert-success' : 'alert-danger'} alert-dismissible fade show text-center" role="alert">
                            ${message}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/warehouse/add" method="post">

                        <input type="hidden" name="id" value="${warehouse.id}" />

                        <div class="mb-3">
                            <label class="form-label">Warehouse Identifier</label>
                            <div class="input-group">
                                <span class="input-group-text bg-light"><i class="bi bi-tag"></i></span>
                                <input type="text"
                                       class="form-control"
                                       name="identifier"
                                       placeholder="e.g. WH-NORTH-01"
                                       required />
                            </div>
                        </div>

                        <div class="mb-3">
                            <label class="form-label">Location Address / Name</label>
                            <div class="input-group">
                                <span class="input-group-text bg-light"><i class="bi bi-geo-alt"></i></span>
                                <input type="text"
                                       class="form-control"
                                       name="location"
                                       placeholder="e.g. Industrial Area, Sector 5"
                                       required />
                            </div>
                        </div>

                        <div class="mb-4">
                            <label class="form-label">Manager In-Charge</label>
                            <div class="input-group">
                                <span class="input-group-text bg-light"><i class="bi bi-person-badge"></i></span>
                                <input type="text"
                                       class="form-control"
                                       name="manager"
                                       placeholder="Enter manager's full name"
                                       required />
                            </div>
                        </div>

                        <div class="d-flex justify-content-between align-items-center">
                            <a href="${pageContext.request.contextPath}/warehouse/list" class="text-decoration-none text-muted">
                                <i class="bi bi-arrow-left"></i> Back to List
                            </a>
                            <button type="submit" class="btn btn-primary btn-submit">
                                <i class="bi bi-plus-circle me-1"></i> Create Warehouse
                            </button>
                        </div>

                    </form>
                </div>

                <div class="card-footer text-center text-muted small bg-light p-3">
                    Warehouse Management System &copy; 2026
                </div>
            </div>

        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>