<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Warehouse Management</title>

    <style>

        /* ===== BODY ===== */

        body {
            margin: 0;
            font-family: "Inter", sans-serif;
            background-color: #3f3f3f;
            min-height: 100vh;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        /* ===== PAGE WRAPPER ===== */

        .page-wrapper {
            width: 980px;
            background: #f3efe9;
            padding: 34px 42px;
            box-sizing: border-box;
        }

        /* ===== HEADER ===== */

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

        /* ===== BUTTONS ===== */

        .top-actions {
            display: flex;
            align-items: center;
            gap: 12px;
        }

        .top-btn,
        .back-btn {
            height: 48px;
            min-width: 140px;
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

        /* ===== TABLE ===== */

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

        /* ===== ACTION BUTTONS ===== */

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

        /* ===== EMPTY ===== */

        .empty-message {
            text-align: center;
            padding: 30px 0;
            color: #8a8a8a;
            font-size: 14px;
        }

    </style>

</head>

<body>

<div class="page-wrapper">

    <!-- ===== HEADER ===== -->

    <div class="top-section">

        <h2 class="page-title">
            Warehouse Management
        </h2>

        <div class="top-actions">

            <a href="${pageContext.request.contextPath}/"
               class="back-btn">

                ᐸ BACK

            </a>

            <a href="${pageContext.request.contextPath}/wareHouse/add"
               class="top-btn">

                ADD WAREHOUSE

            </a>

        </div>

    </div>

    <!-- ===== EMPTY MESSAGE ===== -->

    <c:if test="${empty wareHouses}">

        <div class="empty-message">

            No warehouses found

        </div>

    </c:if>

    <!-- ===== TABLE ===== -->

    <c:if test="${not empty wareHouses}">

        <table>

            <thead>

            <tr>

                <th>WAREHOUSE</th>
                <th>LOCATION</th>
                <th>MANAGER</th>
                <th>ACTION</th>

            </tr>

            </thead>

            <tbody>

            <c:forEach var="wh" items="${wareHouses}">

                <tr>

                    <td>${wh.identifier}</td>

                    <td>${wh.location}</td>

                    <td>${wh.manager}</td>

                    <td>

                        <div class="action-buttons">

                            <a href="${pageContext.request.contextPath}/wareHouse/get?identifier=${wh.identifier}"
                               class="action-link edit"
                               title="Edit">

                                ✎

                            </a>

                            <a href="${pageContext.request.contextPath}/wareHouse/delete?identifier=${wh.identifier}"
                               class="action-link delete"
                               title="Delete"
                               onclick="return confirm('Delete this warehouse?')">

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