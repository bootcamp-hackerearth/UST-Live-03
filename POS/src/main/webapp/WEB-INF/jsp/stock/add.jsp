<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Stock</title>

    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/bootstrap-icons.css">

    <style>
        :root {
            --primary-color: #4f46e5;
            --primary-hover: #4338ca;
        }

        body {
            margin: 0;
            font-family: "Segoe UI", Arial, sans-serif;
            background: linear-gradient(135deg, #f5f7fb, #eef2ff);
        }
        .back-link {
            text-decoration: none;
            font-weight: 500;
            color: var(--primary-color);
        }

        .back-link:hover {
            text-decoration: underline;
        }

        .stock-card {
            max-width: 520px;
            margin: auto;
            border-radius: 14px;
            border: none;
            box-shadow: 0 10px 25px rgba(0,0,0,0.08);
            overflow: hidden;
        }

        .card-header {
            background: var(--primary-color);
            color: #fff;
            padding: 18px;
            text-align: center;
        }

        .card-header h4 {
            margin-bottom: 4px;
            font-weight: 600;
        }

        .card-body {
            padding: 28px 32px;
        }

        .section-title {
            font-size: 13px;
            font-weight: 600;
            color: #6c757d;
            text-transform: uppercase;
            margin-top: 24px;
            margin-bottom: 10px;
        }

        .form-label {
            font-size: 14px;
            font-weight: 600;
        }

        .form-control,
        .form-select {
            border-radius: 8px;
            padding: 10px 12px;
            font-size: 14px;
        }

        .form-control:focus,
        .form-select:focus {
            border-color: var(--primary-color);
            box-shadow: none;
        }

        .error-msg {
            background: #fee2e2;
            color: #b91c1c;
            padding: 10px;
            text-align: center;
            border-radius: 8px;
            margin-bottom: 15px;
            font-size: 14px;
        }

        .btn-submit {
            padding: 12px;
            font-size: 16px;
            border-radius: 10px;
            background: var(--primary-color);
            border: none;
            font-weight: 600;
        }

        .btn-submit:hover {
            background: var(--primary-hover);
        }
    </style>
</head>

<body>

<div class="container mt-5">

    <div class="text-center mb-4">
        <a href="${pageContext.request.contextPath}/stock/list" class="back-link">
            <i class="bi bi-arrow-left"></i> Back to Stock List
        </a>
    </div>

    <div class="card stock-card">
        <div class="card-header">
            <h4><i class="bi bi-box-seam"></i> Add Stock</h4>
            <small>Assign product inventory to a warehouse</small>
        </div>

        <div class="card-body">

            <c:if test="${not empty message}">
                <div class="error-msg">
                    ${message}
                </div>
            </c:if>

            <form:form action="add" method="post" modelAttribute="stocks">
                <div class="section-title">Product Selection</div>
                <div class="mb-3">
                    <label class="form-label">Product</label>
                    <form:select path="productIdentifier" class="form-select" required="true">
                        <form:option value="">-- Select Product --</form:option>
                        <c:forEach items="${products}" var="p">
                            <form:option value="${p.identifier}">
                                ${p.identifier}
                            </form:option>
                        </c:forEach>
                    </form:select>
                </div>

                <div class="section-title">Warehouse</div>
                <div class="mb-3">
                    <label class="form-label">Warehouse</label>
                    <form:select path="warehouseIdentifier" class="form-select" required="true">
                        <form:option value="">-- Select Warehouse --</form:option>
                        <c:forEach items="${warehouses}" var="w">
                            <form:option value="${w.identifier}">
                                ${w.identifier}
                            </form:option>
                        </c:forEach>
                    </form:select>
                </div>
                <div class="section-title">Stock Details</div>
                <div class="mb-3">
                    <label class="form-label">Quantity Available</label>
                    <form:input path="quantity"
                                type="number"
                                class="form-control"
                                placeholder="Enter quantity"
                                required="true"/>
                </div>

                <div class="mb-4">
                    <label class="form-label">Minimum Stock Level</label>
                    <form:input path="minimumStock"
                                type="number"
                                class="form-control"
                                placeholder="Minimum threshold"
                                required="true"/>
                </div>

                <button type="submit" class="btn btn-submit w-100">
                    <i class="bi bi-plus-circle"></i>
                    Add Stock
                </button>

            </form:form>

        </div>
    </div>

</div>

</body>
</html>