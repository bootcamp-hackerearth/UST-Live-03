<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Product</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
          rel="stylesheet">

    <style>
        body {
            background: #2f2f2f;
            min-height: 100vh;
            display: flex;
            align-items: center;
            justify-content: center;
            font-family: "Segoe UI", sans-serif;
        }

        .container {
            max-width: 450px;
        }

        .card {
            border: none;
            border-radius: 18px;
            background-color: #ffffff;
            box-shadow: 0 15px 40px rgba(0, 0, 0, 0.25);
        }

        input, select {
            width: 100%;
            padding: 10px 14px;
            margin-top: 6px;
            margin-bottom: 12px;
            border-radius: 10px;
            border: 1px solid #ddd;
            font-size: 0.9rem;
        }

        label {
            font-weight: 600;
            margin-top: 5px;
        }
    </style>
</head>

<body>

<div class="container">
    <div class="card p-4">

        <h4 class="text-center mb-3">Add Product</h4>

        <c:if test="${not empty message}">
            <div class="alert alert-danger text-center">
                ${message}
            </div>
        </c:if>

        <form action="${pageContext.request.contextPath}/product/add" method="post">

            <!-- Identifier -->
            <label>Identifier</label>
            <input type="text"
                   name="identifier"
                   value="${product.identifier}"
                   required>

            <!-- Product Name -->
            <label>Product Name</label>
            <input type="text"
                   name="name"
                   value="${product.name}"
                   required>

            <!-- Category -->
            <label>Category</label>
            <select name="category" required>
                <option value="">-- Select Category --</option>
                <c:forEach var="cat" items="${categories}">
                    <option value="${cat.identifier}">
                        ${cat.identifier}
                    </option>
                </c:forEach>
            </select>

            <!-- Brand -->
            <label>Brand</label>
            <select name="brand" required>
                <option value="">-- Select Brand --</option>
                <c:forEach var="brand" items="${brands}">
                    <option value="${brand.identifier}">
                        ${brand.identifier}
                    </option>
                </c:forEach>
            </select>

            <!-- Model -->
            <label>Model</label>
            <select name="model" required>
                <option value="">-- Select Model --</option>
                <c:forEach var="model" items="${models}">
                    <option value="${model.identifier}">
                        ${model.identifier}
                    </option>
                </c:forEach>
            </select>

            <!-- Unit -->
            <label>Unit</label>
            <select name="unit" required>
                <option value="">-- Select Unit --</option>
                <c:forEach var="unit" items="${units}">
                    <option value="${unit.identifier}">
                        ${unit.identifier}
                    </option>
                </c:forEach>
            </select>

            <button type="submit" class="btn btn-primary mt-3 w-100">
                Save Product
            </button>

        </form>

        <a href="${pageContext.request.contextPath}/product/list"
           class="text-center d-block mt-3 text-decoration-none">
            ← Back to Product List
        </a>

    </div>
</div>

</body>
</html>