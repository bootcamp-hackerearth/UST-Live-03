<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Brand List</title>

    <style>
        body {
            margin: 0;
            font-family: 'Poppins', sans-serif;
            min-height: 100vh;
            background: #ffffff;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .card-container {
            position: relative;
            width: 900px;
            background: rgba(255, 255, 255, 0.95);
            padding: 35px 40px;
            border-radius: 16px;
            box-shadow: 0 25px 50px rgba(0, 0, 0, 0.25);
        }

        h2 {
            text-align: center;
            margin-bottom: 25px;
            color: #4b6cb7;
            font-weight: 600;
        }

        .back-icon {
            position: absolute;
            top: 16px;
            left: 16px;
            width: 36px;
            height: 36px;
            display: flex;
            align-items: center;
            justify-content: center;
            font-size: 20px;
            color: #4b6cb7;
            text-decoration: none;
            font-weight: 600;
            background: rgba(75, 108, 183, 0.08);
            border-radius: 50%;
            box-shadow: 0 4px 10px rgba(0, 0, 0, 0.12);
            transition: all 0.25s ease;
        }

        .back-icon:hover {
            background: #4b6cb7;
            color: #ffffff;
            transform: translateX(-4px) scale(1.05);
            box-shadow: 0 8px 18px rgba(75, 108, 183, 0.35);
        }

        table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 10px;
        }

        th {
            background: #4b6cb7;
            color: white;
            padding: 12px;
            font-size: 14px;
        }

        td {
            padding: 12px;
            text-align: center;
            border-bottom: 1px solid #eee;
            font-size: 14px;
            color: #333;
            word-break: break-word;
        }

        tr:hover {
            background: #f7f9ff;
        }

        img {
            width: 36px;
            height: 36px;
            border-radius: 50%;
        }

        .action-icon {
            font-size: 18px;
            margin: 0 6px;
            text-decoration: none;
            color: #4b6cb7;
            transition: 0.2s ease;
        }

        .action-icon:hover {
            color: #182848;
            transform: scale(1.2);
        }

        .footer-actions {
            margin-top: 20px;
            display: flex;
            justify-content: center;
            gap: 12px;
        }

        .btn {
            padding: 10px 16px;
            border-radius: 10px;
            text-decoration: none;
            font-weight: 600;
            font-size: 14px;
            display: inline-block;
            transition: 0.25s ease;
        }

        .btn-home {
            background: #6c757d;
            color: white;
        }

        .btn-home:hover {
            background: #555;
        }

        .btn-add {
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: white;
        }

        .btn-add:hover {
            transform: scale(1.05);
        }

        /* ===== Toggle Switch (UNCHANGED) ===== */
        .switch {
            position: relative;
            display: inline-block;
            width: 50px;
            height: 24px;
        }

        .switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        .slider {
            position: absolute;
            cursor: pointer;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background-color: #dc3545;
            transition: 0.4s;
            border-radius: 24px;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 18px;
            width: 18px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            transition: 0.4s;
            border-radius: 50%;
        }

        input:checked + .slider {
            background-color: #28a745;
        }

        input:checked + .slider:before {
            transform: translateX(26px);
        }
    </style>
</head>

<body>

<div class="card-container">

    <a href="/" class="back-icon">←</a>

    <h2>Brand List</h2>

    <table>
        <thead>
        <tr>
            <th>SL</th>
            <th>Icon</th>
            <th>Brand Name</th>
            <th>Description</th>
            <th>Status</th>
            <th>Action</th>
        </tr>
        </thead>

        <tbody>
        <c:forEach items="${brands}" var="brand" varStatus="loop">
            <tr>
                <td>${loop.index + 1}</td>

                <td>
                    <img src="/uploads/${brand.icon}" alt="Icon">
                </td>

                <td>${brand.identifier}</td>
                <td>${brand.description}</td>

                <td>
                    <form action="/brand/status" method="post">
                        <input type="hidden" name="identifier" value="${brand.identifier}"/>

                        <label class="switch">
                            <input type="checkbox"
                                   name="status"
                                   value="true"
                                   ${brand.status ? "checked" : ""}
                                   onchange="this.form.submit()" />
                            <span class="slider"></span>
                        </label>

                        <input type="hidden" name="status" value="false"/>
                    </form>
                </td>

                <td>
                    <a href="/brand/get?identifier=${brand.identifier}"
                       class="action-icon" title="Edit">✏️</a>

                    <a href="/brand/delete?identifier=${brand.identifier}"
                       class="action-icon"
                       title="Delete"
                       onclick="return confirm('Delete this brand?')">🗑</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <div class="footer-actions">
        <a href="/" class="btn btn-home">Home</a>
        <a href="/brand/add" class="btn btn-add">Add Brand</a>
    </div>

</div>

</body>
</html>