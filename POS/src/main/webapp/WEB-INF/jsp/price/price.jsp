<%@ page language="java"
         contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form"
           prefix="form" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core"
           prefix="c" %>

<!DOCTYPE html>

<html lang="en">

<head>

    <meta charset="UTF-8">

    <title>Edit Price</title>

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
            Edit Price
        </h3>

        <c:if test="${not empty message}">

            <div class="alert alert-danger text-center">
                ${message}
            </div>

        </c:if>

        <form:form method="post"
                   action="${pageContext.request.contextPath}/price/update"
                   modelAttribute="priceDto">

            <form:hidden path="identifier"/>

            <form:hidden path="productId"/>

            <form:hidden path="productName"/>

            <form:hidden path="priceType"/>

            <div class="mb-3">

                <label class="form-label fw-semibold">
                    Identifier
                </label>

                <input type="text"
                       class="form-control"
                       value="${priceDto.identifier}"
                       readonly>

            </div>

            <div class="mb-3">

                <label class="form-label fw-semibold">
                    Product ID
                </label>

                <input type="text"
                       class="form-control"
                       value="${priceDto.productId}"
                       readonly>

            </div>

            <div class="mb-3">

                <label class="form-label fw-semibold">
                    Product Name
                </label>

                <input type="text"
                       class="form-control"
                       value="${priceDto.productName}"
                       readonly>

            </div>

            <div class="mb-3">

                <label class="form-label fw-semibold">
                    Price Type
                </label>

                <input type="text"
                       class="form-control"
                       value="${priceDto.priceType}"
                       readonly>

            </div>

            <div class="mb-4">

                <label class="form-label fw-semibold">
                    Value
                </label>

                <form:input path="value"
                            type="number"
                            step="0.01"
                            cssClass="form-control"
                            placeholder="Enter value"
                            required="required"/>

            </div>

            <div class="d-flex gap-2">

                <button type="submit"
                        class="btn btn-primary w-100">

                    Update

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