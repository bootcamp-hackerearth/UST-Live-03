<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<style>
    body {
        background: #2f2f2f;
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
        font-family: "Segoe UI", Arial, sans-serif;
    }

    .edit-card {
        width: 460px;
        background: #ffffff;
        padding: 35px 40px;
        border-radius: 18px;
        box-shadow: 0 18px 45px rgba(0, 0, 0, 0.25);
    }

    h2 {
        text-align: center;
        margin-bottom: 25px;
        font-weight: 600;
        color: #333;
    }

    label {
        font-weight: 600;
        font-size: 0.9rem;
        margin-top: 12px;
        display: block;
        color: #444;
    }

    input, select {
        width: 100%;
        padding: 10px;
        margin-top: 6px;
        border-radius: 8px;
        border: 1px solid #ddd;
        font-size: 0.9rem;
    }

    input[readonly] {
        background-color: #f1f3f5;
    }

    button {
        width: 100%;
        margin-top: 25px;
        padding: 12px;
        background: linear-gradient(90deg, #4facfe, #00f2fe);
        border: none;
        color: #ffffff;
        font-weight: 600;
        border-radius: 25px;
        font-size: 1rem;
        cursor: pointer;
    }

    button:hover {
        opacity: 0.95;
    }

    .back-link {
        display: block;
        text-align: center;
        margin-top: 18px;
        font-weight: 600;
        color: #0d6efd;
        text-decoration: none;
    }

    .alert {
        padding: 10px;
        margin-bottom: 15px;
        border-radius: 8px;
        text-align: center;
        background: #f8d7da;
        color: #842029;
    }
</style>

<div class="edit-card">

    <h2>Update Product</h2>

    <c:if test="${not empty message}">
        <div class="alert">
            ${message}
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/product/update" method="post">

        <label>Identifier</label>
        <input type="text"
               name="identifier"
               value="${product.identifier}"
               readonly />

        <label>Product Name</label>
        <input type="text"
               name="name"
               value="${product.name}"
               required />

        <label>Category</label>
        <select name="category" required>
            <option value="">-- Select Category --</option>
            <c:forEach var="cat" items="${categories}">
                <option value="${cat.identifier}"
                    <c:if test="${product.category == cat.identifier}">
                        selected
                    </c:if>>
                    ${cat.identifier}
                </option>
            </c:forEach>
        </select>

        <label>Brand</label>
        <select name="brand" required>
            <option value="">-- Select Brand --</option>
            <c:forEach var="brand" items="${brands}">
                <option value="${brand.identifier}"
                    <c:if test="${product.brand == brand.identifier}">
                        selected
                    </c:if>>
                    ${brand.identifier}
                </option>
            </c:forEach>
        </select>

        <label>Model</label>
        <select name="model" required>
            <option value="">-- Select Model --</option>
            <c:forEach var="model" items="${models}">
                <option value="${model.identifier}"
                    <c:if test="${product.model == model.identifier}">
                        selected
                    </c:if>>
                    ${model.identifier}
                </option>
            </c:forEach>
        </select>

        <label>Unit</label>
        <select name="unit" required>
            <option value="">-- Select Unit --</option>
            <c:forEach var="unit" items="${units}">
                <option value="${unit.identifier}"
                    <c:if test="${product.unit == unit.identifier}">
                        selected
                    </c:if>>
                    ${unit.identifier}
                </option>
            </c:forEach>
        </select>

        <button type="submit">
            Update Product
        </button>

    </form>

    <a class="back-link"
       href="${pageContext.request.contextPath}/product/list">
        ← Back to Product List
    </a>

</div>