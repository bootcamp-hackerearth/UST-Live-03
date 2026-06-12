<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Role</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            min-height: 100vh;
            background: linear-gradient(135deg, #1f4037, #99f2c8);
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card {
            width: 400px;
            border-radius: 15px;
        }

        h4 {
            font-weight: 600;
        }
    </style>
</head>

<body>
${message}
<div class="card shadow-lg">
    <div class="card-body">

        <h4 class="text-center mb-4 text-primary">Edit Product</h4>

        <c:if test="${empty products}">
            <div class="alert alert-danger text-center">
                Product not found
            </div>
        </c:if>

        <c:if test="${not empty products}">
            <form:form action="/product/update"
                       method="post"
                       modelAttribute="product">

                <form:hidden path="id" value="${product.id}"/>

                <div class="mb-4">
                      <label class="form-label">Product Identifier</label>
                        <form:input path="identifier"
                                    cssClass="form-control"
                                    placeholder="Enter product Id"
                                    readonly="true"/>
                </div>

                <div class="mb-4">
                     <label class="form-label">Product Name</label>
                     <form:input path="name"
                                 cssClass="form-control"
                                 placeholder="Enter product Name"
                                 required="true"/>
                </div>

                <div class="mb-3">
                  <div class="form-group">
                       <label>Select Brand</label>
                       <form:select path="brand"
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
                                   cssClass="form-control">
                                   <form:option value="" label="-- Select Super Categories--"/>
                                   <form:options items="${categories}"
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

    </div>
</div>

</body>
</html>