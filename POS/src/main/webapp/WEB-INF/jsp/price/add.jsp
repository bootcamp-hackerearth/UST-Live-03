<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css"
      rel="stylesheet">

<style>
    body {
        background: #2f2f2f;
        min-height: 100vh;
        font-family: "Segoe UI", sans-serif;
        padding: 40px;
    }

    .container {
        max-width: 550px;
    }

    .card {
        border: none;
        border-radius: 18px;
        background-color: #ffffff;
        box-shadow: 0 15px 40px rgba(0, 0, 0, 0.25);
    }

    .card-header {
        background: transparent;
        border-bottom: none;
        padding: 20px;
        text-align: center;
    }

    .card-header h2 {
        font-size: 22px;
        font-weight: 600;
        color: #333;
    }

    label {
        font-weight: 600;
        font-size: 0.9rem;
    }

    .btn-save {
        background: linear-gradient(90deg, #4facfe, #00f2fe);
        color: #fff;
        border-radius: 20px;
        font-weight: 600;
        padding: 8px 24px;
    }

    .btn-cancel {
        border-radius: 20px;
        padding: 8px 24px;
    }

    .card-footer {
        text-align: center;
        font-size: 0.85rem;
        color: #555;
    }
</style>

<div class="container">
    <div class="card">

        <div class="card-header">
            <h2>Add Price</h2>
        </div>

        <div class="card-body">

            <form action="${pageContext.request.contextPath}/price/add" method="post">

                <div class="mb-3">
                    <label for="identifier" class="form-label">
                         Identifier
                    </label>

                    <select
                            id="identifier"
                            name="identifier"
                            class="form-control"
                            required>

                        <option value="">
                            -- Select --
                        </option>

                        <c:forEach var="product" items="${products}">
                            <option value="${product.identifier}">
                                ${product.identifier}
                            </option>
                        </c:forEach>

                    </select>
                </div>

                <div class="mb-3">
                    <label for="costprice" class="form-label">
                        Cost Price
                    </label>

                    <input type="number"
                           class="form-control"
                           id="costprice"
                           name="costprice"
                           min="0"
                           required />
                </div>

                <div class="mb-4">
                    <label for="sellingprice" class="form-label">
                        Selling Price
                    </label>

                    <input type="number"
                           class="form-control"
                           id="sellingprice"
                           name="sellingprice"
                           min="0"
                           required />
                </div>

                <div class="d-flex justify-content-between">
                    <a href="${pageContext.request.contextPath}/price/list"
                       class="btn btn-secondary btn-cancel">
                        Cancel
                    </a>

                    <button type="submit" class="btn btn-save">
                        Save
                    </button>
                </div>

            </form>

        </div>

        <div class="card-footer">
            POS Management System
        </div>

    </div>
</div>