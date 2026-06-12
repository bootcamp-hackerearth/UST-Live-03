<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>POS Management | Product Profile</title>

<style>
    body {
        margin: 0;
        font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
        background: #F4F5F7;
        min-height: 100vh;
        display: flex;
        align-items: center;
        justify-content: center;
    }

    .profile-card {
        background: #FFFFFF;
        width: 100%;
        max-width: 450px;
        border-radius: 16px;
        box-shadow: 0 8px 20px rgba(0,0,0,0.08);
        overflow: hidden;
    }

    .brand-header {
        background: #0B3C5D;
        padding: 25px;
        color: #FFFFFF;
        text-align: center;
    }

    .profile-body {
        padding: 30px 40px;
    }

    h2 {
        text-align: center;
        margin-bottom: 20px;
        font-weight: 600;
    }

    .profile-row {
        display: flex;
        justify-content: space-between;
        padding: 14px 0;
        align-items: center;
    }

    .label {
        width: 40%;
        font-weight: 700;
    }

    .value {
        width: 60%;
        text-align: right;
        font-weight: 600;
    }

    .readonly {
        color: #6B7280;
    }

    select,
    input[type="text"] {
        width: 60%;
        display: none;
    }

    .editable {
        display: none;
    }

    .actions {
        text-align: center;
        margin-top: 25px;
    }

    .btn {
        padding: 10px 24px;
        border-radius: 8px;
        border: none;
        cursor: pointer;
        font-weight: 600;
    }

    .edit-btn { background: #0B3C5D; color: white; }
    .save-btn { background: #16A34A; color: white; display: none; }

    .back-link {
        display: block;
        margin-top: 20px;
        font-size: 13px;
        color: #6B7280;
        text-decoration: none;
        font-weight: 600;
        text-align: center;
    }
</style>
</head>

<body>

<div class="profile-card">
    <div class="brand-header">
        <h1>POS Management</h1>
    </div>

    <div class="profile-body">
        <form action="${pageContext.request.contextPath}/product/update" method="post">

            <input type="hidden" name="id" value="${product.id}" />
            <input type="hidden" name="identifier" value="${product.identifier}" />

            <h2>Product Configuration</h2>

            <div class="profile-row">
                <span class="label">SKU Code</span>
                <span class="value readonly">${product.identifier}</span>
            </div>

            <div class="profile-row">
                <span class="label">Product Name</span>
                <span class="value editable-value">${product.name}</span>

                <input class="editable" type="text"
                       name="name"
                       value="${product.name}" />
            </div>

            <div class="profile-row">
                <span class="label">Brand</span>
                <span class="value editable-value">${product.brand}</span>
                <select class="editable" name="brand">
                    <c:forEach var="b" items="${brands}">
                        <option value="${b.identifier}"
                            ${product.brand eq b.identifier ? 'selected' : ''}>
                            ${b.identifier}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="profile-row">
                <span class="label">Model</span>
                <span class="value editable-value">${product.model}</span>
                <select class="editable" name="model">
                    <c:forEach var="m" items="${models}">
                        <option value="${m.identifier}"
                            ${product.model eq m.identifier ? 'selected' : ''}>
                            ${m.identifier}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="profile-row">
                <span class="label">Categories</span>
                <span class="value editable-value">
                    <c:forEach var="c" items="${product.categories}" varStatus="s">
                        ${c}<c:if test="${!s.last}">, </c:if>
                    </c:forEach>
                </span>

                <!-- ✅ FIXED -->
                <select class="editable" name="categories[]" multiple size="5">
                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.identifier}"
                            ${product.categories.contains(cat.identifier) ? 'selected' : ''}>
                            ${cat.identifier}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="profile-row">
                <span class="label">Current MRP</span>
                <span class="value readonly">
                    <c:choose>
                        <c:when test="${not empty mrp}">₹ ${mrp}</c:when>
                        <c:otherwise>Not available</c:otherwise>
                    </c:choose>
                </span>
            </div>

            <div class="actions">
                <button type="button" class="btn edit-btn" onclick="enableEdit()">Edit Product</button>
                <button type="submit" class="btn save-btn">Save Changes</button>
            </div>

            <a href="${pageContext.request.contextPath}/product/list" class="back-link">
                ← Back to Product List
            </a>

        </form>
    </div>
</div>

<script>
function enableEdit() {
    document.querySelectorAll('.editable-value')
        .forEach(v => v.style.display = 'none');

    document.querySelectorAll('.editable')
        .forEach(e => e.style.display = 'inline-block');

    document.querySelector('.edit-btn').style.display = 'none';
    document.querySelector('.save-btn').style.display = 'inline-block';

    document.querySelector('h2').innerText = "Edit Product Details";
}
</script>

</body>
</html>
