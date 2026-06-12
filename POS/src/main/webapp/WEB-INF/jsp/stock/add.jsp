<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS | Add Stock</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <style>
        body { background: #f4f7f6; min-height: 100vh; display: flex; align-items: center; font-family: 'Segoe UI', sans-serif; }
        .card { border-radius: 15px; border: none; box-shadow: 0 10px 30px rgba(0,0,0,0.08); }
        .card-header { border-top-left-radius: 15px; border-top-right-radius: 15px; background-color: #0d6efd; }
        .form-label { font-weight: 700; color: #64748b; font-size: 0.85rem; text-transform: uppercase; margin-bottom: 8px; }
        .form-select, .form-control { border-radius: 8px; padding: 10px 12px; border: 1px solid #e2e8f0; }
        .form-select:focus, .form-control:focus { border-color: #0d6efd; box-shadow: 0 0 0 3px rgba(13, 110, 253, 0.1); }
        .input-group-text { background-color: #f8fafc; border-color: #e2e8f0; color: #94a3b8; }
    </style>
</head>
<body>

<div class="container py-5">
    <div class="row justify-content-center">
        <div class="col-md-7 col-lg-6">
            <div class="card">
                <div class="card-header text-white text-center py-4">
                    <h4 class="mb-0 fw-bold"><i class="bi bi-boxes me-2"></i> Register New Stock</h4>
                </div>
                <div class="card-body p-4 p-md-5">
                    <form action="${pageContext.request.contextPath}/stock/add" method="post">

                        <!-- PRODUCT SELECTION -->
                        <div class="mb-4">
                            <label class="form-label">Product / Item SKU</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-search"></i></span>
                                <select class="form-select" name="identifier" required>
                                    <option value="" disabled selected>Search Product Identifier...</option>
                                    <c:forEach var="p" items="${product}">
                                        <option value="${p.identifier}">${p.identifier} - ${p.description}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <!-- WAREHOUSE SELECTION -->
                        <div class="mb-4">
                            <label class="form-label">Warehouse Location</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-building"></i></span>
                                <select class="form-select" name="warehouse" required>
                                    <option value="" disabled selected>Assign Warehouse...</option>
                                    <c:forEach var="w" items="${warehouse}">
                                        <option value="${w.location}">${w.location}</option>
                                    </c:forEach>
                                </select>
                            </div>
                        </div>

                        <!-- QUANTITY & UNIT ROW -->
                        <div class="row g-3 mb-4">
                            <div class="col-md-6">
                                <label class="form-label">Quantity</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="bi bi-hash"></i></span>
                                    <input type="number" class="form-control" name="quantity" min="1" placeholder="0" required />
                                </div>
                            </div>

                            <!-- ✅ UNIT BLOCK -->
                            <div class="col-md-6">
                                <label class="form-label">Unit of Measure</label>
                                <div class="input-group">
                                    <span class="input-group-text"><i class="bi bi-rulers"></i></span>
                                    <select class="form-select" name="unit" required>
                                        <option value="" disabled selected>Select Unit</option>
                                        <c:forEach var="u" items="${units}">
                                            <option value="${u.identifier}">${u.identifier}</option>
                                        </c:forEach>
                                    </select>
                                </div>
                            </div>
                        </div>

                        <!-- STATUS BLOCK -->
                        <div class="mb-5">
                            <label class="form-label">Inventory Availability</label>
                            <div class="input-group">
                                <span class="input-group-text"><i class="bi bi-info-circle"></i></span>
                                <select class="form-select" name="stockStatus" required>
                                    <option value="ACTIVE">ACTIVE</option>
                                    <option value="INACTIVE">INACTIVE</option>
                                </select>
                            </div>
                        </div>

                        <!-- ACTION BUTTONS -->
                        <div class="d-flex gap-3">
                            <a href="${pageContext.request.contextPath}/stock/list" class="btn btn-outline-secondary w-50 py-2 fw-bold">
                                Cancel
                            </a>
                            <button type="submit" class="btn btn-primary w-50 py-2 fw-bold shadow-sm">
                                <i class="bi bi-check2-circle me-2"></i>Save Stock
                            </button>
                        </div>
                    </form>
                </div>
            </div>
            <p class="text-center mt-4 text-muted small">Stock Management System &copy; 2026</p>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>