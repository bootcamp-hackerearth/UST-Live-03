<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Edit Stock</title>

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

        <h4 class="text-center mb-4 text-primary">Edit Stock</h4>

        <c:if test="${empty stocks}">
            <div class="alert alert-danger text-center">
                Product not found
            </div>
        </c:if>

        <c:if test="${not empty stocks}">
            <form:form action="/stock/update"
                       method="post"
                       modelAttribute="stock">

                <form:hidden path="id" value="${stock.id}"/>

                <div class="mb-4">
                     <label class="form-label">Product</label>
                     <form:input path="identifier"
                                 cssClass="form-control"
                                 placeholder="Enter product"
                                 readonly="true"/>
                                </div>

                <div class="mb-4">
                     <label class="form-label">Quantity</label>
                     <form:input path="quantity"
                                 cssClass="form-control"
                                 placeholder="Enter quantity"
                                 oninput="this.value = this.value.replace(/[^0-9]/g, '')"
                                 pattern="[0-9]+"
                                 required="true"/>
                </div>

                <div class="mb-4">
                     <label class="form-label">Minimum Stock</label>
                     <form:input path="minimumStock"
                                 cssClass="form-control"
                                 placeholder="Enter Minimum Count"
                                 oninput="this.value = this.value.replace(/[^0-9]/g, '')"
                                 pattern="[0-9]+"
                                 required="true"/>
                </div>

                <div class="d-flex justify-content-between">
                    <a href="/stock/list" class="btn btn-outline-secondary">
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