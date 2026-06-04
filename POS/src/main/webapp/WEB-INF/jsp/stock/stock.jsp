<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS Retail Management | Stock Details</title>

    <style>
        body {
            margin: 0;
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            background: #F4F5F7;
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .profile-card {
            background: #FFFFFF;
            max-width: 450px;
            width: 100%;
            border-radius: 16px;
            box-shadow: 0 8px 20px rgba(0,0,0,0.08);
            overflow: hidden;
        }

        .brand-header {
            background: #0B3C5D;
            padding: 25px;
            color: white;
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

        input[type="number"],
        select {
            width: 60%;
            padding: 6px;
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

        .edit-btn {
            background: #0B3C5D;
            color: white;
        }

        .save-btn {
            background: #16A34A;
            color: white;
            display: none;
        }

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
        <h1>POS Retail Management</h1>
    </div>

    <div class="profile-body">
        <form action="${pageContext.request.contextPath}/stock/update" method="post">

            <input type="hidden" name="id" value="${stock.id}" />
            <input type="hidden" name="identifier" value="${stock.identifier}" />

            <h2>Stock Configuration</h2>

            <div class="profile-row">
                <span class="label">Product</span>
                <span class="value readonly">${stock.identifier}</span>
            </div>

            <div class="profile-row">
                <span class="label">Available Stock</span>
                <span class="value editable-value">${stock.availableStock}</span>
                <input class="editable" type="number"
                       name="availableStock"
                       value="${stock.availableStock}" min="0" />
            </div>

            <div class="profile-row">
                <span class="label">Outgoing Stock</span>
                <span class="value editable-value">${stock.outgoingStock}</span>
                <input class="editable" type="number"
                       name="outgoingStock"
                       value="${stock.outgoingStock}" min="0" />
            </div>

            <div class="profile-row">
                <span class="label">Warehouse</span>
                <span class="value editable-value">${stock.warehouse}</span>
                <select class="editable" name="warehouse">
                    <c:forEach var="wh" items="${warehouses}">
                        <option value="${wh.identifier}"
                                ${wh.identifier eq stock.warehouse ? 'selected' : ''}>
                            ${wh.identifier}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="profile-row">
                <span class="label">Product Status</span>
                <span class="value editable-value">${stock.productStatus}</span>
                <select class="editable" name="productStatus">
                    <option value="AVAILABLE" ${stock.productStatus eq 'AVAILABLE' ? 'selected' : ''}>AVAILABLE</option>
                    <option value="LOW_STOCK" ${stock.productStatus eq 'LOW_STOCK' ? 'selected' : ''}>LOW_STOCK</option>
                    <option value="OUT_OF_STOCK" ${stock.productStatus eq 'OUT_OF_STOCK' ? 'selected' : ''}>OUT_OF_STOCK</option>
                </select>
            </div>

            <div class="actions">
                <button type="button" class="btn edit-btn" onclick="enableEdit()">Edit Stock</button>
                <button type="submit" class="btn save-btn">Save Changes</button>
            </div>

            <a href="${pageContext.request.contextPath}/stock/list" class="back-link">
                ← Back to Stock List
            </a>
        </form>
    </div>
</div>

<script>
function enableEdit() {

    document.querySelectorAll('.editable-value')
        .forEach(v => v.style.display = 'none');

    document.querySelectorAll('.editable')
        .forEach(i => i.style.display = 'inline-block');

    document.querySelector('.edit-btn').style.display = 'none';
    document.querySelector('.save-btn').style.display = 'inline-block';

    document.querySelector('h2').innerText = "Edit Stock Details";
}
</script>

</body>
</html>