<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Price</title>

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

<div class="page-header text-center">
    <h4 class="mb-0">
        <i class="bi bi-currency-rupee me-2"></i> Update Price
    </h4>
</div>

<div class="form-wrapper">
    <div class="card shadow-sm">
        <div class="card-body">

            <form:form method="post"
                       action="${pageContext.request.contextPath}/price/update"
                       modelAttribute="price">

                <form:hidden path="id"/>
                <form:hidden path="identifier"/>

                <div class="mb-3">
                    <label>Product</label>
                    <input type="text"
                           class="form-control"
                           value="${price.identifier}"
                           readonly>
                </div>

                <div class="mb-3">
                    <label>Cost Price</label>
                    <form:input path="costPrice"
                                type="number"
                                min="0"
                                step="0.01"
                                cssClass="form-control"
                                placeholder="Enter cost price"/>
                </div>

                <div class="mb-3">
                    <label>Price Type</label>
                    <form:select path="type"
                                 cssClass="form-select"
                                 required="true">
                        <form:option value="MRP">MRP</form:option>
                        <form:option value="SELLING">Selling Price</form:option>
                    </form:select>
                </div>

                <div class="d-flex justify-content-end gap-2 mt-4">
                    <a href="${pageContext.request.contextPath}/price/list"
                       class="btn btn-outline-secondary">
                        Cancel
                    </a>

                    <button type="submit" class="btn btn-gradient">
                        <i class="bi bi-save me-1"></i> Update Price
                    </button>
                </div>

            </form:form>

        </div>
    </div>
</div>

</body>
</html>