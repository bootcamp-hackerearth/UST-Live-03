<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
    <title>Update Product</title>

    <style>
        body {
                 font-family: Arial, sans-serif;
                 background: #F6F7F9;
                 padding: 30px;
             }

             .container {
                 max-width: 450px;
                 margin: auto;
                 background: white;
                 padding: 25px;
                 border-radius: 10px;
                 box-shadow: 0 6px 14px rgba(0,0,0,0.08);
             }

             h2 {
                 text-align: center;
                 margin-bottom: 20px;
             }

             label {
                 font-weight: 600;
                 display: block;
                 margin-top: 12px;
             }

             input, select {
                 width: 100%;
                 padding: 10px;
                 margin-top: 6px;
                 border-radius: 6px;
                 border: 1px solid #ccc;
             }

             .btn {
                 width: 100%;
                 padding: 12px;
                 margin-top: 20px;
                 background: #2B2B2B;
                 color: white;
                 font-size: 15px;
                 border: none;
                 border-radius: 6px;
                 cursor: pointer;
             }

             .btn:hover {
                 background: #444;
             }

             .message {
                 background: #FDECEA;
                 color: #B71C1C;
                 padding: 10px;
                 border-radius: 6px;
                 margin-bottom: 15px;
                 text-align: center;
             }
             .btn-group {
                         display: flex;
                         gap: 10px;
                         margin-top: 25px;
                     }

                     .save-btn {
                         flex: 1;
                         padding: 10px;
                         background: #2B2B2B;
                         color: white;
                         border: none;
                         border-radius: 6px;
                         font-weight: 600;
                         cursor: pointer;
                     }

                     .save-btn:hover {
                         background: #444;
                     }

                     .back-btn {
                         flex: 1;
                         padding: 10px;
                         background: #E5E7EB;
                         color: #111827;
                         border-radius: 6px;
                         font-weight: 600;
                         text-align: center;
                         text-decoration: none;
                     }

                     .back-btn:hover {
                         background: #D1D5DB;
                     }
    </style>
</head>

<body>

<div class="container">

    <h2>Update Product</h2>

    <c:if test="${not empty message}">
        <div class="message">${message}</div>
    </c:if>

    <form:form action="/product/update"
               method="post"
               modelAttribute="product">

        <label>Product Identifier</label>
        <form:input path="identifier" readonly="true"/>

        <label>Product Name</label>
        <form:input path="name" required="true"/>

        <label>Unit</label>
        <form:select path="unit">
            <form:option value="" label="-- Select Unit --"/>
            <form:options items="${unit}" itemValue="identifier" itemLabel="identifier"/>
        </form:select>

        <label>Brand</label>
        <form:select path="brand">
            <form:option value="" label="-- Select Brand --"/>
            <form:options items="${brand}" itemValue="identifier" itemLabel="identifier"/>
        </form:select>

        <label>Model</label>
        <form:select path="model">
            <form:option value="" label="-- Select Model --"/>
            <form:options items="${model}" itemValue="identifier" itemLabel="identifier"/>
        </form:select>

        <label>Price</label>
        <form:input path="price" type="number" step="0.01" required="true"/>

        <label>Category</label>
        <form:select path="category">
            <form:option value="" label="-- Select Category --"/>
            <form:options items="${categories}" itemValue="identifier" itemLabel="identifier"/>
        </form:select>

       <div class="btn-group">
            <button type="submit" class="save-btn">Update</button>
            <a href="/product/list" class="back-btn">Back</a>
       </div>

    </form:form>

</div>

</body>
</html>