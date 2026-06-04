<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Price</title>

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
        h4 {
            font-weight: 600;
            color: #111827;
        }
        .form-label {
            color: #374151;
            font-weight: 500;
        }
        .form-control, .form-select {
            border: 1px solid #d1d5db;
            border-radius: 8px;
        }
        .btn-primary {
            background: #3b82f6;
            border: none;
        }
        .btn-primary:hover {
            background: #2563eb;
        }
        .btn-outline-secondary {
            color: #374151;
            border-color: #9ca3af;
        }
        .btn-outline-secondary:hover {
            background: #e5e7eb;
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

        <h4 class="text-center mb-4">Update Price</h4>

        <c:if test="${not empty message}">
            <div class="error-message">${message}</div>
        </c:if>
        <c:if test="${empty price}">
            <div class="alert alert-danger text-center">
                Price not found
            </div>
        </c:if>
        <c:if test="${not empty price}">
            <form:form method="post"
                       action="${pageContext.request.contextPath}/price/update"
                       modelAttribute="price">
                <form:hidden path="identifier"/>
                <div class="mb-3">
                    <label class="form-label">Product</label>
                    <form:select path="product" cssClass="form-select" required="true">
                        <form:option value="">-- Select Product --</form:option>
                        <c:forEach var="p" items="${products}">
                            <form:option value="${p.name}">
                                ${p.name}
                            </form:option>
                        </c:forEach>
                    </form:select>
                </div>
                <div class="mb-3">
                    <label class="form-label">Price</label>
                    <form:input path="priceAmount"
                                cssClass="form-control"
                                type="number"
                                step="0.01"
                                min="0"
                                required="true"/>
                </div>
                <div class="mb-3">
                    <label class="form-label">Price Type</label>
                    <form:select path="priceType" cssClass="form-select" required="true">
                        <form:option value="">-- Select Price Type --</form:option>
                        <form:option value="COST">Cost Price</form:option>
                        <form:option value="SELLING">Selling Price</form:option>
                        <form:option value="MRP">MRP</form:option>
                    </form:select>
                </div>
                <div class="d-flex justify-content-between">
                    <a href="${pageContext.request.contextPath}/price/list"
                       class="btn btn-outline-secondary">
                        Cancel
                    </a>
                    <button type="submit" class="btn btn-primary">
                        Update
                    </button>
                </div>
            </form:form>
        </c:if>
    </div>
</div>
</body>
</html>