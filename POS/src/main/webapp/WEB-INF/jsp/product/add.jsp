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
             background: linear-gradient(to bottom, #ffffff, #e5e5e5, #bbbbbb);
            min-height: 100vh;
        }
        .card {
            border-radius: 12px;
        }
        .form-control {
            border-radius: 8px;
        }
        .card-header{
            background: #ffffff;
        }
    </style>
</head>
<body>

<div class="container d-flex justify-content-center align-items-center mt-5">
    <div class="col-md-5">

        <div class="card shadow-lg">
            <div class="card-header text-center text-black">
                <h4 class="mb-0">Add New Product</h4>
            </div>

            <div class="card-body">

                <c:if test="${not empty stock}">
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
                            placeholder="Enter Product Identifier (e.g., P001)"
                            pattern="P[0-9]{3}"
                            title="Format must be P followed by 3 digits (e.g., P001)"
                            required="true"/>
                    </div>

                    <div class="mb-3">
                        <label class="form-label fw-semibold">Product Name</label>
                        <form:input path="name"
                                    cssClass="form-control"
                                    type="text"
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
                   <label class="form-label fw-semibold">Price</label>
                  <form:input path="price"
                        cssClass="form-control"
                        type="number"
                        min="0.01"
                        step="0.01"
                        pattern="{+ only allow}"
                        placeholder="Enter Price"
                        required="true"/>
                    </div>

                    <div class="mb-3">
                      <label class="form-label fw-semibold">Description</label>
                       <form:input path="description"
                        cssClass="form-control"
                        type="text"
                     placeholder="Enter description"
                   required="true"/>
                                        </div>


<div class="mb-3">
    <label class="form-label fw-semibold">Category</label>
    <form:select path="category" cssClass="form-select" required="true">
        <form:option value="" label="Select Category"/>
        <form:options
                items="${categories}"
                itemValue="identifier"
                itemLabel="identifier"/>
    </form:select>
</div>


                 <div class="mb-3">
                                            <div class="form-group">
                                                 <label>Select Unit</label>
                                                 <form:select path="unit"
                                                              required="true"
                                                              cssClass="form-control">
                                                              <form:option value="" label="-- Select Model--"/>
                                                              <form:options items="${unit}"
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

                </form:form>

            </div>

            <div class="card-footer text-center text-muted small">
                POS Management System
            </div>

            <div class="text-center mt-3">
                            <a href="/product/list">← Back to Products List</a>
                        </div>
        </div>
         <c:if test="${not empty message}">
                        <div style="
                            background:#f8d7da;
                            color:#721c24;
                            padding:10px;
                            margin-bottom:15px;
                            border-radius:4px;
                            text-align:center;">
                            ${message}
                        </div>
                    </c:if>

    </div>


</div>

</body>
</html>