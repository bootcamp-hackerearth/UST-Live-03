<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Stock</title>
    <link rel="stylesheet"
          href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css">
</head>

<body>

<div class="container mt-5">
    <div class="card shadow-lg">
        <div class="card-header bg-primary text-white">
            <h4 class="mb-0">Edit Stock</h4>
        </div>

        <div class="card-body">
            <form:form action="${pageContext.request.contextPath}/stock/add"
                       method="post"
                       modelAttribute="stockDto">

                <!-- Hidden Stock ID -->
                <form:hidden path="id"/>

                <!--  Product Dropdown -->
                <div class="mb-3">
                    <label class="form-label">Product Name</label>
                    <form:select path="identifier" cssClass="form-select" required="true">
                        <form:options items="${productList}"
                                      itemValue="identifier"
                                      itemLabel="identifier"/>
                    </form:select>
                </div>

                <!-- Warehouse Dropdown -->
                <div class="mb-3">
                    <label class="form-label">Warehouse</label>
                    <form:select path="warehouse" cssClass="form-select" required="true">
                        <form:options items="${warehouseList}"
                                      itemValue="identifier"
                                      itemLabel="identifier"/>
                    </form:select>
                </div>

                <!-- Quantity -->
                <div class="mb-3">
                    <label class="form-label">Quantity</label>
                    <form:input path="quantity"
                                type="number"
                                class="form-control"
                                required="true"/>
                </div>

                <!-- Unit Price -->
                <div class="mb-3">
                    <label class="form-label">Unit Price</label>
                    <form:input path="unitPrice"
                                type="number"
                                step="0.01"
                                class="form-control"
                                required="true"/>
                </div>

                <!-- Status -->
                <div class="mb-3">
                    <label class="form-label">Status</label>
                    <form:select path="status" cssClass="form-select">
                        <form:option value="true">Active</form:option>
                        <form:option value="false">InActive</form:option>
                    </form:select>
                </div>

                <!-- Buttons -->
                <div class="d-flex gap-2">
                    <button type="submit" class="btn btn-success">
                        Add Stock
                    </button>
                    <a href="${pageContext.request.contextPath}/stock/list"
                       class="btn btn-secondary">
                        Cancel
                    </a>
                </div>

            </form:form>
        </div>
    </div>
</div>

</body>
</html>