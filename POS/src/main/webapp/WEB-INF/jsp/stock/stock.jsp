<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS | Edit Stock</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <style>
        :root {
            --primary-blue: #0d6efd;
            --bg-light: #f4f7f6;
            --text-dark: #2c3e50;
            --border-color: #e2e8f0;
        }

        body {
            background-color: var(--bg-light);
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            min-height: 100vh;
            display: flex;
            align-items: center;
        }

        .card {
            border-radius: 15px;
            border: none;
            box-shadow: 0 10px 30px rgba(0,0,0,0.08);
        }

        .card-header {
            border-top-left-radius: 15px;
            border-top-right-radius: 15px;
            background-color: var(--primary-blue);
            padding: 20px;
        }

        .form-label {
            font-weight: 700;
            color: #64748b;
            font-size: 0.85rem;
            text-transform: uppercase;
            margin-bottom: 8px;
        }

        .form-control:read-only {
            background-color: #f8fafc;
            color: #94a3b8;
            cursor: not-allowed;
        }

        .input-group-text {
            background-color: #f8fafc;
            color: #94a3b8;
            border-color: #e2e8f0;
        }
    </style>
</head>

<body>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-7 col-lg-6">

            <div class="card">
                <div class="card-header text-white text-center">
                    <h4 class="mb-0 fw-bold"><i class="bi bi-pencil-square me-2"></i>Edit Stock Entry</h4>
                </div>

                <div class="card-body p-4 p-md-5">
                    <form action="${pageContext.request.contextPath}/stock/update" method="post">

                        <input type="hidden" name="id" value="${stock.id}" />

                        <div class="mb-4">
                            <label class="form-label">Product Identifier</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-lock-fill"></i></span>
                                <input type="text" class="form-control" name="identifier" value="${stock.identifier}" readonly />
                            </div>
                        </div>

                        <div class="mb-4">
                            <label class="form-label">Warehouse Location</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-building"></i></span>
                                <select class="form-select" name="warehouse" required>
                                    <c:forEach var="w" items="${warehouse}">
                                        <option value="${w.location}" ${stock.warehouse == w.location ? 'selected' : ''}>
                                            ${w.location}
                                        </option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <div class="row g-3 mb-4">
                            <!-- QUANTITY -->
                            <div class="col-md-6">
                                <label class="form-label">Quantity</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="bi bi-hash"></i></span>
                                    <input type="number" class="form-control" name="quantity" value="${stock.quantity}" required />
                                </div>
                            </div>

                            <div class="col-md-6">
                                <label class="form-label">Unit of Measure</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="bi bi-rulers"></i></span>
                                    <select class="form-select" name="unit" required>
                                        <c:forEach var="u" items="${units}">
                                            <option value="${u.identifier}" ${stock.unit == u.identifier ? 'selected' : ''}>
                                                ${u.identifier}
                                            </option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                        </div>

                        <div class="d-flex gap-3 mt-4">
                            <a href="${pageContext.request.contextPath}/stock/list" class="btn btn-outline-secondary w-50 py-2 fw-bold">
                                Cancel
                            </a>
                            <button type="submit" class="btn btn-success w-50 py-2 fw-bold shadow-sm">
                                <i class="bi bi-check-lg me-2"></i>Update Stock
                            </button>
                        </div>

                    </form>
                </div>

                <div class="card-footer text-center text-muted small py-3">
                    Stock Management System &copy; 2026
                </div>
            </div>

        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>