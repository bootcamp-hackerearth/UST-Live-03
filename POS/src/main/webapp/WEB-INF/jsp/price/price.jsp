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
            margin: 0;
            font-family: 'Poppins', sans-serif;
            min-height: 100vh;
            background: #ffffff;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card {
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
            padding: 10px 12px;
            border-radius: 10px;
            border: 1px solid #ccc;
            font-size: 14px;
        }

        .form-control:focus {
            border-color: #4b6cb7;
            box-shadow: 0 0 0 2px rgba(75, 108, 183, 0.2);
        }

        .btn-primary {
            padding: 10px 22px;
            border-radius: 12px;
            font-weight: 600;
            background: linear-gradient(135deg, #4b6cb7, #182848);
            border: none;
        }

        .btn-primary:hover {
            transform: scale(1.05);
        }

        .btn-outline-secondary {
            border-radius: 10px;
            font-weight: 500;
        }

        .alert {
            padding: 10px;
            border-radius: 8px;
            margin-bottom: 15px;
            text-align: center;
        }
    </style>
</head>

<body>

<div class="card shadow-lg">
    <div class="card-body">

        <h4>Edit Price</h4>

        <!-- ✅ When Price not found -->
        <c:if test="${empty price}">
            <div class="alert alert-danger">
                Price record not found
            </div>
        </c:if>

        <!-- ✅ Edit Price Form -->
        <c:if test="${not empty price}">
            <form:form action="/price/update"
                       method="post"
                       modelAttribute="price">

                <!-- ✅ Hidden ID -->
                <form:hidden path="id" />

                <!-- ✅ Identifier (not editable) -->
                <div class="mb-4">
                    <label class="form-label">Product Identifier</label>
                    <form:input path="identifier"
                                cssClass="form-control"
                                readonly="true" />
                </div>

                <!-- ✅ Cost Price -->
                <div class="mb-4">
                    <label class="form-label">Cost Price</label>
                    <form:input path="costPrice"
                                type="number"
                                cssClass="form-control"
                                required="true" />
                </div>

                <!-- ✅ Selling Price -->
                <div class="mb-4">
                    <label class="form-label">Selling Price</label>
                    <form:input path="sellingPrice"
                                type="number"
                                cssClass="form-control"
                                required="true" />
                </div>

                <!-- ✅ No Difference field shown (backend-only calculation) -->

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

    </div>
</div>

</body>
</html>