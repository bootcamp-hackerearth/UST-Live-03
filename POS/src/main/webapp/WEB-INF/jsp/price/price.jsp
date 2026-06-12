<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Price Management | Edit Price</title>

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
            background-color: #0d6efd; /* Same blue as List header */
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

        .readonly-field {
            background-color: #e9ecef;
            cursor: not-allowed;
        }

        .btn-update {
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

            <div class="card card-edit">
                <div class="card-header text-center">
                    <h3 class="mb-0"><i class="bi bi-pencil-square me-2"></i> Edit Price Details</h3>
                </div>

                <div class="card-body p-4">

                    <c:if test="${not empty message}">
                        <div class="alert ${success ? 'alert-success' : 'alert-danger'} alert-dismissible fade show text-center" role="alert">
                            ${message}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/price/update" method="post">

                        <input type="hidden" name="id" value="${price.id}" />

                        <div class="mb-3">
                                                    <label class="form-label">Product Name </label>
                                                    <select class="form-select" name="identifier" required>
                                                        <option value="" disabled selected>-- Select Product --</option>
                                                        <c:forEach var="p" items="${product}">
                                                            <option value="${p.identifier}">
                                                                ${p.identifier}
                                                            </option>
                                                        </c:forEach>
                                                    </select>
                                                    <div class="form-text">Select the product this stock belongs to.</div>
                                                </div>

                        <div class="row">
                            <div class="col-md-6 mb-3">
                                <label class="form-label">MRP</label>
                                <div class="input-group">
                                    <span class="input-group-text">$</span>
                                    <input type="number"
                                           class="form-control"
                                           name="mrp"
                                           value="${price.mrp}"
                                           required />
                                </div>
                            </div>

                            <div class="col-md-6 mb-3">
                                <label class="form-label">Selling Price</label>
                                <div class="input-group">
                                    <span class="input-group-text">$</span>
                                    <input type="number"
                                           class="form-control"
                                           name="sellingPrice"
                                           value="${price.sellingPrice}"
                                           required />
                                </div>
                            </div>
                        </div>

                        <div class="mb-4">
                            <label class="form-label">Effective From</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-calendar-event"></i></span>
                                <input type="datetime-local"
                                       class="form-control"
                                       name="effectiveFrom"
                                       value="${price.effectiveFrom}"
                                       required />
                            </div>
                            <small class="text-muted">Current validity: ${price.effectiveFrom}</small>
                        </div>

                        <div class="d-flex justify-content-between align-items-center">
                            <a href="${pageContext.request.contextPath}/price/list" class="btn btn-outline-secondary">
                                <i class="bi bi-x-circle"></i> Cancel
                            </a>
                            <button type="submit" class="btn btn-success btn-update">
                                <i class="bi bi-save me-1"></i> Update Price
                            </button>
                        </div>

                    </form>
                </div>

                <div class="card-footer text-center text-muted small bg-light p-3">
                    Price & Inventory Management System &copy; 2024
                </div>
            </div>

        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>