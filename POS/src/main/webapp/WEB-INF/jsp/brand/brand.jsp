<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS Retail Management | Brand Details</title>

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
            max-width: 460px;
            border-radius: 18px;
            box-shadow: 0 10px 24px rgba(0,0,0,0.08);
            overflow: hidden;
        }

        .brand-header {
            background: #0B3C5D;
            padding: 24px;
            color: #FFFFFF;
            text-align: center;
            font-size: 20px;
            font-weight: 700;
        }

        .profile-body {
            padding: 32px 36px;
        }

        h2 {
            text-align: center;
            margin-bottom: 26px;
            color: #1F2937;
            font-size: 20px;
        }

        .profile-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 18px;
        }

        .label {
            font-weight: 700;
            color: #4B5563;
            width: 45%;
        }

        .value {
            width: 55%;
            text-align: right;
            font-weight: 600;
        }

        .fixed-value {
            opacity: 0.85;
            cursor: not-allowed;
        }

        textarea,
        input[type="file"] {
            width: 55%;
            display: none;
            padding: 8px;
            border-radius: 6px;
            border: 1px solid #D1D5DB;
            font-size: 14px;
        }

        textarea {
            resize: none;
            height: 90px;
        }

        .brand-icon {
            width: 52px;
            height: 52px;
            object-fit: contain;
            border-radius: 10px;
            border: 1px solid #E5E7EB;
            background: #FFFFFF;
        }

        .actions {
            text-align: center;
            margin-top: 28px;
        }

        .btn {
            padding: 10px 26px;
            border-radius: 8px;
            border: none;
            cursor: pointer;
            font-weight: 700;
            font-size: 14px;
        }

        .edit-btn {
            background: #0B3C5D;
            color: #FFFFFF;
        }

        .save-btn {
            background: #16A34A;
            color: #FFFFFF;
            display: none;
        }

        .back-link {
            display: block;
            margin-top: 22px;
            text-align: center;
            font-size: 14px;
            font-weight: 600;
            color: #6B7280;
            text-decoration: none;
        }

    </style>
</head>

<body>
<div class="profile-card">
    <div class="brand-header">POS Management</div>

    <div class="profile-body">
        <form action="${pageContext.request.contextPath}/brand/update"
              method="post"
              enctype="multipart/form-data">

            <input type="hidden" name="id" value="${brand.id}" />
            <input type="hidden" name="identifier" value="${brand.identifier}" />

            <h2>Brand Configuration</h2>

            <div class="profile-row">
                <span class="label">Brand Identifier</span>
                <span class="value fixed-value">${brand.identifier}</span>
            </div>

            <div class="profile-row">
                <span class="label">Description</span>
                <span class="value">${brand.description}</span>
                <textarea class="editable" name="description">${brand.description}</textarea>
            </div>

            <div class="actions">
                <button type="button" class="btn edit-btn" onclick="enableEdit()">Edit Brand</button>
                <button type="submit" class="btn save-btn">Save Changes</button>
            </div>

            <a href="${pageContext.request.contextPath}/brand/list" class="back-link">
                ← Back to Brand List
            </a>
        </form>
    </div>
</div>

<script>
    function enableEdit() {
        document.querySelectorAll('.value:not(.fixed-value)')
            .forEach(v => v.style.display = 'none');

        document.querySelectorAll('.editable')
            .forEach(i => i.style.display = 'inline-block');

        document.querySelector('.edit-btn').style.display = 'none';
        document.querySelector('.save-btn').style.display = 'inline-block';

        document.querySelector('h2').innerText = "Edit Brand Details";
    }
</script>

</body>
</html>
