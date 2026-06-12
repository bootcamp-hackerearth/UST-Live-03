<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Edit Stock</title>

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

       .btn {
           border-radius: 8px;
           font-size: 14px;
       }

       .btn-primary {
           background-color: #4e73df;
           border-color: #4e73df;
       }

       .btn-primary:hover {
           background-color: #224abe;
           border-color: #224abe;
       }

       .btn-outline-secondary {
           border-radius: 8px;
       }

       .navbar {
           background: linear-gradient(180deg, #4e73df, #224abe);
       }

       .alert {
           border-radius: 8px;
           font-size: 14px;
       }
    </style>
</head>
<body>
<nav class="navbar navbar-dark bg-dark shadow">
    <div class="container-fluid">
        <span class="navbar-brand fw-bold">Stock Management</span>
        <a href="/stock/list" class="btn btn-outline-light btn-sm">Back</a>
    </div>
</nav>

<div class="container d-flex justify-content-center align-items-center" style="min-height: 100vh;">
    <div class="card shadow p-4" style="width: 450px;">
        <h3 class="text-center mb-4 fw-bold">Edit Stock</h3>
        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>
        <c:if test="${empty stockDto}">
            <div class="alert alert-danger text-center">
                Stock not found
            </div>
        </c:if>
        <c:if test="${not empty stockDto}">
            <form action="/stock/update" method="post">
                <input type="hidden" name="id" value="${stockDto.id}"/>
                <div class="mb-3">
                    <label class="form-label fw-semibold">Stock Identifier</label>
                    <input type="text"
                           class="form-control"
                           name="identifier"
                           value="${stockDto.identifier}"
                           readonly>
                </div>
                <div class="mb-3">
                    <label class="form-label fw-semibold">Warehouse</label>
                    <select name="wareHouse"
                            class="form-control">
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
                    <select name="product"
                            class="form-control">
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
                   <select name="racks" class="form-control">
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
    <select name="shelves"
            class="form-control"
            multiple
            size="4">
        <c:forEach var="s" items="${shelves}">
            <option value="${s.name}"
                <c:if test="${stockDto.shelves != null
                             && stockDto.shelves.contains(s.name)}">
                    selected
                </c:if>>
                ${s.name}
</option>
        </c:forEach>
    </select>
                <div class="mb-3">
                    <label class="form-label fw-semibold">Quantity</label>
                    <input type="number"
                           class="form-control"
                           name="quantity"
                           value="${stockDto.quantity}">
                </div>
               <div class="mb-3">
                   <label class="form-label fw-semibold">Status</label>
                   <select name="status" class="form-control">
                       <option value="true"
                           <c:if test="${stockDto.status}">selected</c:if>>
                           Active
                       </option>
                       <option value="false"
                           <c:if test="${!stockDto.status}">selected</c:if>>
                           Inactive
                       </option>
                   </select>
               </div>
                <div class="d-flex gap-2">
                    <button type="submit" class="btn btn-primary w-100">
                        Update
                    </button>
                    <a href="/stock/list" class="btn btn-outline-secondary w-100">
                        Cancel
                    </a>
                </div>
            </form>
        </c:if>
    </div>
</div>
</body>
</html>