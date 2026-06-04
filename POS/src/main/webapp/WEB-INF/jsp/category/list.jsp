<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <title>Category Management</title>

    <!-- Bootstrap -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Font -->
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600&display=swap" rel="stylesheet">

    <!-- CSRF -->
    <meta name="_csrf" content="${_csrf.token}">

    <style>
        :root {
            --primary:#2563eb;
            --bg:#f8fafc;
            --glass:rgba(255,255,255,.75);
            --text:#0f172a;
            --muted:#64748b;
            --border:#e2e8f0;
            --danger:#dc2626;
            --success:#16a34a;
            --radius:16px;
            --shadow:0 20px 40px rgba(2,6,23,.08);
        }

        * { box-sizing:border-box; font-family:'Inter',sans-serif; }

        body {
            background:var(--bg);
            min-height:100vh;
            padding:40px 20px;
            color:var(--text);
        }

        .back-arrow {
                    position: fixed;
                    top: 20px;
                    left: 20px;
                    width: 42px;
                    height: 42px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    border-radius: 50%;
                    background: var(--glass);
                    backdrop-filter: blur(10px);
                    border: 1px solid var(--border);
                    color: var(--text);
                    text-decoration: none;
                    font-size: 18px;
                    box-shadow: var(--shadow);
                    transition: all 0.25s ease;
                }

                .back-arrow:hover {
                    background: #eef2ff;
                    color: var(--primary);
                    transform: translateX(-2px);
                }

        .container-box{
            max-width:1100px;
            margin:60px auto 0;
        }

        .card{
            background:var(--glass);
            backdrop-filter:blur(16px);
            border-radius:var(--radius);
            border:1px solid var(--border);
            box-shadow:var(--shadow);
            overflow:hidden;
        }

        .card-header{
            padding:20px 24px;
            display:flex;
            justify-content:space-between;
            align-items:center;
            font-weight:600;
            font-size:18px;
        }

        .add-btn{
            background:var(--primary);
            color:white;
            text-decoration:none;
            padding:8px 14px;
            border-radius:10px;
            font-size:13px;
            font-weight:600;
        }

        table{ width:100%; border-collapse:collapse; }

        th,td{
            padding:14px 16px;
            border-bottom:1px solid var(--border);
            font-size:13px;
        }

        th{
            font-size:11px;
            text-transform:uppercase;
            color:var(--muted);
            background:rgba(248,250,252,.9);
        }

        tr:hover{ background:rgba(241,245,249,.6); }

        /* Toggle */
        .toggle-switch{
            width:46px; height:24px;
            border-radius:999px;
            background:#cbd5f5;
            position:relative;
            cursor:pointer;
        }

        .toggle-switch::after{
            content:"";
            position:absolute;
            top:3px; left:4px;
            width:18px; height:18px;
            background:white;
            border-radius:50%;
            box-shadow:0 2px 6px rgba(0,0,0,.15);
            transition:.25s;
        }

        .toggle-switch.active{ background:var(--success); }

        .toggle-switch.active::after{
            transform:translateX(20px);
        }

        .actions{ display:flex; gap:8px; }

        .btn-action{
            padding:6px 12px;
            border-radius:8px;
            font-size:12px;
            font-weight:600;
            text-decoration:none;
        }

        .btn-edit{ background:#eef2ff; color:var(--primary); }
        .btn-delete{ background:#fee2e2; color:var(--danger); }

        .empty-state{
            padding:40px;
            text-align:center;
            color:var(--muted);
        }
    </style>
</head>

<body>

<a href="${pageContext.request.contextPath}/" class="back-arrow">←</a>

<div class="container-box">
    <div class="card">

        <div class="card-header">
            <span>Category Management</span>
            <a class="add-btn" href="${pageContext.request.contextPath}/category/add">
                + Add Category
            </a>
        </div>

        <c:if test="${empty categorys}">
            <div class="empty-state">No categories found</div>
        </c:if>

        <c:if test="${not empty categorys}">
            <table>
                <thead>
                <tr>
                    <th>Category</th>
                    <th>Super Category</th>
                    <th>Status</th>
                    <th style="width:180px;">Actions</th>
                </tr>
                </thead>

                <tbody>
                <c:forEach var="cat" items="${categorys}">
                    <tr>
                        <td><strong>${cat.identifier}</strong></td>
                        <td class="text-muted">${cat.superCategory}</td>

                        <td>
                            <c:choose>
                                <c:when test="${not empty cat.superCategory}">
                                    <div class="toggle-switch ${cat.status ? 'active' : ''}"
                                         onclick="toggleCategoryStatus('${cat.identifier}', this)">
                                    </div>
                                </c:when>
                                <c:otherwise>
                                    <span class="text-muted">—</span>
                                </c:otherwise>
                            </c:choose>
                        </td>

                        <td>
                            <div class="actions">
                                <a class="btn-action btn-edit"
                                   href="${pageContext.request.contextPath}/category/get?identifier=${cat.identifier}">
                                    Edit
                                </a>
                                <a class="btn-action btn-delete"
                                   href="${pageContext.request.contextPath}/category/delete?identifier=${cat.identifier}"
                                   onclick="return confirm('Delete this category?')">
                                    Delete
                                </a>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </c:if>

    </div>
</div>

<script>
    function toggleCategoryStatus(identifier, el) {

        el.classList.toggle("active");
        const token = document.querySelector('meta[name="_csrf"]').content;

        fetch('${pageContext.request.contextPath}/category/toggle-status?identifier=' + identifier, {
            method: 'POST',
            headers: {
                'X-CSRF-TOKEN': token
            }
        }).catch(() => {
            el.classList.toggle("active");
        });
    }
</script>

</body>
</html>