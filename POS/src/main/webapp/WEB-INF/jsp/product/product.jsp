<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS | Edit Product</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet"/>

    <style>
        :root {
            --primary-blue: #0d6efd;
            --bg-light: #f4f7f6;
            --text-dark: #2c3e50;
            --border-color: #e2e8f0;
            --shadow-soft: 0 4px 14px rgba(0,0,0,0.06);
            --shadow-deep: 0 12px 30px rgba(0,0,0,0.08);
        }

        body {
            background-color: var(--bg-light);
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            color: var(--text-dark);
            padding: 40px 0;
        }

        .container {
            max-width: 900px;
        }

        .back-btn {
            background: #ffffff;
            color: #64748b !important;
            border: 1px solid var(--border-color);
            padding: 10px 20px;
            border-radius: 10px;
            font-weight: 600;
            text-decoration: none;
            transition: all 0.2s ease;
            display: inline-flex;
            align-items: center;
            gap: 8px;
            box-shadow: var(--shadow-soft);
            margin-bottom: 25px;
        }

        .back-btn:hover {
            background: #f8fafc;
            color: var(--primary-blue) !important;
            transform: translateY(-2px);
        }

        .card-edit {
            background: #ffffff;
            border: none;
            border-radius: 16px;
            box-shadow: var(--shadow-deep);
            overflow: hidden;
        }

        .card-header-custom {
            background-color: var(--primary-blue);
            color: #ffffff;
            padding: 24px;
            text-align: center;
        }

        .card-header-custom h2 {
            margin: 0;
            font-size: 1.5rem;
            font-weight: 700;
        }

        .section-title {
            font-size: 0.85rem;
            font-weight: 800;
            color: var(--primary-blue);
            text-transform: uppercase;
            letter-spacing: 1px;
            margin: 30px 0 20px 0;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .section-title::after {
            content: "";
            flex-grow: 1;
            height: 1px;
            background: var(--border-color);
        }

        .form-label {
            font-size: 0.85rem;
            color: #64748b;
            font-weight: 700;
            margin-bottom: 8px;
        }

        .input-group-text {
            background-color: #f8fafc;
            border-color: var(--border-color);
            color: #94a3b8;
        }

        .form-control, .form-select {
            border: 1px solid var(--border-color);
            border-radius: 8px;
            padding: 11px 14px;
            font-weight: 500;
        }

        .readonly-field {
            background-color: #f1f5f9 !important;
            color: #475569;
            font-weight: 600;
        }

        .btn-update {
            background-color: var(--primary-blue);
            border: none;
            color: white;
            padding: 16px;
            border-radius: 12px;
            font-weight: 700;
            font-size: 1.1rem;
            transition: all 0.3s ease;
            width: 100%;
            margin-top: 20px;
            display: flex;
            justify-content: center;
            align-items: center;
            gap: 10px;
        }

        .btn-update:hover {
            background-color: #0b5ed7;
            transform: translateY(-2px);
            box-shadow: 0 8px 15px rgba(13, 110, 253, 0.2);
        }
    </style>
</head>

<body>

<div class="container">

    <a href="${pageContext.request.contextPath}/product/list" class="back-btn">
        <i class="bi bi-arrow-left"></i> Back to List
    </a>

    <div class="card card-edit">
        <div class="card-header-custom">
            <h2><i class="bi bi-pencil-square me-2"></i>Edit Product: ${product.identifier}</h2>
        </div>

        <div class="card-body p-4 p-md-5">

            <c:if test="${not empty message}">
                <div class="alert alert-danger shadow-sm mb-4">
                    <i class="bi bi-exclamation-octagon-fill me-2"></i> ${message}
                </div>
            </c:if>

            <form:form method="post" action="${pageContext.request.contextPath}/product/update" modelAttribute="product">

                <form:hidden path="id" />

                <div class="section-title" style="margin-top: 0;">Identity & Description</div>

                <div class="row g-4 mb-4">
                    <div class="col-md-6">
                        <label class="form-label">Product Identifier (Unique)</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-lock-fill"></i></span>
                            <form:input path="identifier" class="form-control readonly-field" readonly="true"/>
                        </div>
                    </div>


                    <div class="col-12">
                        <label class="form-label">Product Name / Description</label>
                        <form:textarea path="description" class="form-control" rows="2" />
                    </div>
                </div>

                <div class="section-title">Classification Update</div>

                <div class="row g-4 mb-4">
                    <div class="col-md-6">
                        <label class="form-label">Brand</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-award"></i></span>
                            <form:select path="brand" class="form-select">
                                <c:forEach items="${brand}" var="b">
                                    <form:option value="${b.identifier}" label="${b.identifier}"/>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>

                    <div class="col-md-6">
                        <label class="form-label">Model Series</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-cpu"></i></span>
                            <form:select path="models" class="form-select">
                                <c:forEach items="${models}" var="m">
                                    <form:option value="${m.identifier}" label="${m.identifier}"/>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>
                </div>

                <div class="section-title">Availability</div>

                <div class="row g-4 mb-5">
                    <div class="col-md-6">
                        <label class="form-label">Inventory Status</label>
                        <div class="input-group">
                            <span class="input-group-text"><i class="bi bi-toggle-on"></i></span>
                            <form:select path="status" class="form-select">
                                <form:option value="true">Active</form:option>
                                <form:option value="false">Inactive</form:option>
                            </form:select>
                        </div>
                    </div>
                </div>

                <button type="submit" class="btn-update">
                    <i class="bi bi-check-all"></i> Update Product Details
                </button>

            </form:form>
        </div>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/js/bootstrap.bundle.min.js"></script>

</body>
</html>