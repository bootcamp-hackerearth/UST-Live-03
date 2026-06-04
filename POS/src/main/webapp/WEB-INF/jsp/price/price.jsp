<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Price</title>

    <!-- Bootstrap CSS -->
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"/>

    <!-- Bootstrap Icons -->
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.3/font/bootstrap-icons.css"/>

    <style>
        body {
            background: linear-gradient(to right, #eef2f7, #f9fbfd);
            font-family: "Segoe UI", Roboto, Arial;
        }

        .card {
            max-width: 500px;
            margin: 70px auto;
            border-radius: 12px;
            border: none;
            box-shadow: 0 8px 22px rgba(0, 0, 0, 0.08);
        }

        .card-header {
            background: linear-gradient(to right, #000, #343a40);
            color: #fff;
            text-align: center;
            font-weight: 600;
            padding: 16px;
            font-size: 17px;
        }

        .card-body {
            padding: 25px;
        }

        label {
            font-weight: 600;
            font-size: 14px;
            margin-bottom: 4px;
        }

        .form-control,
        .form-select {
            border-radius: 8px;
            padding: 10px;
        }

        .readonly-field {
            background-color: #e9ecef;
            cursor: not-allowed;
        }

        .btn-primary,
        .btn-outline-secondary {
            border-radius: 8px;
        }
    </style>
</head>

<body>

<div class="card">

    <div class="card-header">
        <i class="bi bi-currency-rupee me-2"></i> Update Price
    </div>

    <div class="card-body">

        <form:form method="post"
                   action="${pageContext.request.contextPath}/price/update"
                   modelAttribute="price">

            <form:hidden path="id"/>
            <form:hidden path="identifier"/>

            <div class="mb-3">
                <label>Identifier</label>
                <input type="text"
                       class="form-control readonly-field"
                       value="${price.identifier}"
                       readonly>
            </div>

            <div class="mb-3">
                <label>Product</label>
                <form:select path="product"
                             cssClass="form-select"
                             disabled="true">
                    <form:option value="">-- Select Product --</form:option>
                    <form:options items="${products}"
                                  itemValue="identifier"
                                  itemLabel="identifier"/>
                </form:select>
            </div>

            <div class="mb-3">
                <label> Price Amount</label>
                <form:input path="priceAmount"
                            type="number"
                            min="0"
                            step="0.01"
                            cssClass="form-control"
                            placeholder="Enter cost price"
                            required="required"/>
            </div>

            <div class="mb-3">
                <label>Price Type</label>
                <form:select path="type"
                             cssClass="form-select"
                             disabled="true">
                    <form:option value="MRP">MRP</form:option>
                    <form:option value="SELLING">Selling Price</form:option>
                </form:select>
            </div>

            <div class="d-grid gap-2 mt-4">
                <button type="submit" class="btn btn-success">
                    <i class="bi bi-save me-1"></i> Update Price
                </button>

                <a href="${pageContext.request.contextPath}/price/list"
                   class="btn btn-outline-secondary">
                    Cancel
                </a>
            </div>

        </form:form>

    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>