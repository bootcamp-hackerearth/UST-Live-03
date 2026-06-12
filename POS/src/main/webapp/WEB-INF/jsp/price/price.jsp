<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Price</title>

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
            background: #ffffff
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="card shadow-lg">
    <div class="card-body">

        <h4 class="text-center">Edit Price</h4>

        <c:if test="${empty priceDto}">
            <div class="alert alert-danger text-center">
                Price not found
            </div>
        </c:if>

        <c:if test="${not empty priceDto}">
            <form:form action="/price/update"
                       method="post"
                       modelAttribute="priceDto">

                <form:hidden path="identifier"/>

               <div class="mb-4">
                   <label class="form-label">Product</label>
                   <form:input path="product"
                               cssClass="form-control"
                               readonly="true"/>

                   <form:hidden path="product"/>
               </div>

                <div class="mb-4">
                        <label class="form-label">Price Amount</label>
                                    <form:input path="priceAmount"
                                                cssClass="form-control"
                                                type="number" step="0.01"
                                                placeholder="Enter Price"
                                                required="true"/>
                </div>

                <div class="mb-3">
                        <label class="form-label fw-semibold">Price Type</label>
                                      <form:select path="priceType"
                                                   cssClass="form-select"
                                                   required="true">
                                          <form:option value="" label="-- Select Price Type --"/>
                                          <form:option value="MRP" label="MRP"/>
                                          <form:option value="Cost Price" label="Cost Price"/>
                                          <form:option value="Selling Price" label="Selling Price"/>
                                      </form:select>
                                  </div>

                <div class="d-flex justify-content-between">
                    <a href="/price/list" class="btn btn-outline-secondary">
                        Cancel
                    </a>
                    <button type="submit" class="btn btn-primary">
                        Update
                    </button>
                </div>

            </form:form>
        </c:if>
        <div class="text-center mt-3">
                <a href="/price/list">← Back to stock List</a>
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