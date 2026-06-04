<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>POS Retail Management | Add Rack</title>

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
            margin-bottom: 18px;
        }

        label {
            display: block;
            margin-bottom: 8px;
            font-weight: 600;
            color: #4B5563;
            font-size: 13px;
        }

        input, select {
            width: 100%;
            padding: 12px;
            border-radius: 8px;
            border: 1px solid #E5E7EB;
            box-sizing: border-box;
            font-size: 14px;
        }

        select[multiple] {
            height: 130px;
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
            margin-top: 15px;
        }

        .back-link {
            display: block;
            text-align: center;
            margin-top: 18px;
            color: #6B7280;
            text-decoration: none;
            font-size: 14px;
            font-weight: 600;
        }
    </style>
</head>

<body>

<div class="container">

    <div class="brand-header">
        <h1>POS Retail Management</h1>
    </div>

    <div class="form-body">
        <h2>Add Rack</h2>

        <c:if test="${not empty message}">
            <div class="${success ? 'success' : 'error'}">
                ${message}
            </div>
        </c:if>
        <form action="${pageContext.request.contextPath}/rack/add"
              method="post">

            <div class="form-group">
                <label>Rack Identifier</label>
                <input type="text"
                       name="identifier"
                       placeholder="e.g. RACK_A1"
                       required />
            </div>

            <div class="form-group">
                <label>Select Shelves</label>
                <select name="shelfs" multiple required>
                    <c:forEach var="shelf" items="${shelves}">
                        <option value="${shelf.identifier}">
                            ${shelf.identifier}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <button type="submit" class="btn-submit">
                Save Rack
            </button>

            <a href="${pageContext.request.contextPath}/rack/list"
               class="back-link">
                ← Cancel and Return
            </a>
        </form>
    </div>
</div>

</body>
</html>
