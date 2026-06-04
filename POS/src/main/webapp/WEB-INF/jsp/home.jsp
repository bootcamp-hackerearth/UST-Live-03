<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

<!DOCTYPE html>
<html lang="en">

<head>

<meta charset="UTF-8">

<meta name="viewport"
      content="width=device-width, initial-scale=1.0">

<title>Dashboard</title>

<link href="https://fonts.googleapis.com/css2?family=Poppins:wght@400;500;600;700&display=swap"
      rel="stylesheet">

<style>

*{
    margin:0;
    padding:0;
    box-sizing:border-box;
}

body{

    font-family:'Poppins', sans-serif;

    background:#ffffff;

    overflow-x:hidden;

    color:#111;
}
.layout{

    display:flex;

    min-height:100vh;
}

.sidebar{

    width:270px;

    background:#111;

    color:#fff;

    display:flex;

    flex-direction:column;

    transition:0.3s ease;

    min-height:100vh;

}

.sidebar.hide{

    margin-left:-270px;
}

.sidebar-header{

    padding:25px 20px;

    border-bottom:1px solid rgba(255,255,255,0.08);

    text-align:center;
}

.app-name{

    font-size:24px;

    font-weight:700;

    letter-spacing:1px;

    color:#fff;
}

.menu{

    flex:1;

    padding:25px 18px;

    overflow-y:auto;
}

.menu a{

    display:flex;

    align-items:center;

    padding:14px 16px;

    margin-bottom:14px;

    text-decoration:none;

    color:#ddd;

    background:#1f1f1f;

    border-radius:12px;

    transition:0.3s ease;

    font-size:15px;

    font-weight:500;
}

.menu a:hover{

    background:#fff;

    color:#000;

    transform:translateX(5px);
}

.menu a.active{

    background:#fff;

    color:#000;

    font-weight:600;
}

.logout-container{

    padding:20px;
}

.logout-btn{

    width:100%;

    padding:13px;

    border:none;

    border-radius:12px;

    background:#fff;

    color:#000;

    font-size:15px;

    font-weight:600;

    cursor:pointer;

    transition:0.3s ease;
}

.logout-btn:hover{

    background:#dcdcdc;
}

.content{

    flex:1;

    display:flex;

    flex-direction:column;

    background:#ffffff;
}

.topbar{

    height:75px;

    background:#ffffff;

    border-bottom:1px solid #e5e5e5;

    display:flex;

    justify-content:space-between;

    align-items:center;

    padding:0 25px;

    box-shadow:0 2px 10px rgba(0,0,0,0.05);
}

.toggle-btn{

    width:45px;

    height:45px;

    border:none;

    border-radius:10px;

    background:#111;

    color:#fff;

    font-size:20px;

    cursor:pointer;

    transition:0.3s ease;
}

.toggle-btn:hover{

    background:#333;
}

.user-name{

    font-size:15px;

    color:#666;
}

.user-name span{

    color:#111;

    font-weight:600;
}

.content-body{

    flex:1;

    padding:45px;

    background:#ffffff;
}

.welcome-card{

    background:#ffffff;

    border-radius:20px;

    padding:40px;

    border:1px solid #e5e5e5;

    box-shadow:0 8px 25px rgba(0,0,0,0.06);
}

.welcome-card h1{

    font-size:38px;

    margin-bottom:15px;

    color:#111;
}

.welcome-card p{

    color:#555;

    font-size:16px;

    line-height:1.7;
}

@media(max-width:768px){

    .sidebar{

        position:fixed;

        z-index:1000;

        height:100%;
    }

    .content{

        width:100%;
    }

    .content-body{

        padding:25px;
    }

    .welcome-card{

        padding:28px;
    }

    .welcome-card h1{

        font-size:28px;
    }
}

</style>

<script>

function toggleSidebar(){

    document.getElementById("sidebar")
            .classList.toggle("hide");
}

function setActive(el){

    const links=document.querySelectorAll('.menu a');

    links.forEach(link=>{

        link.classList.remove('active');
    });

    el.classList.add('active');
}

</script>

</head>

<body>

<div class="layout">

    <aside class="sidebar"
           id="sidebar">

        <div class="sidebar-header">

            <span class="app-name">

                Dashboard

            </span>

        </div>

        <nav class="menu">

            <c:choose>

                <c:when test="${empty nodes}">

                    <p style="color:#999;text-align:center;">

                        No Modules Available

                    </p>

                </c:when>

                <c:otherwise>

                    <c:forEach var="node"
                               items="${nodes}">

                        <a href="${pageContext.request.contextPath}${node.path}"
                           onclick="setActive(this)">

                            ${node.identifier}

                        </a>

                    </c:forEach>

                </c:otherwise>

            </c:choose>

        </nav>

        <div class="logout-container">

            <form action="${pageContext.request.contextPath}/logout"
                  method="post">

                <button type="submit"
                        class="logout-btn">

                    Logout

                </button>

            </form>

        </div>

    </aside>

    <div class="content">

        <div class="topbar">

            <button class="toggle-btn"
                    onclick="toggleSidebar()">

                ☰

            </button>

            <div class="user-name">

                Logged in as:

                <span>

                    <c:choose>

                        <c:when test="${not empty pageContext.request.userPrincipal}">

                            ${pageContext.request.userPrincipal.name}

                        </c:when>

                        <c:otherwise>

                            ${sessionScope.username}

                        </c:otherwise>

                    </c:choose>

                </span>

            </div>

        </div>

        <div class="content-body">

            <div class="welcome-card">

                <h1>

                    Welcome

                </h1>

                <p>

                    Select a module from the sidebar to continue.
                    This dashboard provides access to all available
                    modules and management features.

                </p>

            </div>

        </div>

    </div>

</div>

</body>

</html>