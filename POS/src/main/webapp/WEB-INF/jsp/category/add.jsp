<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS Retail Management | Add Category</title>

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

        .container {
            width: 100%;
            max-width: 430px;
            background: #ffffff;
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

        .brand-header h1 {
            margin: 0;
            font-size: 22px;
            font-weight: 600;
            letter-spacing: 0.8px;
        }

        .form-body {
            padding: 35px 40px;
        }

        h2 {
            color: #1F2937;
            font-size: 18px;
            margin-bottom: 20px;
            text-align: center;
            font-weight: 600;
        }

        .form-group {
            margin-bottom: 20px;
        }

        label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #4B5563;
            font-size: 13px;
        }

        input[type="text"],
        select {
            width: 100%;
            padding: 12px;
            border-radius: 8px;
            border: 1px solid #E5E7EB;
            box-sizing: border-box;
            font-size: 14px;
            font-family: inherit;
            transition: border-color 0.2s;
        }

        input:focus,
        select:focus {
            outline: none;
            border-color: #0B3C5D;
            background: #F9FAFB;
        }

        .btn-submit {
            width: 100%;
            padding: 12px;
            border-radius: 8px;
            border: none;
            background: #0B3C5D;
            color: white;
            cursor: pointer;
            font-weight: 600;
            font-size: 15px;
            transition: opacity 0.2s;
            margin-top: 10px;
        }

        .btn-submit:hover {
            opacity: 0.9;
        }

        .back-link {
            display: block;
            text-align: center;
            margin-top: 20px;
            color: #6B7280;
            text-decoration: none;
            font-size: 14px;
            font-weight: 600;
        }

        .back-link:hover {
            color: #0B3C5D;
            text-decoration: underline;
        }
    </style>
</head>

<body>

<div class="container">
    <div class="brand-header">
        <h1>POS Retail Management</h1>
    </div>

    <div class="form-body">
        <h2>Create New Category</h2>

        <form action="${pageContext.request.contextPath}/category/add" method="post">

            <div class="form-group">
                <label>Category Name</label>
                <input type="text"
                       name="identifier"
                       placeholder="e.g. ELECTRONICS"
                       required
                       autofocus />
            </div>

            <div class="form-group">
                <label>Super Category</label>
                <select name="superCategory">
                    <option value="">-- No Parent (Top Level) --</option>

                    <c:forEach var="cat" items="${categories}">
                        <option value="${cat.identifier}">
                            ${cat.identifier}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <button type="submit" class="btn-submit">
                Save Category
            </button>

            <a href="${pageContext.request.contextPath}/category/list"
               class="back-link">
                ← Cancel and Return
            </a>

        </form>
    </div>
</div>

</body>
</html>