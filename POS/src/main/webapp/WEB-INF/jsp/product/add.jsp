<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Product</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: linear-gradient(135deg, #f3f4f6, #e5e7eb, #f9fafb);
            min-height: 100vh;
            font-family: 'Segoe UI', sans-serif;
        }
        .card {
            border-radius: 14px;
            border: 1px solid #e5e7eb;
            background: rgba(255, 255, 255, 0.9);
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
        }
        .card-header {
            background: linear-gradient(135deg, #e5e7eb, #f3f4f6) !important;
            border-top-left-radius: 14px;
            border-top-right-radius: 14px;
        }
        .form-control, .form-select {
            border-radius: 8px;
        }
        .btn-primary {
            background: #3b82f6;
            border: none;
        }
        .error-message {
            color: #dc2626;
            text-align: center;
            margin-top: 10px;
        }
    </style>
</head>

<body>

<c:if test="${not empty message}">
    <div class="error-message">${message}</div>
</c:if>

<div class="container d-flex justify-content-center align-items-center mt-5">
    <div class="col-md-6">

        <div class="card shadow-lg">
            <div class="card-header text-center">
                <h4 class="mb-0">Add Product</h4>
            </div>
            <div class="card-body">
                <form:form
                        action="${pageContext.request.contextPath}/product/add"
                        method="post"
                        modelAttribute="productDto">

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Product ID</label>
                        <form:input path="identifier" cssClass="form-control" required="true"/>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Product Name</label>
                        <form:input path="name" cssClass="form-control" required="true"/>
                    </div>
                    <div class="mb-3">
                       <label class="form-label fw-semibold">Unit</label>
                       <form:select path="unit" cssClass="form-select" required="true">
                           <form:option value="" label="Select Unit"/>
                           <form:options items="${units}"
                                         itemValue="identifier"
                                         itemLabel="identifier"/>
                       </form:select>
                   </div>
                    <div class="mb-3">
                     <label class="form-label fw-semibold">Price</label>
                    <form:input path="price" type="number" step="0.01" cssClass="form-control" required="true"/>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Category</label>
                        <form:select path="category" cssClass="form-select" required="true">
                            <form:option value="" label="Select Category"/>
                            <form:options items="${categories}"
                                          itemValue="identifier"
                                          itemLabel="identifier"/>
                        </form:select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Brand</label>
                        <form:select path="brand" cssClass="form-select" required="true">
                            <form:option value="" label="Select Brand"/>
                            <form:options items="${brands}"
                                          itemValue="identifier"
                                          itemLabel="identifier"/>
                        </form:select>
                    </div>
                    <div class="mb-3">
                        <label class="form-label fw-semibold">Model</label>
                        <form:select path="models" cssClass="form-select" required="true">
                            <form:option value="" label="Select Model"/>
                            <form:options items="${models}"
                                          itemValue="identifier"
                                          itemLabel="identifier"/>
                        </form:select>
                    </div>
                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary btn-lg">
                            Save Product
                        </button>
                    </div>
                </form:form>
            </div>
            <div class="card-footer text-center small">
                POS Management System
            </div>
            <div class="text-center pb-3">
                <a href="${pageContext.request.contextPath}/product/list">
                    ← Back to List
                </a>
            </div>
        </div>
    </div>
</div>
</body>
</html>