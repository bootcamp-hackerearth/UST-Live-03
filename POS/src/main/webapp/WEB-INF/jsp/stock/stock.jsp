<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Stock</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Roboto, Arial, sans-serif;
            background: #f1f5f9;
            min-height: 100vh;
            padding: 30px;
        }

        .container {
            width: 75%;
            margin: auto;
            background: #ffffff;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 10px 30px rgba(15, 23, 42, 0.15);
            border-top: 6px solid #1e293b;
        }

        h2 {
            text-align: center;
            margin-bottom: 24px;
            color: #1e293b;
            font-size: 22px;
            font-weight: 700;
        }

        .form-group {
            margin-bottom: 18px;
        }

        label {
            display: block;
            margin-bottom: 6px;
            font-size: 13px;
            font-weight: 600;
            color: #334155;
        }

        input,
        select {
            width: 100%;
            padding: 12px;
            border-radius: 6px;
            border: 1px solid #cbd5e1;
            font-size: 14px;
        }

        input[readonly] {
            background-color: #f8fafc;
            color: #64748b;
        }

        input:focus,
        select:focus {
            outline: none;
            border-color: #1e293b;
            box-shadow: 0 0 0 3px rgba(30, 41, 59, 0.15);
        }

        .btn-group {
            margin-top: 20px;
            text-align: center;
        }

        .btn {
            padding: 10px 16px;
            border-radius: 6px;
            border: none;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            color: #ffffff;
            background-color: #1e293b;
            text-decoration: none;
        }

        .back-btn {
            background-color: #e2e8f0;
            color: #1e293b;
            margin-left: 12px;
        }

        .back-btn:hover {
            background-color: #cbd5e1;
        }

        .message {
            margin-bottom: 16px;
            padding: 10px;
            border-radius: 6px;
            background-color: #fee2e2;
            color: #991b1b;
            text-align: center;
            font-size: 13px;
        }
    </style>
</head>

<body>

<div class="container">
    <h2>Update Stock</h2>

    <c:if test="${not empty message}">
        <div class="message">
            ${message}
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/stock/update" method="post">

        <!-- ID -->
        <div class="form-group">
            <label>ID</label>
            <input
                type="text"
                name="id"
                value="${stock.id}"
                readonly
            />
        </div>

        <!-- Identifier -->
        <div class="form-group">
            <label>Identifier</label>
            <input
                type="text"
                name="identifier"
                value="${stock.identifier}"
                readonly
            />
        </div>

        <div class="form-group">
            <label>Product</label>
            <select name="product" required>
            <option value="">-- Select Product --</option>

                <c:forEach items="${products}" var="p">
                <option
                    value="${p.name}"
                        <c:if test="${p.name eq stock.product}">
                            selected
                        </c:if>
                >
                ${p.name}
                </option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label>Warehouse</label>
            <select name="warehouse" required>
                <option value="">-- Select Warehouse --</option>

                <c:forEach items="${warehouses}" var="wh">
                    <option
                        value="${wh.identifier}"
                        <c:if test="${wh.identifier eq stock.warehouse}">
                            selected
                        </c:if>
                    >
                        ${wh.identifier}
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label>Quantity</label>
            <input
                type="number"
                name="quantity"
                value="${stock.quantity}"
                required
            />
        </div>
        <div class="form-group">
                    <label>Shelf</label>

                    <div class="checkbox-group">
                        <c:forEach items="${shelfs}" var="s">
                            <div class="checkbox-item">
                                <input
                                    type="checkbox"
                                    id="shelf_${s.name}"
                                    name="shelf"
                                    value="${s.name}"
                                    <c:if test="${fn:contains(stock.shelf, s.name)}">
                                        checked
                                    </c:if>
                                />

                                <label for="shelf_${s.name}">
                                    ${s.name}
                                </label>
                            </div>
                        </c:forEach>
                    </div>
                </div>

                <div class="form-group">
                    <label>Rack</label>

                    <div class="checkbox-group">
                        <c:forEach items="${racks}" var="r">
                            <div class="checkbox-item">
                                <input
                                    type="checkbox"
                                    id="rack_${r.name}"
                                    name="rack"
                                    value="${r.name}"
                                    <c:if test="${fn:contains(stock.rack, r.name)}">
                                        checked
                                    </c:if>
                                />

                                <label for="rack_${r.name}">
                                    ${r.name}
                                </label>
                            </div>
                        </c:forEach>
                    </div>
                </div>

        <div class="form-group">
            <label>Expiry Date</label>
            <input
                type="datetime-local"
                name="expiryDate"
                value="${stock.expiryDate}"
                required
            />
        </div>
            <input
                type="hidden"
                name="status"
                value="${stock.status}"
            />

        <div class="btn-group">
            <button type="submit" class="btn">
                Update Stock
            </button>

            <a href="${pageContext.request.contextPath}/stock/list"
               class="btn back-btn">
                Back
            </a>
        </div>

    </form>
</div>

</body>
</html>