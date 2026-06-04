<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Add Product</title>

    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">

    <style>
        :root {
            --primary: #2563eb;
            --primary-hover: #1e40af;

            --bg: #f8fafc;
            --glass: rgba(255,255,255,0.75);

            --text: #0f172a;
            --muted: #64748b;
            --border: #e2e8f0;

            --danger: #dc2626;
            --danger-bg: #fee2e2;
            --danger-border: #fecaca;

            --radius: 16px;
            --shadow: 0 20px 40px rgba(2,6,23,0.08);
        }

        * {
            font-family: 'Inter', sans-serif;
            box-sizing: border-box;
        }

        body {
            background: var(--bg);
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 40px 20px;
            color: var(--text);
        }

        .back-arrow {
            position: fixed;
            top: 20px;
            left: 20px;

            width: 42px;
            height: 42px;

            display: flex;
            align-items: center;
            justify-content: center;

            border-radius: 50%;
            background: var(--glass);
            backdrop-filter: blur(10px);

            border: 1px solid var(--border);
            color: var(--text);

            text-decoration: none;
            font-size: 18px;

            box-shadow: var(--shadow);
            transition:
                transform 0.2s ease,
                box-shadow 0.2s ease,
                background 0.2s ease,
                color 0.2s ease;
        }

        .back-arrow:hover {
            background: #eef2ff;
            color: var(--primary);
            transform: translateY(-2px) translateX(-2px);
            box-shadow: 0 12px 28px rgba(37,99,235,0.25);
        }

        .form-card {
            width: 520px;
            background: var(--glass);
            backdrop-filter: blur(16px);
            padding: 30px;
            border-radius: var(--radius);
            border: 1px solid var(--border);
            box-shadow: var(--shadow);
        }

        h2 {
            text-align: center;
            margin-bottom: 22px;
            font-weight: 600;
        }

        label {
            font-size: 13px;
            color: var(--muted);
            font-weight: 500;
            margin-bottom: 4px;
        }

        .form-control {
            border-radius: 10px;
            border: 1px solid var(--border);
            padding: 10px;
            font-size: 14px;
        }

        .form-control:focus {
            border-color: var(--primary);
            box-shadow: 0 0 0 3px rgba(37,99,235,0.2);
        }

        select[multiple] {
            height: 120px;
        }

        .hint {
            font-size: 12px;
            color: var(--muted);
            margin-top: 4px;
        }

        .btn-submit {
            margin-top: 22px;
            background: linear-gradient(135deg, var(--primary), var(--primary-hover));
            color: white;
            border-radius: 10px;
            padding: 11px;
            font-weight: 600;
            border: none;
            width: 100%;
            transition: 0.2s;
        }

        .btn-submit:hover {
            transform: translateY(-1px);
            box-shadow: 0 10px 25px rgba(37,99,235,0.3);
        }

        .server-msg {
            padding: 10px;
            border-radius: 10px;
            font-size: 13px;
            text-align: center;
            margin-bottom: 14px;
        }

        .server-msg.error {
            background: var(--danger-bg);
            border: 1px solid var(--danger-border);
            color: var(--danger);
        }
    </style>
</head>

<body>

<a href="${pageContext.request.contextPath}/product/list" class="back-arrow">←</a>

<div class="form-card">

    <h2>Add Product</h2>

    <c:if test="${not empty message}">
        <div class="server-msg error">
            ${message}
        </div>
    </c:if>

    <form:form
            action="${pageContext.request.contextPath}/product/add"
            method="post"
            modelAttribute="productDto">

        <div class="mb-3">
            <label>Product Name</label>
            <form:input path="productName"
                        cssClass="form-control"
                        required="true"/>
        </div>

        <div class="mb-3">
            <label>SKU Code</label>
            <form:input path="identifier"
                        cssClass="form-control"
                        required="true"/>
        </div>

        <div class="mb-3">
            <label>Category</label>
            <form:select path="category" multiple="true" cssClass="form-control">
                <c:forEach var="c" items="${categories}">
                    <form:option value="${c.identifier}" label="${c.identifier}"/>
                </c:forEach>
            </form:select>
            <div class="hint">Hold Ctrl / Cmd to select multiple</div>
        </div>

        <div class="mb-3">
            <label>Brand</label>
            <form:select path="brand" multiple="true" cssClass="form-control">
                <c:forEach var="b" items="${brand}">
                    <form:option value="${b.identifier}" label="${b.identifier}"/>
                </c:forEach>
            </form:select>
        </div>

        <div class="mb-3">
            <label>Model</label>
            <form:select path="model" multiple="true" cssClass="form-control">
                <c:forEach var="m" items="${model}">
                    <form:option value="${m.identifier}" label="${m.identifier}"/>
                </c:forEach>
            </form:select>
        </div>

        <div class="mb-3">
            <label>Unit</label>
            <form:select path="unit" multiple="true" cssClass="form-control">
                <c:forEach var="u" items="${unit}">
                    <form:option value="${u.identifier}" label="${u.identifier}"/>
                </c:forEach>
            </form:select>
        </div>

        <button type="submit" class="btn-submit">
            Add Product
        </button>

    </form:form>

</div>

</body>
</html>