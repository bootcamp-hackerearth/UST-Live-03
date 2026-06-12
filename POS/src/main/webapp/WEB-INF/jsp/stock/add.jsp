<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Product</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: linear-gradient(135deg, #1f4037, #99f2c8);
            min-height: 100vh;
        }
        .card {
            border-radius: 12px;
        }
        .form-control {
            border-radius: 8px;
        }
    </style>
</head>
<body>

<div class="container d-flex justify-content-center align-items-center mt-5">
    <div class="col-md-5">

        <div class="card shadow-lg">
            <div class="card-header text-center bg-primary text-white">
                <h4 class="mb-0">Add New Product to Stock List</h4>
            </div>

            <div class="card-body">

                <c:if test="${not empty message}">
                    <div class="alert alert-success text-center">
                        ${message}
                    </div>
                </c:if>

                <form:form method="post"
                           action="/stock/add"
                           modelAttribute="stockDto">

                     <div class="mb-3">
                      <div class="form-group">
                          <label>Product List</label>
                          <form:select path="identifier"
                                       cssClass="form-control"
                                       required="required">
                                   <form:option value="" label="-- Select Product --"/>
                                   <form:options items="${products}"
                                   itemValue="identifier"
                                   itemLabel="identifier"/>
                          </form:select>
                      </div>
                     </div>

                     <div class="mb-3">
                          <div class="form-group">
                             <label>Warehouse List</label>
                             <form:select path="warehouse"
                                          cssClass="form-control"
                                          required="required">
                                          <form:option value="" label="-- Select Warehouse--"/>
                                          <form:options items="${warehouses}"
                                               itemValue="identifier"
                                               itemLabel="name"/>
                                          </form:select>
                          </div>
                     </div>

                     <div class="mb-3">
                         <label class="form-label fw-semibold">Quantity to Add</label>
                         <form:input path="quantity"
                                     cssClass="form-control"
                                     placeholder="Enter quantity"
                                     type="number"
                                     required="true"/>
                     </div>

                     <div class="mb-3">
                         <label class="form-label fw-semibold">Minimum Count</label>
                         <form:input path="minimumStock"
                                     cssClass="form-control"
                                     placeholder="Enter the Minimum Count"
                                     required="true"
                                     type="number"/>
                     </div>

                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary btn-lg">
                            Add Product
                        </button>
                    </div>

                    <div class="d-flex justify-content-between">
                         <a href="${pageContext.request.contextPath}/stock/list"
                         class="btn btn-outline-secondary">
                              Back to Stocks List
                         </a>
                    </div>

                <c:if test="${not empty message}">
                        <p class="error">${message}</p>
                    </c:if>

                </form:form>
            </div>

            <div class="card-footer text-center text-muted small">
                POS Management System
            </div>
        </div>

    </div>
</div>

</body>
</html>