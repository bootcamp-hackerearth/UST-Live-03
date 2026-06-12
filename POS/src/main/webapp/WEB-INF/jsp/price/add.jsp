<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Price</title>


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
</head>

<body>

<div class="container">
    <div class="card">

        <div class="card-header">
            <h2>Add Price</h2>
        </div>

        <div class="card-body">

            <form action="${pageContext.request.contextPath}/price/add" method="post">

                <!-- Product Selection -->
                <div class="mb-3">
                    <label for="identifier" class="form-label">Product</label>

                    <select class="form-control" id="identifier" name="identifier" required>
                        <option value="">-- Select Product --</option>

                        <c:forEach var="p" items="${products}">
                            <option value="${p.identifier}">
                                ${p.identifier}
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="mb-3">
                    <label for="costprice" class="form-label">Cost Price</label>
                    <input type="number"
                           step="0.01"
                           class="form-control"
                           id="costprice"
                           name="costprice"
                           required
                           min="0" />
                </div>

                <div class="mb-3">
                    <label for="sellingprice" class="form-label">Selling Price</label>
                    <input type="number"
                           step="0.01"
                           class="form-control"
                           id="sellingprice"
                           name="sellingprice"
                           required
                           min="0" />
                </div>

                <div class="mb-3">
                    <label for="mrpprice" class="form-label">MRP Price</label>
                    <input type="number"
                           step="0.01"
                           class="form-control"
                           id="mrpprice"
                           name="mrpprice"
                           required
                           min="0" />
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

</body>
</html>
