<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Management System | Add Product</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">

    <style>
        body {
            min-height: 100vh;
            background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
            display: flex;
            justify-content: center;
            align-items: center;
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            margin: 0;
            padding: 20px;
        }

        .product-card {
            width: 100%;
            max-width: 500px;
            background: #ffffff;
            border-radius: 16px;
            box-shadow: 0 15px 35px rgba(0,0,0,0.3);
            overflow: hidden;
        }

        .card-header {
            background: #f8f9fa;
            border-bottom: 1px solid #eee;
            padding: 20px;
            text-align: center;
        }

        .card-header h3 {
            margin: 0;
            color: #333;
            font-weight: 700;
            font-size: 1.5rem;
        }

        .card-body {
            padding: 30px;
        }

        .form-label {
            font-weight: 600;
            color: #495057;
            font-size: 0.9rem;
        }

        .form-control {
            border-radius: 8px;
            padding: 10px 12px;
            border: 1px solid #ced4da;
            transition: border-color 0.2s, box-shadow 0.2s;
        }

        .form-control:focus {
            border-color: #2a5298;
            box-shadow: 0 0 0 3px rgba(42, 82, 152, 0.1);
        }

        .btn-submit {
            background: #28a745;
            color: white;
            border: none;
            padding: 10px 25px;
            border-radius: 8px;
            font-weight: 600;
            transition: background 0.3s;
        }

        .btn-submit:hover {
            background: #218838;
        }

        .btn-cancel {
            background: #6c757d;
            color: white;
            text-decoration: none;
            padding: 10px 25px;
            border-radius: 8px;
            font-size: 0.9rem;
            transition: opacity 0.3s;
        }

        .btn-cancel:hover {
            color: white;
            opacity: 0.85;
        }

        .alert {
            border-radius: 10px;
            font-size: 0.9rem;
        }
    </style>
</head>

<body>

<div class="product-card">
    <div class="card-header">
        <h3>Add New Product</h3>
    </div>

    <div class="card-body">

        <c:if test="${not empty message}">
            <div class="alert ${success ? 'alert-success' : 'alert-danger'} alert-dismissible fade show" role="alert">
                ${message}
                <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
            </div>
        </c:if>

        <form:form action="${pageContext.request.contextPath}/product/add"
                   method="post"
                   modelAttribute="productDto">

            <form:hidden path="id"/>

            <div class="mb-3">
                <label class="form-label">Product Identifier</label>
                <form:input path="identifier"
                            cssClass="form-control"
                            placeholder="e.g. SKU-9901"
                            required="true"/>
            </div>

            <div class="mb-3">
                <label class="form-label">Category</label>

                <form:select path="category" cssClass="form-select" required="true">
                    <option value="" disabled selected>-- Select Category Identifier --</option>

                    <c:forEach var="c" items="${category}">
                        <option value="${c.identifier}">
                            ${c.identifier}
                        </option>
                    </c:forEach>
                </form:select>

                <div class="form-text">Choose the category this product belongs to.</div>
            </div>
            <div class="col-md-4">
                                    <label class="form-label">Brand</label>
                                    <div class="input-group">
                                        <span class="input-group-text"><i class="bi bi-award"></i></span>
                                        <form:select path="brand" class="form-select">
                                            <form:option value="" label="-- Select Brand --"/>
                                            <c:forEach items="${brand}" var="b">
                                                <form:option value="${b.identifier}">${b.identifier}</form:option>
                                            </c:forEach>
                                        </form:select>
                                    </div>
                                </div>

                                <div class="col-md-4">
                                    <label class="form-label">Model</label>
                                    <div class="input-group">
                                        <span class="input-group-text"><i class="bi bi-cpu"></i></span>
                                        <form:select path="models" class="form-select">
                                            <form:option value="" label="-- Select Model --"/>
                                            <c:forEach items="${models}" var="m">
                                                <form:option value="${m.identifier}">${m.identifier}</form:option>
                                            </c:forEach>
                                        </form:select>
                                    </div>
                                </div>


            <div class="mb-4">
                <label class="form-label">Description</label>
                <form:textarea path="description"
                               cssClass="form-control"
                               rows="3"
                               placeholder="Brief product details..."/>
            </div>

            <div class="d-flex justify-content-between align-items-center">
                <a href="${pageContext.request.contextPath}/product/list" class="btn-cancel">
                    Back to List
                </a>
                <button type="submit" class="btn-submit">
                    Save Product
                </button>
            </div>

        </form:form>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>