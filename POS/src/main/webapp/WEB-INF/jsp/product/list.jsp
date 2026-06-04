<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Retail POS | Product List</title>

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
            max-width: 1200px;
            background: var(--card-bg);
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 4px 6px rgb(0 0 0 / 0.1);
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
            font-size: 11px;
            text-transform: uppercase;
            color: #64748b;
        }

        /* SWITCH */
        .switch {
            position: relative;
            display: inline-block;
            width: 40px;
            height: 20px;
        }

        .switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        .slider {
            position: absolute;
            inset: 0;
            background-color: #cbd5e1;
            transition: .3s;
            border-radius: 34px;
        }

        .slider:before {
            content: "";
            position: absolute;
            height: 14px;
            width: 14px;
            left: 3px;
            bottom: 3px;
            background-color: white;
            transition: .3s;
            border-radius: 50%;
        }

        input:checked + .slider {
            background-color: var(--success);
        }

        input:checked + .slider:before {
            transform: translateX(20px);
        }

        .status-label {
            font-size: 11px;
            font-weight: 700;
            margin-left: 8px;
            min-width: 60px;
            display: inline-block;
        }

        .text-active { color: var(--success); }
        .text-inactive { color: #94a3b8; }

        .action-icon {
            margin: 0 10px;
            color: #64748b;
            font-size: 16px;
            text-decoration: none;
        }

        .action-icon:hover { color: var(--primary); }
        .delete-btn:hover { color: var(--danger) !important; }

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

        #toast {
            visibility: hidden;
            min-width: 280px;
            background-color: #1e293b;
            color: white;
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
        }
    </style>
</head>

<body>

<div class="container">
    <h2>Product Master List</h2>

    <c:if test="${empty products}">
        <div style="text-align:center;color:#64748b;">
            No products found.
        </div>
    </c:if>

    <c:if test="${not empty products}">
        <table>
            <thead>
            <tr>
                <th>Sl</th>
                <th>Product</th>
                <th>category</th>
                <th>Brand</th>
                <th>Model</th>
                <th>Unit</th>
                <th>Status</th>
                <th>Action</th>
            </tr>
            </thead>

            <tbody>
            <c:forEach var="p" items="${products}" varStatus="loop">
                <tr>
                    <td>${loop.index + 1}</td>
                    <td><strong>${p.identifier}</strong></td>
                    <td>${p.category}</td>
                    <td>${p.brand}</td>
                    <td>${p.models}</td>
                    <td>${p.unit}</td>

                    <td>
                        <label class="switch">
                            <input type="checkbox"
                                   class="status-toggle"
                                   data-id="${p.identifier}"
                                   ${p.status ? 'checked' : ''}>
                            <span class="slider"></span>
                        </label>
                        <span class="status-label ${p.status ? 'text-active' : 'text-inactive'}">
                            ${p.status ? 'ACTIVE' : 'INACTIVE'}
                        </span>
                    </td>

                    <td>
                        <a class="action-icon"
                           href="${pageContext.request.contextPath}/product/get?identifier=${p.identifier}">
                            <i class="fa fa-pen-to-square"></i>
                        </a>

                        <a class="action-icon delete-btn"
                           href="${pageContext.request.contextPath}/product/delete?identifier=${p.identifier}"
                           onclick="return confirm('Delete this product?')">
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
        <a href="${pageContext.request.contextPath}/product/add">Add Product</a>
    </div>
</div>

<div id="toast"></div>

<script>
    document.querySelectorAll('.status-toggle').forEach(toggle => {
        toggle.addEventListener('change', function () {
            const identifier = this.dataset.id;
            const checked = this.checked;
            const label = this.closest('td').querySelector('.status-label');

            fetch('${pageContext.request.contextPath}/product/toggle?identifier='
                + encodeURIComponent(identifier) + '&status=' + checked, {
                method: 'POST'
            }).then(response => {
                if (response.ok) {
                    label.innerText = checked ? 'ACTIVE' : 'INACTIVE';
                    label.className = 'status-label ' +
                        (checked ? 'text-active' : 'text-inactive');
                    showToast('Product status updated');
                } else {
                    throw new Error();
                }
            }).catch(() => {
                this.checked = !checked;
                alert('Status update failed');
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
