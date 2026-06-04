<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <title>Add Price</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>

        body {
            background-color: #E9EEF5;
            min-height: 100vh;
        }

        .card {
            border-radius: 16px;
        }

        .form-control,
        .form-select {
            border-radius: 10px;
        }

        .btn {
            border-radius: 10px;
        }

        .form-check {
            margin-bottom: 6px;
        }

        .category-box {
            max-height: 150px;
            overflow-y: auto;
            border: 1px solid #ced4da;
            border-radius: 10px;
            padding: 10px;
            background: #fff;
        }

    </style>

</head>

<body>

<nav class="navbar navbar-dark bg-dark shadow">

    <div class="container-fluid">

        <span class="navbar-brand fw-bold">
            Price Management
        </span>

        <a href="${pageContext.request.contextPath}/price/list"
           class="btn btn-outline-light btn-sm">

            Back

        </a>

    </div>

</nav>

<div class="container d-flex justify-content-center align-items-center"
     style="min-height: 100vh;">

    <div class="card shadow p-4"
         style="width: 500px;">

        <h3 class="text-center mb-4 fw-bold">
            Add Price
        </h3>

        <c:if test="${not empty message}">

            <div class="alert alert-danger text-center">
                ${message}
            </div>

        </c:if>

        <form:form method="post"
                   action="${pageContext.request.contextPath}/price/add"
                   modelAttribute="priceDto">

            <div class="mb-3">

                <label class="form-label fw-semibold">
                    Product
                </label>

                <form:select path="productId"
                             cssClass="form-select"
                             required="true">

                    <form:option value="">
                        -- Select Product --
                    </form:option>

                    <c:forEach items="${products}" var="p">

                        <form:option value="${p.identifier}">
                            ${p.productName} (${p.identifier})
                        </form:option>

                    </c:forEach>

                </form:select>

            </div>

            <div class="mb-4">

                <label class="form-label fw-semibold">
                    Price Type
                </label>

                <div class="category-box">

                    <div class="form-check">

                        <form:radiobutton path="priceType"
                                          value="Cost Price"
                                          cssClass="form-check-input"
                                          required="true"/>

                        <label class="form-check-label">
                            Cost Price
                        </label>

                    </div>

                    <div class="form-check">

                        <form:radiobutton path="priceType"
                                          value="MRP"
                                          cssClass="form-check-input"/>

                        <label class="form-check-label">
                            MRP
                        </label>

                    </div>

                    <div class="form-check">

                        <form:radiobutton path="priceType"
                                          value="Selling Price"
                                          cssClass="form-check-input"/>

                        <label class="form-check-label">
                            Selling Price
                        </label>

                    </div>

                </div>

            </div>

            <div class="mb-3">

                <label class="form-label fw-semibold">
                    Value
                </label>

                <form:input path="value"
                            cssClass="form-control"
                            type="number"
                            step="0.01"
                            placeholder="Enter value"
                            required="true"/>

            </div>

            <div class="d-flex gap-2">

                <button type="submit"
                        class="btn btn-primary w-100">
                    Save Price
                </button>

                <a href="${pageContext.request.contextPath}/price/list"
                   class="btn btn-outline-secondary w-100">
                    Cancel
                </a>

            </div>

        </form:form>

    </div>

</div>

</body>

</html>