<%@ page language="java" contentType="text/html; charset=UTF-8"
         pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Stock</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            margin: 0;
            min-height: 100vh;
            background-color: #f4f6fb;
            font-family: 'Poppins', sans-serif;
        }

        .card {
            border-radius: 12px;
            background-color: #ffffff;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.08);
        }

        .form-control {
            border-radius: 8px;
            font-size: 14px;
            padding: 10px 12px;
        }

        .form-label {
            font-size: 14px;
            color: #444;
        }

        .btn-primary {
            background-color: #4e73df;
            border-color: #4e73df;
        }

        .btn-primary:hover {
            background-color: #224abe;
            border-color: #224abe;
        }

        .navbar {
            background: linear-gradient(180deg, #4e73df, #224abe);
        }
    </style>
</head>
<body>
<nav class="navbar navbar-dark shadow">
    <div class="container-fluid">
        <span class="navbar-brand fw-bold">Stock Management</span>
        <a href="/stock/list" class="btn btn-outline-light btn-sm">Back</a>
    </div>
</nav>
<div class="container d-flex justify-content-center align-items-center" style="min-height: 100vh;">
    <div class="card shadow p-4" style="width: 450px;">
        <h3 class="text-center mb-4 fw-bold">Add Stock</h3>
        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>
        <form:form method="post"
                   action="/stock/add"
                   modelAttribute="stockDto">
            <div class="mb-3">
                <label class="form-label fw-semibold">Stock Identifier</label>
                <form:input path="identifier"
                            cssClass="form-control"
                            placeholder="Auto-generated"
                            readonly="true"/>
            </div>
            <div class="mb-3">
                <label class="form-label fw-semibold">Warehouse</label>
                <select name="wareHouse" class="form-control" required>
                    <option value="">-- Select Warehouse --</option>
                    <c:forEach var="wh" items="${warehouse}">
                        <option value="${wh.identifier}"
                            <c:if test="${stockDto.wareHouse == wh.identifier}">
                                selected
                            </c:if>>
                            ${wh.identifier}
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="mb-3">
                <label class="form-label fw-semibold">Product</label>
                <select name="product" class="form-control" required>
                    <option value="">-- Select Product --</option>
                    <c:forEach var="p" items="${product}">
                        <option value="${p.productName}"
                            <c:if test="${stockDto.product == p.productName}">
                                selected
                            </c:if>>
                            ${p.productName}
                        </option>
                    </c:forEach>
                </select>
            </div>
            <div class="mb-3">
                <label class="form-label fw-semibold">Rack</label>
                <select name="racks" class="form-control" required>
                    <option value="">-- Select Rack --</option>
                    <c:forEach var="r" items="${racks}">
                        <option value="${r.name}"
                            <c:if test="${stockDto.racks == r.identifier}">
                                selected
                            </c:if>>
                            ${r.name}
                        </option>
                    </c:forEach>
                </select>
            </div>
     <div class="mb-3">
         <label class="form-label fw-semibold">Shelf</label>

         <form:select path="shelves"
                      cssClass="form-control"
                      multiple="true"
                      size="4">
             <c:forEach var="s" items="${shelves}">
                 <form:option value="${s.name}">
                     ${s.name}
                 </form:option>
             </c:forEach>
         </form:select>
     </div>
            <div class="mb-3">
                <label class="form-label fw-semibold">Quantity</label>
                <form:input path="quantity"
                            type="number"
                            cssClass="form-control"
                            placeholder="Enter quantity"
                            required="true"/>
            </div>
            <div class="mb-3">
                <label class="form-label fw-semibold">Status</label>
                <form:select path="status" cssClass="form-control">
                    <form:option value="true">Active</form:option>
                    <form:option value="false">Inactive</form:option>
                </form:select>
            </div>
            <button type="submit" class="btn btn-primary w-100">
                Add Stock
            </button>
            <a href="/stock/list"
               class="btn btn-outline-secondary w-100 mt-2">
                Cancel
            </a>
        </form:form>
    </div>
</div>
</body>
</html>