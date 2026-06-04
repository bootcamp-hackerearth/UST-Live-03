<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Brand List</title>

    <style>
        body {
            font-family: 'Poppins', sans-serif;
            padding: 30px;
            background: #f7f8fc;
        }

        .top-bar {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
        }

        .search-bar {
            margin-bottom: 20px;
            position: relative;
            width: 300px;
        }

        .search-bar input {
            width: 100%;
            padding: 10px 10px 10px 40px;
            border-radius: 8px;
            border: 1px solid #ccc;
        }

        .search-bar::before {
            content: "🔍";
            position: absolute;
            left: 12px;
            top: 50%;
            transform: translateY(-50%);
            pointer-events: none;
        }

        .add-btn {
            padding: 10px 18px;
            background: linear-gradient(135deg, #4b6cb7, #182848);
            color: #fff;
            border-radius: 8px;
            text-decoration: none;
            font-weight: 600;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            background: white;
            box-shadow: 0 10px 25px rgba(0,0,0,0.1);
        }

        th, td {
            padding: 14px;
            border-bottom: 1px solid #eee;
            text-align: center;
        }

        th {
            background: #f1f3f8;
            font-weight: 600;
        }

        img {
            width: 40px;
            height: 40px;
            border-radius: 50%;
        }

        .status-active {
            color: green;
            font-weight: 600;
        }

        .status-inactive {
            color: red;
            font-weight: 600;
        }

        .action a {
            font-size: 20px;
            text-decoration: none;   
            margin: 0 8px;
            cursor: pointer;
        }

        .bottom-bar {
            margin-top: 30px;
            display: flex;
            justify-content: center;
        }

        .home-btn {
            padding: 12px 30px;
            font-weight: 600;
            text-decoration: none;
            color: white;
            background: #182848;
            border-radius: 8px;
        }
    </style>
</head>

<body>

<div class="top-bar">
    <h2>Brand List</h2>
    <a href="/brand/add" class="add-btn">+ Add New Brand</a>
</div>

<div class="search-bar">
    <input type="text" id="searchInput"
           placeholder="Search by brand name..."
           onkeyup="searchBrand()">
</div>

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
        <c:forEach items="${brands}" var="brand" varStatus="loop">
            <tr>
                <td>${loop.index + 1}</td>
                <td class="brand-name">${brand.identifier}</td>
                <td>${brand.description}</td>
                <td>
                    <c:choose>
                        <c:when test="${brand.status}">
                            <span class="status-active">Active</span>
                        </c:when>
                        <c:otherwise>
                            <span class="status-inactive">Inactive</span>
                        </c:otherwise>
                    </c:choose>
                </td>
                <td class="action">
                    <a href="/brand/get?identifier=${brand.identifier}">✏️</a>
                    <a href="/brand/delete?identifier=${brand.identifier}"
                       onclick="return confirm('Delete this brand?')">🗑</a>
                </td>
            </tr>
        </c:forEach>
    </tbody>
</table>

<div class="bottom-bar">
    <a href="/" class="home-btn">Home</a>
</div>

<script>
    function searchBrand() {
        var input = document.getElementById("searchInput").value.toLowerCase();
        var rows = document.querySelectorAll("#brandTable tbody tr");

        rows.forEach(function(row) {
            var brand = row.querySelector(".brand-name")
                           .innerText.toLowerCase();
            row.style.display = brand.includes(input) ? "" : "none";
        });
    }
</script>

</body>
</html>
