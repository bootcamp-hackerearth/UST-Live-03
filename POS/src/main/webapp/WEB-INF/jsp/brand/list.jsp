<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Brand List</title>

    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600&display=swap" rel="stylesheet"/>

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
            width: 1000px;
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

        .home-link {
            position: absolute;
            top: 16px;
            right: 16px;
            font-size: 14px;
            font-weight: 600;
            color: #4b6cb7;
            text-decoration: none;
            padding: 8px 14px;
            border-radius: 8px;
            background: rgba(75,108,183,0.08);
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th {
            background: #4b6cb7;
            color: #fff;
            padding: 12px;
            font-size: 14px;
        }

        td {
            padding: 12px;
            text-align: center;
            border-bottom: 1px solid #eee;
            font-size: 14px;
        }

        tr:hover {
            background: #f7f9ff;
        }

        img {
            width: 40px;
            height: 40px;
            border-radius: 50%;
        }

        .toggle-switch {
            position: relative;
            width: 52px;
            height: 26px;
            display: inline-block;
        }

        .toggle-switch input {
            display: none;
        }

        .toggle-slider {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background-color: #e74c3c;
            border-radius: 30px;
            transition: .3s;
            cursor: pointer;
        }

        .toggle-slider:before {
            content: "";
            position: absolute;
            height: 20px;
            width: 20px;
            left: 3px;
            bottom: 3px;
            background: white;
            border-radius: 50%;
            transition: .3s;
        }

        .toggle-switch input:checked + .toggle-slider {
            background-color: #2ecc71;
        }

        .toggle-switch input:checked + .toggle-slider:before {
            transform: translateX(26px);
        }

        .action-icon {
            font-size: 18px;
            margin: 0 6px;
            text-decoration: none;
            color: #4b6cb7;
        }

        .footer-actions {
            margin-top: 20px;
            text-align: center;
        }

        .btn-add {
            padding: 12px 18px;
            border-radius: 10px;
            text-decoration: none;
            font-weight: 600;
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: #fff;
        }
    </style>
</head>

<body>

<div class="card-container">

    <a href="/" class="home-link">Home</a>

    <h2>List of Brands</h2>

    <table id="brandTable">
        <thead>
        <tr>
            <th>SL</th>
            <th>Brand Name</th>
            <th>Description</th>
            <th>Status</th>
            <th>Action</th>
        </tr>
        </thead>
        <tbody>

        <c:forEach items="${brands}" var="brand" varStatus="st">
            <tr>
                <td>${st.count}</td>
                <td class="brand-name">${brand.identifier}</td>
                <td>${brand.description}</td>
                <td>
                    <form action="/brand/toggleStatus" method="post" style="margin:0;">
                        <input type="hidden" name="identifier"
                               value="${brand.identifier}"/>

                        <label class="toggle-switch">
                            <input type="checkbox"
                                   ${brand.status ? "checked" : ""}
                                   onchange="this.form.submit()"/>
                            <span class="toggle-slider"></span>
                        </label>
                    </form>
                </td>

                <td>
                    <a href="/brand/get?identifier=${brand.identifier}"
                       class="action-icon" title="Edit">✏️</a>

                    <a href="/brand/delete?identifier=${brand.identifier}"
                       class="action-icon"
                       title="Delete"
                       onclick="return confirm('Delete this brand?');">🗑</a>
                </td>
            </tr>
        </c:forEach>

        </tbody>
    </table>

    <div class="footer-actions">
        <a href="/brand/add" class="btn-add">+ Add New Brand</a>
    </div>

</div>

<script>
    function searchBrand() {
        var input = document.getElementById("searchInput").value.toLowerCase();
        var rows = document.querySelectorAll("#brandTable tbody tr");

        rows.forEach(function (row) {
            var brand = row.querySelector(".brand-name")
                .innerText.toLowerCase();
            row.style.display = brand.includes(input) ? "" : "none";
        });
    }
</script>

</body>
</html>
``