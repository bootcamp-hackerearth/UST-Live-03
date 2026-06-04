<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Product</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            min-height: 100vh;
            background: linear-gradient(135deg, #f3f4f6, #e5e7eb, #f9fafb);
            display: flex;
            justify-content: center;
            align-items: center;
            font-family: 'Segoe UI', sans-serif;
        }
        .card {
            width: 450px;
            border-radius: 15px;
            border: 1px solid #e5e7eb;
            background: rgba(255, 255, 255, 0.9);
            box-shadow: 0 10px 25px rgba(0, 0, 0, 0.08);
        }
        .form-control, .form-select {
            border-radius: 8px;
        }
        .btn-primary {
            background: #3b82f6;
            border: none;
        }
        .error-message {
            color: red;
            text-align: center;
            margin-bottom: 10px;
        }
    </style>
</head>

<body>

<div class="card shadow-lg">
    <div class="card-body">

        <h4 class="text-center mb-4">Update Product</h4>

        <c:if test="${not empty message}">
            <div class="error-message">${message}</div>
        </c:if>
        <c:if test="${not empty product}">
            <form:form action="${pageContext.request.contextPath}/product/update"
                       method="post"
                       modelAttribute="product">
                <div class="mb-3">
                    <label class="form-label">Product ID</label>
                    <form:input path="identifier"
                                cssClass="form-control"
                                readonly="true"/>
                </div>
                <div class="mb-3">
                    <label class="form-label">Product Name</label>
                    <form:input path="name"
                                cssClass="form-control"
                                required="true"/>
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
                    <label class="form-label">Category</label>
                    <form:select path="category"
                                 cssClass="form-select"
                                 required="true">
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
                <button type="submit" class="btn btn-primary w-100">
                    Update Product
                </button>
            </form:form>
        </c:if>
    </div>
</div>
</body>
</html>