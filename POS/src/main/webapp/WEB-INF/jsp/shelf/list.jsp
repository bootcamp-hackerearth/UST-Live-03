<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Retail POS | Shelf List</title>

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
            max-width: 900px;
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
            margin-top: 10px;
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
            vertical-align: middle;
        }

        .switch input { opacity: 0; width: 0; height: 0; }

        .slider {
            position: absolute;
            cursor: pointer;
            top: 0; left: 0; right: 0; bottom: 0;
            background-color: #cbd5e1;
            transition: .3s;
            border-radius: 34px;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 14px; width: 14px;
            left: 3px; bottom: 3px;
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
            display: inline-block;
            min-width: 60px;
            text-align: left;
        }

        .text-active { color: var(--success); }
        .text-inactive { color: #94a3b8; }

        .action-icon {
            margin: 0 10px;
            color: #64748b;
            font-size: 16px;
            text-decoration: none;
            transition: color 0.2s;
        }

        .action-icon:hover { color: var(--primary); }
        .delete-btn:hover { color: var(--danger) !important; }

        #toast {
            visibility: hidden;
            min-width: 280px;
            background-color: #1e293b;
            color: #fff;
            text-align: center;
            border-radius: 8px;
            padding: 16px;
            position: fixed;
            z-index: 100;
            left: 50%;
            bottom: 30px;
            transform: translateX(-50%);
            box-shadow: 0 10px 15px -3px rgba(0,0,0,0.1);
        }

        #toast.show { visibility: visible; animation: fadein 0.5s, fadeout 0.5s 2.5s; }

        @keyframes fadein { from {bottom: 0; opacity: 0;} to {bottom: 30px; opacity: 1;} }
        @keyframes fadeout { from {bottom: 30px; opacity: 1;} to {bottom: 0; opacity: 0;} }

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
    <h2>Registered Shelves</h2>

    <c:if test="${empty shelfs}">
        <div style="text-align: center; padding: 20px; color: #64748b;">No shelves found.</div>
    </c:if>

    <c:if test="${not empty shelfs}">
        <table>
            <thead>
            <tr>
                <th>Sl.</th>
                <th>Shelf Name</th>
                <th>description</th>
               <th>status</th>
                <th>Action</th>
            </tr>
            </thead>
            <tbody>
            <c:forEach var="shelf" items="${shelfs}" varStatus="loop">
                <tr id="row-${shelf.identifier}">
                    <td>${loop.index + 1}</td>
                    <td><strong>${shelf.identifier}</strong></td>
                    <td>${shelf.description}</td>
                    <td>
                        <label class="switch">
                            <input type="checkbox"
                                   class="status-toggle"
                                   data-id="${shelf.identifier}"
                                   ${shelf.status ? 'checked' : ''}>
                            <span class="slider"></span>
                        </label>
                        <span class="status-label ${shelf.status ? 'text-active' : 'text-inactive'}">
                            ${shelf.status ? 'ACTIVE' : 'INACTIVE'}
                        </span>
                    </td>
                    <td>
                        <a class="action-icon" href="${pageContext.request.contextPath}/shelf/get?identifier=${shelf.identifier}">
                            <i class="fa fa-pen-to-square"></i>
                        </a>
                        <a class="action-icon delete-btn"
                           href="${pageContext.request.contextPath}/shelf/delete?identifier=${shelf.identifier}"
                           onclick="return confirm('Are you sure you want to delete this shelf?')">
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
        <a href="${pageContext.request.contextPath}/shelf/add">Add Shelf</a>
    </div>
</div>

<div id="toast"></div>

<script>

    document.querySelectorAll('.status-toggle').forEach(toggle => {
        toggle.addEventListener('change', function() {
            const identifier = this.getAttribute('data-id');
            const isChecked = this.checked;
            const label = this.closest('td').querySelector('.status-label');

            fetch(`${pageContext.request.contextPath}/shelf/toggle?identifier=` + encodeURIComponent(identifier) + `&status=` + isChecked, {
                method: 'POST'
            })
            .then(response => {
                if (response.ok) {
                    if (label) {
                        label.innerText = isChecked ? 'ACTIVE' : 'INACTIVE';
                        label.className = `status-label ${isChecked ? 'text-active' : 'text-inactive'}`;
                    }
                    showToast("Shelf Status Updated Successfully");
                } else {
                    throw new Error("Sync failed");
                }
            })
            .catch(err => {
                console.error("Fetch Error:", err);
                this.checked = !isChecked;
                if (label) {
                    label.innerText = !isChecked ? 'ACTIVE' : 'INACTIVE';
                    label.className = `status-label ${!isChecked ? 'text-active' : 'text-inactive'}`;
                }
                alert("Error: Database sync failed.");
            });
        });
    });

    function showToast(message) {
        const toast = document.getElementById("toast");
        if (toast) {
            toast.innerText = message;
            toast.className = "show";
            setTimeout(() => { toast.className = ""; }, 3000);
        }
    }

    window.onload = function() {
        const msg = "${message}";
        if (msg && msg.trim() !== "") {
            showToast(msg);
        }
    };
</script>

</body>
</html>