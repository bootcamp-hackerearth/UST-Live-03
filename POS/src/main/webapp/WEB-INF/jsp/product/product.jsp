<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<!DOCTYPE html>
<html>
<head>
    <title>Update Product</title>

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

        .checkbox-group {
            margin-top: 8px;
        }

        .checkbox-item {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 8px;
        }

        .checkbox-item input {
            width: 16px;
            height: 16px;
            cursor: pointer;
        }

        .checkbox-item label {
            margin: 0;
            font-size: 14px;
            cursor: pointer;
        }

        label {
            display: block;
            margin-bottom: 6px;
            font-size: 13px;
            font-weight: 600;
            color: #334155;
        }

        input,
        textarea,
        select {
            width: 100%;
            padding: 12px;
            border-radius: 6px;
            border: 1px solid #cbd5e1;
            font-size: 14px;
        }

        textarea {
            resize: vertical;
            min-height: 90px;
        }

        input[readonly] {
            background-color: #f8fafc;
            color: #64748b;
        }

        input:focus,
        textarea:focus,
        select:focus {
            outline: none;
            border-color: #1e293b;
            box-shadow: 0 0 0 3px rgba(30, 41, 59, 0.15);
        }

        .btn {
            padding: 10px 18px;
            border-radius: 6px;
            border: none;
            font-size: 14px;
            font-weight: 600;
            cursor: pointer;
            color: #ffffff;
            background-color: #1e293b;
            text-decoration: none;
        }

        .btn:hover {
            background-color: #0f172a;
        }

        .back-btn {
            background-color: #e2e8f0;
            color: #1e293b;
            margin-left: 12px;
        }

        .btn-group {
            margin-top: 20px;
            text-align: center;
        }

        .message {
            color: #dc2626;
            text-align: center;
            margin-bottom: 12px;
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="container">
    <h2>Update Product</h2>

    <c:if test="${not empty message}">
        <div class="message">
            ${message}
        </div>
    </c:if>

    <form action="${pageContext.request.contextPath}/product/update" method="post">

        <div class="form-group">
            <label>ID</label>
            <input
                type="text"
                name="id"
                value="${product.id}"
                readonly
            />
        </div>

        <div class="form-group">
            <label>Identifier</label>
            <input
                type="text"
                name="identifier"
                value="${product.identifier}"
                readonly
            />
        </div>

        <div class="form-group">
            <label>Name</label>
            <input
                type="text"
                name="name"
                value="${product.name}"
                required
            />
        </div>

        <div class="form-group">
            <label>Unit</label>
            <select name="unit" required>
                <option value="">-- Select Unit --</option>

                <c:forEach items="${units}" var="u">
                    <option
                        value="${u.name}"
                        <c:if test="${u.name eq product.unit}">
                            selected
                        </c:if>
                    >
                        ${u.name}
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label>Brand</label>
            <select name="brand" required>
                <option value="">-- Select Brand --</option>

                <c:forEach items="${brands}" var="b">
                    <option
                        value="${b.name}"
                        <c:if test="${b.name eq product.brand}">
                            selected
                        </c:if>
                    >
                        ${b.name}
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label>Category</label>
            <select name="category" required>
                <option value="">-- Select Category --</option>

                <c:forEach items="${categories}" var="cat">
                    <option
                        value="${cat.name}"
                        <c:if test="${cat.name eq product.category}">
                            selected
                        </c:if>
                    >
                        ${cat.name}
                    </option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label>Description</label>
            <textarea name="description">
               ${product.description}
            </textarea>
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
                            <c:if test="${fn:contains(product.shelf, s.name)}">
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
                            <c:if test="${fn:contains(product.rack, r.name)}">
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

        <div class="btn-group">
            <button type="submit" class="btn">
                Update Product
            </button>

            <a href="${pageContext.request.contextPath}/product/list"
               class="btn back-btn">
                Back
            </a>
        </div>

    </form>
</div>

</body>
</html>