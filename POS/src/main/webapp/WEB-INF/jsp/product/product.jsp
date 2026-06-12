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

        .card {
            position: relative;
            width: 500px;
            background: rgba(255, 255, 255, 0.95);
            padding: 35px 40px;
            border-radius: 16px;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
        }

        h4 {
            text-align: center;
            margin-bottom: 25px;
            color: #4b6cb7;
            font-weight: 600;
        }

        .form-label {
            font-weight: 500;
            color: #333;
        }

        .form-control {
            border-radius: 10px;
            font-size: 14px;
            padding: 10px 12px;
        }

        .btn-primary {
            background: linear-gradient(135deg, #4b6cb7, #182848);
            border: none;
            border-radius: 12px;
            font-weight: 600;
        }

        .btn-primary:hover {
            transform: scale(1.05);
        }

        .btn-outline-secondary {
            border-radius: 10px;
            font-weight: 500;
        }
    </style>
</head>

<body>

<div class="card shadow-lg">
    <div class="card-body">

        <h4>Edit Product</h4>

        <c:if test="${empty product}">
            <div class="alert alert-danger text-center">
                Product not found
            </div>
        </c:if>

        <c:if test="${not empty product}">
            <form:form method="post"
                       action="${pageContext.request.contextPath}/product/update"
                       modelAttribute="product">

                <!-- Hidden DB ID -->
                <form:hidden path="id"/>

                <!-- ✅ Product Identifier -->
                <div class="mb-4">
                    <label class="form-label">Product Identifier</label>
                    <form:input path="identifier"
                                cssClass="form-control"
                                readonly="true"/>
                </div>

                <!-- ✅ Supplier ID -->
                <div class="mb-4">
                    <label class="form-label">Supplier ID</label>
                    <form:input path="supplierId"
                                cssClass="form-control"
                                placeholder="Enter Supplier ID"
                                required="true"/>
                </div>

                <!-- ✅ Buttons -->
                <div class="d-flex justify-content-between">
                    <a href="${pageContext.request.contextPath}/product/list"
                       class="btn btn-outline-secondary">
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