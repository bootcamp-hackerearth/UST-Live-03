<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<title>Edit Product</title>

<style>
* {
    margin: 0;
    padding: 0;
    box-sizing: border-box;
    font-family: 'Poppins', sans-serif;
}

body {
    min-height: 100vh;
    background: linear-gradient(135deg, #1a1b26, #2a2b3d);
    display: flex;
    justify-content: center;
    align-items: center;
}

.card {
    width: 450px;
    padding: 30px;
    border-radius: 15px;

    background: rgba(255,255,255,0.05);
    backdrop-filter: blur(12px);

    border: 1px solid rgba(255,255,255,0.2);
    box-shadow: 0 8px 32px rgba(0,0,0,0.4);
}

h4 {
    text-align: center;
    color: #fff;
    margin-bottom: 20px;
}

label {
    color: #ccc;
    font-size: 14px;
}

input, select {
    width: 100%;
    padding: 10px;
    margin-top: 6px;
    margin-bottom: 15px;

    border-radius: 8px;
    border: none;
    outline: none;

    background: rgba(255,255,255,0.1);
    color: #fff;
}

select option {
    background: #1a1b26;
    color: #fff;
}

input:focus, select:focus {
    border: 1px solid #00ffff;
    box-shadow: 0 0 8px #00ffff;
}

input[readonly] {
    background: rgba(255,255,255,0.05);
    color: #aaa;
}

.btn {
    padding: 10px 14px;
    border-radius: 8px;
    text-decoration: none;
    font-size: 13px;
    border: none;
    cursor: pointer;
}

.btn-primary {
    background: #00ffff;
    color: #000;
}

.btn-primary:hover {
    box-shadow: 0 0 15px #00ffff;
}

.btn-secondary {
    background: #666;
    color: #fff;
}

.btn-secondary:hover {
    background: #555;
}

.btn-group {
    display: flex;
    justify-content: space-between;
    margin-top: 10px;
}

.alert {
    text-align: center;
    padding: 10px;
    margin-bottom: 10px;
    color: #ff8080;
}

.footer {
    text-align: center;
    margin-top: 10px;
    color: #aaa;
    font-size: 12px;
}
</style>
</head>

<body>

<div class="card">

    <h4>Edit Product</h4>

    <c:if test="${empty product}">
        <div class="alert">
            Product not found
        </div>
    </c:if>

    <c:if test="${not empty product}">
        <form:form method="post"
                   action="${pageContext.request.contextPath}/product/update"
                   modelAttribute="product">

            <form:hidden path="id"/>

            <label>Product Identifier</label>
            <form:input path="identifier" readonly="true"/>

            <label>Category</label>
            <form:select path="category">
                <form:option value="">-- Select Category --</form:option>
                <form:options items="${category}"
                              itemValue="identifier"
                              itemLabel="identifier"/>
            </form:select>

            <label>Brand</label>
            <form:select path="brand">
                <form:option value="">-- Select Brand --</form:option>
                <form:options items="${brand}"
                              itemValue="identifier"
                              itemLabel="identifier"/>
            </form:select>

            <label>Unit</label>
            <form:select path="unit">
                <form:option value="">-- Select Unit --</form:option>
                <form:options items="${unit}"
                              itemValue="identifier"
                              itemLabel="identifier"/>
            </form:select>

            <label>Model</label>
            <form:select path="model">
                <form:option value="">-- Select Model --</form:option>
                <form:options items="${model}"
                              itemValue="identifier"
                              itemLabel="identifier"/>
            </form:select>

            <label>Supplier ID</label>
            <form:input path="supplierId"
                        placeholder="Enter Supplier ID"
                        required="true"/>

            <div class="btn-group">
                <a href="${pageContext.request.contextPath}/product/list"
                   class="btn btn-secondary">
                    Cancel
                </a>

                <button type="submit" class="btn btn-primary">
                    Update
                </button>
            </div>

        </form:form>
    </c:if>

    <div class="footer">
        POS Management System
    </div>

</div>

</body>
</html>