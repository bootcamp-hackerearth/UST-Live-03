<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Add Stock</title>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
      rel="stylesheet">

<style>
body {
    background: linear-gradient(135deg,#f3f4f6,#e5e7eb,#f9fafb);
    min-height: 100vh;
    font-family: 'Segoe UI', sans-serif;
}

.card {
    border-radius: 14px;
    border: 1px solid #e5e7eb;
    background: rgba(255,255,255,0.9);
    box-shadow: 0 10px 25px rgba(0,0,0,0.08);
}

.card-header {
    background: linear-gradient(135deg,#e5e7eb,#f3f4f6) !important;
    color:#111827 !important;
}

.form-control,.form-select {
    border-radius: 8px;
    border: 1px solid #d1d5db;
}

.btn-primary {
    background:#3b82f6;
    border:none;
}
.btn-primary:hover { background:#2563eb; }

.error-message {
    color:red;
    text-align:center;
}
</style>
</head>

<body>

<c:if test="${not empty message}">
    <div class="error-message">${message}</div>
</c:if>

<div class="container d-flex justify-content-center mt-5">
<div class="col-md-6">

<div class="card shadow-lg">

<div class="card-header text-center">
    <h4>Add Stock</h4>
</div>

<div class="card-body">

<form:form method="post"
           action="${pageContext.request.contextPath}/stock/add"
           modelAttribute="stockDto">

    <form:hidden path="identifier"/>


    <div class="mb-3">
        <label>Product</label>
        <form:select path="product" cssClass="form-select">
            <form:option value="" label="-- Select Product --"/>
            <form:options items="${products}" itemValue="name" itemLabel="name"/>
        </form:select>
    </div>

    <div class="mb-3">
        <label>Quantity</label>
        <form:input path="quantity" cssClass="form-control" type="number"/>
    </div>

    <div class="mb-3">
        <label>Status</label>
        <form:select path="status" cssClass="form-select">
            <form:option value="" label="-- Select Status --"/>
            <form:option value="AVAILABLE" label="Available"/>
            <form:option value="OUT_OF_STOCK" label="Out of Stock"/>
            <form:option value="DAMAGED" label="Damaged"/>
        </form:select>
    </div>

    <div class="mb-3">
        <label>Warehouse</label>
        <form:select path="warehouse" cssClass="form-select">
            <form:option value="" label="-- Select Warehouse --"/>
           <form:options items="${warehouses}" itemValue="identifier" itemLabel="identifier"/>
        </form:select>
    </div>
    <button class="btn btn-primary w-100">Save Stock</button>
     <div class="back text-center pb-3">
                    <a href="${pageContext.request.contextPath}/stock/list">← Back to List</a>
                </div>

</form:form>
</div>
</div>
</div>
</div>
</body>
</html>