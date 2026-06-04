<%@ page language="java" contentType="text/html; charset=UTF-8"%>
<%@ taglib uri="jakarta.tags.core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
    <title>POS - Home</title>
    <style>
        *, *::before, *::after { box-sizing: border-box; }
        body { margin: 0; font-family: "Segoe UI", Arial, sans-serif; background: #f0f2f5; display: flex; height: 100vh; }

        .sidebar {
            width: 220px; background: #1e3a5f; color: #fff;
            display: flex; flex-direction: column; flex-shrink: 0;
        }
        .sidebar .brand {
            padding: 18px 20px; font-size: 19px; font-weight: 700;
            letter-spacing: 1px; border-bottom: 1px solid rgba(255,255,255,0.1);
            background: #16304f;
        }
        .sidebar .brand span { font-size: 11px; font-weight: 400; display: block; color: rgba(255,255,255,0.5); letter-spacing: 2px; margin-top: 2px; }
        .sidebar nav { flex: 1; overflow-y: auto; padding: 8px 0; }
        .nav-item a {
            display: block; color: rgba(255,255,255,0.78); text-decoration: none;
            padding: 10px 20px; font-size: 14px; transition: background 0.15s, color 0.15s;
            border-left: 3px solid transparent;
        }
        .nav-item a:hover { background: rgba(255,255,255,0.1); color: #fff; border-left-color: #5b9bd5; }
        .sidebar-footer { padding: 12px 20px; font-size: 11px; color: rgba(255,255,255,0.35); border-top: 1px solid rgba(255,255,255,0.08); }

        .main { flex: 1; display: flex; flex-direction: column; overflow: hidden; }
        .top-bar {
            background: #fff; border-bottom: 1px solid #dde1e7;
            padding: 0 28px; height: 56px;
            display: flex; justify-content: space-between; align-items: center;
        }
        .top-bar h3 { margin: 0; font-size: 16px; font-weight: 600; color: #1e3a5f; }
        .logout-btn {
            background: transparent; color: #dc3545; border: 1px solid #dc3545;
            padding: 6px 14px; border-radius: 5px; font-size: 13px; cursor: pointer;
        }
        .logout-btn:hover { background: #fff0f0; }

        /* Profile dropdown */
        .top-right { display: flex; align-items: center; gap: 12px; }
        .profile-wrap { position: relative; }
        .profile-btn {
            display: flex; align-items: center; gap: 8px;
            background: #f0f2f5; border: 1px solid #dde1e7;
            padding: 6px 12px; border-radius: 5px; cursor: pointer;
            font-size: 13px; font-weight: 500; color: #1e3a5f;
            transition: background 0.15s;
        }
        .profile-btn:hover { background: #e4e8ef; }
        .profile-icon {
            width: 28px; height: 28px; border-radius: 50%;
            background: #1e3a5f; color: #fff;
            display: flex; align-items: center; justify-content: center;
            font-size: 13px; font-weight: 600; flex-shrink: 0;
        }
        .dropdown {
            display: none; position: absolute; top: calc(100% + 8px); right: 0;
            background: #fff; border: 1px solid #dde1e7; border-radius: 8px;
            box-shadow: 0 4px 16px rgba(0,0,0,0.1); width: 220px; z-index: 100;
        }
        .dropdown.open { display: block; }
        .dropdown-header {
            padding: 14px 16px 10px; border-bottom: 1px solid #eef0f3;
        }
        .dropdown-header .d-name { font-size: 14px; font-weight: 600; color: #1e3a5f; }
        .dropdown-header .d-username { font-size: 12px; color: #888; margin-top: 2px; }
        .dropdown-row { padding: 8px 16px; font-size: 13px; color: #555; }
        .dropdown-row span { font-weight: 600; color: #333; }

        .content { flex: 1; padding: 28px; overflow-y: auto; }

        .welcome-card {
            background: #fff; border-radius: 8px; border: 1px solid #dde1e7;
            padding: 28px 30px;
        }
        .welcome-card h2 { margin: 0 0 8px; font-size: 20px; color: #1e3a5f; }
        .welcome-card p { margin: 0; color: #666; font-size: 14px; line-height: 1.6; }

        .info-row { display: flex; gap: 16px; margin-top: 20px; }
        .info-box {
            flex: 1; background: #f7f9fc; border: 1px solid #dde1e7;
            border-radius: 6px; padding: 16px 18px;
        }
        .info-box .label { font-size: 11px; font-weight: 600; text-transform: uppercase; letter-spacing: 1px; color: #888; margin-bottom: 6px; }
        .info-box .value { font-size: 16px; font-weight: 600; color: #1e3a5f; }
    </style>
</head>
<body>

<!-- SIDEBAR -->
<div class="sidebar">
    <div class="brand">POS System<span>Point of Sale</span></div>
    <nav>
        <c:if test="${empty nodes}">
            <div style="padding: 12px 20px; font-size: 13px; color: rgba(255,255,255,0.4);">No items found</div>
        </c:if>
        <c:forEach items="${nodes}" var="node">
            <div class="nav-item">
                <a href="${pageContext.request.contextPath}${node.path}">${node.identifier}</a>
            </div>
        </c:forEach>
    </nav>
    <div class="sidebar-footer">© 2025 POS System</div>
</div>

<!-- MAIN -->
<div class="main">
    <div class="top-bar">
        <h3>Dashboard</h3>
        <div class="top-right">

            <!-- Profile Dropdown -->
            <div class="profile-wrap">
                <div class="profile-btn" onclick="toggleDropdown()">
                    <div class="profile-icon">
                        <c:choose>
                            <c:when test="${not empty loggedInUser.name}">${loggedInUser.name.substring(0,1).toUpperCase()}</c:when>
                            <c:otherwise>U</c:otherwise>
                        </c:choose>
                    </div>
                    <c:choose>
                        <c:when test="${not empty loggedInUser.name}">${loggedInUser.name}</c:when>
                        <c:otherwise>${pageContext.request.userPrincipal.name}</c:otherwise>
                    </c:choose>
                    ▾
                </div>

                <div class="dropdown" id="profileDropdown">
                    <div class="dropdown-header">
                        <div class="d-name">
                            <c:choose>
                                <c:when test="${not empty loggedInUser.name}">${loggedInUser.name}</c:when>
                                <c:otherwise>${pageContext.request.userPrincipal.name}</c:otherwise>
                            </c:choose>
                        </div>
                        <div class="d-username">${loggedInUser.username}</div>
                    </div>
                    <div class="dropdown-row">📞 <span>${loggedInUser.phoneNo}</span></div>
                    <div class="dropdown-row">🎭 <span>${loggedInUser.roles}</span></div>
                </div>
            </div>

            <!-- Logout -->
            <a href="${pageContext.request.contextPath}/logout">
                <button class="logout-btn">Logout</button>
            </a>

        </div>
    </div>
    <div class="content">
        <div class="welcome-card">
            <h2>Welcome to POS System</h2>
            <p>Use the left navigation panel to manage Users, Roles, Nodes, or other master data in the system.</p>
            <div class="info-row">
                <div class="info-box"><div class="label">Module</div><div class="value">POS</div></div>
                <div class="info-box"><div class="label">Status</div><div class="value" style="color:#28a745;">Active</div></div>
                <div class="info-box"><div class="label">Mode</div><div class="value">Training</div></div>
            </div>
        </div>
    </div>
</div>

<script>
    function toggleDropdown() {
        document.getElementById('profileDropdown').classList.toggle('open');
    }
    // Close dropdown when clicking outside
    document.addEventListener('click', function(e) {
        var wrap = document.querySelector('.profile-wrap');
        if (!wrap.contains(e.target)) {
            document.getElementById('profileDropdown').classList.remove('open');
        }
    });
</script>
</body>
</html>
