<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Add Role</title>

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

        .bottom-error {
             margin-top: 12px;
             padding: 10px;
             text-align: center;
             border-radius: 6px;
             background: #fee2e2;
             color: #b91c1c;
             font-size: 13px;
             }
    </style>
</head>
<body>

<div class="container d-flex justify-content-center align-items-center mt-5">
    <div class="col-md-5">

        <div class="card shadow-lg">
            <div class="card-header text-center bg-primary text-white">
                <h4 class="mb-0">Add New Product to Product List</h4>
            </div>

            <div class="card-body">

                <c:if test="${not empty product}">
                    <div class="alert alert-success text-center">
                        ${product}
                    </div>
                </c:if>

                <form:form method="post"
                           action="/product/add"
                           modelAttribute="productDto">
                     <div class="mb-3">
                           <label class="form-label fw-semibold">Product Identifier</label>
                                    <form:input path="identifier"
                                      cssClass="form-control"
                                      placeholder="Enter Product Id"
                                      required="true"/>
                     </div>

                     <div class="mb-3">
                          <label class="form-label fw-semibold">Product Name</label>
                          <form:input path="name"
                                      cssClass="form-control"
                                      placeholder="Enter Product Name"
                                      required="true"/>
                     </div>
                     <div class="mb-3">
                        <div class="form-group">
                             <label>Select Brand</label>
                             <form:select path="brand"
                                          required="true"
                                          cssClass="form-control">
                                          <form:option value="" label="-- Select Brand--"/>
                                          <form:options items="${brand}"
                                                        itemValue="identifier"
                                                        itemLabel="identifier"/>
                             </form:select>
                        </div>
                     </div>

                     <div class="mb-3">
                        <div class="form-group">
                             <label>Select Model</label>
                             <form:select path="model"
                                          required="true"
                                          cssClass="form-control">
                                          <form:option value="" label="-- Select Model--"/>
                                          <form:options items="${model}"
                                                        itemValue="identifier"
                                                        itemLabel="identifier"/>
                             </form:select>
                        </div>
                     </div>

                     <div class="mb-3">
                       <div class="form-group">
                            <label>Select Unit</label>
                            <form:select path="unit"
                                         required="true"
                                         cssClass="form-control">
                                         <form:option value="" label="-- Select Unit--"/>
                                         <form:options items="${unit}"
                                                       itemValue="identifier"
                                                       itemLabel="identifier"/>
                            </form:select>
                       </div>
                     </div>

                     <div class="mb-3">
                          <div class="form-group">
                              <label>Super Category List</label>
                              <form:select path="category"
                                           multiple="true"
                                           cssClass="form-control"
                                           required="true">
                                           <form:option value="" label="-- Select Super Categories--"/>
                                           <form:options items="${categories}"
                                                itemValue="identifier"
                                                itemLabel="identifier"/>
                              </form:select>
                          </div>
                     </div>

                    <div class="d-grid">
                        <button type="submit" class="btn btn-primary btn-lg">
                            Add Product
                        </button>
                    </div>

                    <div class="d-flex justify-content-between">
                         <a href="${pageContext.request.contextPath}/product/list"
                         class="btn btn-outline-secondary">
                              Back to Products List
                         </a>
                    </div>

                <c:if test="${not empty message}">
                        <div class="bottom-error">${message}</div>
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