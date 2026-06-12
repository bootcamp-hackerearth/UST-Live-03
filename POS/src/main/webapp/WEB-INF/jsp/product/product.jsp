<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Product</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            min-height: 100vh;
            background: linear-gradient(to bottom, #ffffff, #e5e5e5, #bbbbbb);
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card {
            width: 400px;
            border-radius: 15px;
        }

        h4 {
            background: #ffffff;
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="card shadow-lg">
    <div class="card-body">

        <h4 class="text-center">Edit Product</h4>

        <c:if test="${empty productDto}">
            <div class="alert alert-danger text-center">
                Product not found
            </div>
        </c:if>

        <c:if test="${not empty productDto}">
            <form:form action="/product/update"
                       method="post"
                       modelAttribute="productDto">

                <form:hidden path="id"/>

                <div class="mb-4">
                    <label class="form-label">Product Identifier</label>
                    <form:input path="identifier"
 cssClass="form-control"
 readonly="true"/>
                </div>

              <div class="mb-4">
<label class="form-label">Product Name</label>
<form:input path="name"

            cssClass="form-control"
            readonly="true"/>
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

                <div class="mb-4">
  <label class="form-label">Price</label>
  <form:input path="price"
              cssClass="form-control"
              type="number"
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
     <label>Category</label>
      <form:select path="category" required="true">
 <form:option value="" label="Select Category"/>
 <form:options items="${categories}" itemValue="identifier" itemLabel="identifier"/>
                            </form:select>
 </div>

<div class="mb-3">
                           <div class="form-group">
 <label>Select Model</label>
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

                <div class="d-flex justify-content-between">
                    <a href="/product/list" class="btn btn-outline-secondary">
                        Cancel
                    </a>
                    <button type="submit" class="btn btn-primary">
                        Update
                    </button>
                </div>

            </form:form>
        </c:if>
        <div class="text-center mt-3">
                <a href="/stock/list">← Back to stock List</a>
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

</body>
</html>