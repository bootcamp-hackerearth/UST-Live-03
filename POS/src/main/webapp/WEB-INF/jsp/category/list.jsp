<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Retail POS | Category List</title>

    <link rel="stylesheet"
          href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.1/css/all.min.css"/>

    <style>
        :root {
            --primary: #0f172a;
            --bg-light: #f8fafc;
            --border: #e2e8f0;
            --card-bg: #ffffff;
            --success: #22c55e;
            --danger: #ef4444;
        }

        body {
            margin: 0;
            min-height: 100vh;
            background: var(--bg-light);
            font-family: system-ui, -apple-system, sans-serif;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 40px;
        }

        .container {
            width: 100%;
            max-width: 1000px;
            background: var(--card-bg);
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 4px 6px -1px rgb(0 0 0 / 0.1);
            border-top: 5px solid var(--primary);
        }

        h2 {
            text-align: center;
            margin-bottom: 25px;
            color: var(--primary);
        }

        table {
            width: 100%;
            border-collapse: collapse;
        }

        th, td {
            padding: 14px;
            border-bottom: 1px solid var(--border);
            text-align: center;
            font-size: 14px;
        }

        th {
            background: #f1f5f9;
            text-transform: uppercase;
            font-size: 11px;
            letter-spacing: 0.05em;
            color: #64748b;
        }

        .switch {
            position: relative;
            display: inline-block;
            width: 40px;
            height: 20px;
        }

        .switch input { opacity: 0; width: 0; height: 0; }

        .slider {
            position: absolute;
            cursor: pointer;
            inset: 0;
            background-color: #cbd5e1;
            transition: .3s;
            border-radius: 34px;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 14px;
            width: 14px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            transition: .3s;
            border-radius: 50%;
        }

        input:checked + .slider { background-color: var(--success); }
        input:checked + .slider:before { transform: translateX(20px); }

        .status-label {
            font-size: 11px;
            font-weight: 700;
            margin-left: 8px;
        }

        .text-active { color: var(--success); }
        .text-inactive { color: #94a3b8; }

        .action-icon {
            color: #64748b;
            margin: 0 10px;
            font-size: 16px;
            text-decoration: none;
        }

        .action-icon:hover { color: var(--primary); }
        .delete-btn:hover { color: var(--danger) !important; }

        #toast {
            visibility: hidden;
            min-width: 280px;
            background: #1e293b;
            color: #fff;
            text-align: center;
            border-radius: 8px;
            padding: 16px;
            position: fixed;
            left: 50%;
            bottom: 30px;
            transform: translateX(-50%);
            z-index: 100;
        }

        #toast.show {
            visibility: visible;
            animation: fadein 0.4s, fadeout 0.4s 2.6s;
        }

        @keyframes fadein {
            from { opacity: 0; bottom: 0; }
            to { opacity: 1; bottom: 30px; }
        }
        @keyframes fadeout {
            from { opacity: 1; bottom: 30px; }
            to { opacity: 0; bottom: 0; }
        }

        .footer {
            margin-top: 30px;
            text-align: center;
            border-top: 1px solid var(--border);
            padding-top: 20px;
        }

        .footer a {
            margin: 0 15px;
            font-weight: 600;
            text-decoration: none;
            color: var(--primary);
        }
    </style>
</head>

<body>

<div class="container">
    <h2>Registered Categories</h2>

    <c:if test="${empty categorys}">
        <div style="text-align:center;color:#64748b;">No categories found.</div>
    </c:if>

    <c:if test="${not empty categorys}">
        <table>
            <thead>
            <tr>
                <th>Sl.</th>
                <th>Category</th>
                <th>Super Category</th>
                <th>Status</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="category" items="${categorys}" varStatus="loop">
                <tr>
                    <td>${loop.index + 1}</td>
                    <td><strong>${category.identifier}</strong></td>
                    <td>${category.superCategory}</td>
                    <td>
                        <label class="switch">
                            <input type="checkbox"
                                   class="status-toggle"
                                   data-id="${category.identifier}"
                                   ${category.status ? 'checked' : ''}>
                            <span class="slider"></span>
                        </label>
                        <span class="status-label ${category.status ? 'text-active' : 'text-inactive'}">
                            ${category.status ? 'ACTIVE' : 'INACTIVE'}
                        </span>
                    </td>
                    <td>
                        <a class="action-icon"
                           href="${pageContext.request.contextPath}/category/get?identifier=${category.identifier}">
                            <i class="fa fa-pen-to-square"></i>
                        </a>
                        <a class="action-icon delete-btn"
                           href="${pageContext.request.contextPath}/category/delete?identifier=${category.identifier}"
                           onclick="return confirm('Delete this category?')">
                            <i class="fa fa-trash-can"></i>
                        </a>
                    </td>
                </tr>
            </c:forEach>
            </tbody>
        </table>
    </c:if>

    <div class="footer">
        <a href="${pageContext.request.contextPath}/">Home</a>
        <a href="${pageContext.request.contextPath}/category/add">Add Category</a>
    </div>
</div>

<div id="toast"></div>

<script>
    document.querySelectorAll('.status-toggle').forEach(toggle => {
        toggle.addEventListener('change', function () {
            const id = this.dataset.id;
            const checked = this.checked;
            const label = this.closest('td').querySelector('.status-label');

            fetch(`${pageContext.request.contextPath}/category/toggle?identifier=` +
                encodeURIComponent(id) + `&status=` + checked, {
                method: 'POST'
            })
            .then(res => {
                if (!res.ok) throw new Error('Failed');
                label.innerText = checked ? 'ACTIVE' : 'INACTIVE';
                label.className = `status-label ${checked ? 'text-active' : 'text-inactive'}`;
                showToast("Category status updated");
            })
            .catch(() => {
                this.checked = !checked;
                alert("Status update failed");
            });
        });
    });

    function showToast(msg) {
        const toast = document.getElementById("toast");
        toast.innerText = msg;
        toast.className = "show";
        setTimeout(() => toast.className = "", 3000);
    }
</script>

</body>
</html>