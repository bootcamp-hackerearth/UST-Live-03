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
     margin: 0;
     font-family: 'Poppins', sans-serif;
     min-height: 100vh;
     background: #ffffff;
     display: flex;
     justify-content: center;
     align-items: center;
 }

 /* Main card container */
 .card {
     position: relative;
     width: 500px;
     background: rgba(255, 255, 255, 0.95);
     padding: 35px 40px;
     border-radius: 16px;
     box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
 }

 /* Heading */
 h4 {
     text-align: center;
     margin-bottom: 25px;
     color: #4b6cb7;
     font-weight: 600;
 }

 /* Form labels */
 .form-label {
     display: block;
     margin-bottom: 6px;
     font-weight: 500;
     color: #333;
 }

 /* Inputs */
 .form-control {
     width: 100%;
     padding: 10px 12px;
     border-radius: 10px;
     border: 1px solid #ccc;
     font-family: 'Poppins', sans-serif;
     font-size: 14px;
 }

 /* Focus effect */
 .form-control:focus {
     outline: none;
     border-color: #4b6cb7;
     box-shadow: 0 0 0 2px rgba(75, 108, 183, 0.2);
 }

 /* Primary button (Update) */
 .btn-primary {
     padding: 10px 22px;
     border-radius: 12px;
     border: none;
     cursor: pointer;
     font-weight: 600;
     font-size: 15px;
     background: linear-gradient(135deg, #4b6cb7, #182848);
     color: white;
     transition: 0.25s ease;
 }

 .btn-primary:hover {
     transform: scale(1.05);
 }

 /* Cancel button */
 .btn-outline-secondary {
     border-radius: 10px;
     font-weight: 500;
 }

 /* Alerts */
 .alert {
     padding: 10px;
     border-radius: 8px;
     margin-bottom: 15px;
     text-align: center;
     font-size: 14px;
 }

 /* Small helper text */
 small {
     font-size: 12px;
     color: #777;
 }
    </style>
</head>
<body>
${message}
<div class="card shadow-lg">
    <div class="card-body">
        <h4 class="text-center mb-4 text-primary">Edit Product</h4>
        <c:if test="${empty product}">
            <div class="alert alert-danger text-center">
                Product not found
            </div>
        </c:if>
        <c:if test="${not empty product}">
            <form:form action="/product/update"
                       method="post"
                       modelAttribute="product">
                <form:hidden path="id" value="${product.id}"/>
                <div class="mb-4">
                <label class="form-label"></label>
                <form:input path="identifier"
                 cssClass="form-control"
                 placeholder="Enter"
                 required="true"/>
                                </div>

                    <div class="mb-4">
                            <label class="form-label">Category</label>
                            <form:select path="category"
                                         cssClass="form-control"
                                         required="true">

                                <form:option value="" label="-- Select category --"/>

                                <form:options items="${categories}"
                                              itemValue="identifier"
                                              itemLabel="identifier"/>
                            </form:select>

                <div class="mb-4">
                    <label class="form-label">Supplier ID</label>
                    <form:input path="supplierId"
                                cssClass="form-control"
                                placeholder="Enter Supplier ID"
                                required="true"/>
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