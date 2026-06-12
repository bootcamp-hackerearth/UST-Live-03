<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Price Management | Add Price</title>

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
            box-shadow: 0 0 0 0.25 red rgba(13, 110, 253, 0.1);
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
                    <h3 class="mb-0"><i class="bi bi-tag-fill me-2"></i> Add New Price</h3>
                </div>

                <div class="card-body p-4">

                    <c:if test="${not empty message}">
                        <div class="alert ${success ? 'alert-success' : 'alert-danger'} alert-dismissible fade show text-center" role="alert">
                            ${message}
                            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                        </div>
                    </c:if>

                    <form action="${pageContext.request.contextPath}/price/add" method="post">

                        <input type="hidden" name="id" value="${price.id}" />

                        <div class="mb-3">
                                                    <label class="form-label">Product Name</label>
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
                                    <span class="input-group-text bg-light"></span>
                                    <input type="number"
                                           step="0.01"
                                           class="form-control"
                                           name="mrp"
                                           placeholder="0.00"
                                           required />
                                </div>
                            </div>

                            <div class="col-md-6 mb-3">
                                <label class="form-label">Selling Price</label>
                                <div class="input-group">
                                    <span class="input-group-text bg-light"></span>
                                    <input type="number"
                                           step="0.01"
                                           class="form-control"
                                           name="sellingPrice"
                                           placeholder="0.00"
                                           required />
                                </div>
                            </div>
                        </div>

                        <div class="mb-4">
                            <label class="form-label">Effective From</label>
                            <div class="input-group">
                                <span class="input-group-text bg-light"><i class="bi bi-calendar-event"></i></span>
                                <input type="datetime-local"
                                       class="form-control"
                                       name="effectiveFrom"
                                       required />
                            </div>
                            <div class="form-text">Choose the date and time this price becomes active.</div>
                        </div>

                        <div class="d-flex justify-content-between align-items-center">
                            <a href="${pageContext.request.contextPath}/price/list" class="text-decoration-none text-muted">
                                <i class="bi bi-arrow-left"></i> Back to List
                            </a>
                            <button type="submit" class="btn btn-primary btn-submit">
                                <i class="bi bi-check-circle me-1"></i> Save Price
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