<%@ page contentType="text/html;charset=UTF-8" %>
<!doctype html><html><head><title>Library Login</title><link rel="stylesheet" href="${pageContext.request.contextPath}/assets/style.css"></head>
<body class="login-bg"><main class="login card"><h1>Library Portal</h1><p>Library Management & Digital Lending</p>
<% if(request.getAttribute("error")!=null){ %><div class="alert"><%=request.getAttribute("error")%></div><% } %>
<form method="post" action="${pageContext.request.contextPath}/login"><label>Username</label><input name="username" required autofocus><label>Password</label><input type="password" name="password" required><button>Sign in</button></form>
<p class="muted">First run? Follow README to create the initial admin account.</p></main></body></html>
