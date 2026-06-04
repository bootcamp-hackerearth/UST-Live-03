<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Stock Management</title>

    <style>

        body {
            margin: 0;
            font-family: "Inter", sans-serif;
            background-color: #3f3f3f;
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .page-wrapper {
            width: 1200px;
            background: #f3efe9;
            padding: 34px 42px;
            box-sizing: border-box;
        }

        .top-section {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 28px;
        }

        .page-title {
            font-size: 26px;
            font-weight: 700;
            color: #2f2f2f;
            margin: 0;
        }

        .top-actions {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .top-btn,
        .back-btn {
            height: 48px;
            min-width: 120px;
            padding: 0 18px;
            display: flex;
            align-items: center;
            justify-content: center;
            box-sizing: border-box;
            text-decoration: none;
            font-size: 12px;
            font-weight: 700;
            letter-spacing: 1px;
            border: 2px solid #3f3f3f;
            transition: 0.3s;
        }

        .top-btn {
            background: #3f3f3f;
            color: #ffffff;
        }

        .top-btn:hover {
            background: transparent;
            color: #3f3f3f;
        }

        .back-btn {
            background: transparent;
            color: #3f3f3f;
        }

        .back-btn:hover {
            background: #3f3f3f;
            color: #ffffff;
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th {
            text-align: left;
            padding: 14px 12px;
            font-size: 11px;
            letter-spacing: 2px;
            color: #8a8a8a;
            border-bottom: 3px solid #d6d1cb;
        }

        td {
            padding: 20px 12px;
            border-bottom: 2px solid #dedad5;
            color: #2f2f2f;
            font-size: 14px;
            vertical-align: middle;
        }

        .action-buttons {
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .action-link {
            width: 44px;
            height: 44px;
            display: flex;
            align-items: center;
            justify-content: center;
            box-sizing: border-box;
            text-decoration: none;
            font-size: 18px;
            font-weight: 700;
            border: 2px solid #3f3f3f;
            transition: 0.3s;
        }

        .edit {
            background: #3f3f3f;
            color: #ffffff;
        }

        .edit:hover {
            background: transparent;
            color: #3f3f3f;
        }

        .delete {
            background: transparent;
            color: #3f3f3f;
        }

        .delete:hover {
            background: #3f3f3f;
            color: #ffffff;
        }

        .empty {
            text-align: center;
            padding: 20px;
            font-size: 15px;
            color: #2f2f2f;
        }

    </style>

</head>

<body>

<div class="page-wrapper">

    <div class="top-section">

        <h2 class="page-title">
            Stock Management
        </h2>

        <div class="top-actions">

            <a href="${pageContext.request.contextPath}/"
               class="back-btn">

                ᐸ BACK

            </a>

            <a href="${pageContext.request.contextPath}/stocks/add"
               class="top-btn">

                ADD STOCK

            </a>

        </div>

    </div>

    <c:if test="${empty stocks}">

        <div class="empty">

            No stock records found

        </div>

    </c:if>

    <c:if test="${not empty stocks}">

        <table>

            <thead>

            <tr>

                <th>SKU CODE</th>
                <th>PRODUCT NAME</th>
                <th>AVAILABLE</th>
                <th>INCOMING</th>
                <th>OUTGOING</th>
                <th>STATUS</th>
                <th>WAREHOUSE</th>
                <th>ACTION</th>

            </tr>

            </thead>

            <tbody>

            <c:forEach var="stock" items="${stocks}">

                <tr>

                    <td>${stock.identifier}</td>
                    <td>${stock.name}</td>
                    <td>${stock.availableStock}</td>
                    <td>${stock.incomingStock}</td>
                    <td>${stock.outgoingStock}</td>
                    <td>${stock.productStatus}</td>
                    <td>${stock.wareHouse}</td>

                    <td>

                        <div class="action-buttons">

                            <a href="${pageContext.request.contextPath}/stocks/get?identifier=${stock.identifier}"
                               class="action-link edit"
                               title="Edit">

                                ✎

                            </a>

                            <a href="${pageContext.request.contextPath}/stocks/delete?identifier=${stock.identifier}"
                               class="action-link delete"
                               title="Delete"
                               onclick="return confirm('Are you sure you want to delete this product stock?');">

                                🗑

                            </a>

                        </div>

                    </td>

                </tr>

            </c:forEach>

            </tbody>

        </table>

    </c:if>

</div>

</body>
</html>