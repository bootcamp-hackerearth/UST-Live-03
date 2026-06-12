<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS Management | Price Profile</title>

    <style>
        body {
            margin: 0;
            padding: 0;
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
            color: #1F2937;
        }

        .profile-row {
            display: flex;
            justify-content: space-between;
            padding: 14px 0;
            align-items: center;
        }

        .label {
            font-weight: 700;
            width: 45%;
            color: #4B5563;
        }

        .value {
            width: 55%;
            text-align: right;
            font-weight: 600;
        }

        .readonly-value {
            color: #6B7280;
        }

        input[type="number"] {
            width: 55%;
            padding: 6px;
            display: none;
        }

        .actions {
            text-align: center;
            margin-top: 25px;
        }

        .btn {
            padding: 10px 24px;
            border-radius: 8px;
            cursor: pointer;
            border: none;
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
        <h1>POS Management</h1>
    </div>

    <div class="profile-body">
        <form action="${pageContext.request.contextPath}/price/update" method="post">

            <!-- DB identity -->
            <input type="hidden" name="id" value="${price.id}" />
            <input type="hidden" name="identifier" value="${price.identifier}" />
            <input type="hidden" name="effectiveFrom" value="${price.effectiveFrom}" />

            <h2>Price Configuration</h2>

            <!-- READ-ONLY -->
            <div class="profile-row">
                <span class="label">Product Name</span>
                <span class="value readonly-value">${price.identifier}</span>
            </div>

            <!-- EDITABLE -->
            <div class="profile-row">
                <span class="label">MRP</span>
                <span class="value">₹ ${price.mrp}</span>
                <input class="editable" type="number" step="0.01" name="mrp" value="${price.mrp}" />
            </div>

            <!-- EDITABLE -->
            <div class="profile-row">
                <span class="label">Selling Price</span>
                <span class="value">₹ ${price.sellingPrice}</span>
                <input class="editable" type="number" step="0.01" name="sellingPrice"
                       value="${price.sellingPrice}" />
            </div>

            <!-- EDITABLE -->
            <div class="profile-row">
                <span class="label">Cost Price</span>
                <span class="value">₹ ${price.costPrice}</span>
                <input class="editable" type="number" step="0.01" name="costPrice"
                       value="${price.costPrice}" />
            </div>

            <!-- READ-ONLY -->
            <div class="profile-row">
                <span class="label">Effective From</span>
                <span class="value readonly-value">${price.effectiveFrom}</span>
            </div>

            <div class="actions">
                <button type="button" class="btn edit-btn" onclick="enableEdit()">Edit Price</button>
                <button type="submit" class="btn save-btn">Save Changes</button>
            </div>

            <a href="${pageContext.request.contextPath}/price/list" class="back-link">
                ← Back to Price List
            </a>
        </form>
    </div>
</div>

<script>
    function enableEdit() {

        // Hide only editable display values
        document.querySelectorAll('.value:not(.readonly-value)')
            .forEach(v => v.style.display = 'none');

        // Show editable inputs
        document.querySelectorAll('.editable')
            .forEach(i => i.style.display = 'inline-block');

        document.querySelector('.edit-btn').style.display = 'none';
        document.querySelector('.save-btn').style.display = 'inline-block';

        document.querySelector('h2').innerText = "Edit Price Details";
    }
</script>

</body>
</html>