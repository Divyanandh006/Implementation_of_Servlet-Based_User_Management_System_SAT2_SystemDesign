<%@ page contentType="text/html;charset=UTF-8" %><%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ include file="top.jspf" %>
<h1>Dashboard</h1><p class="muted">Overview of your library network.</p>
<div class="stats">
<div class="stat"><span>Members</span><b>${members}</b></div><div class="stat"><span>Book titles</span><b>${books}</b></div><div class="stat"><span>Available copies</span><b>${available}</b></div><div class="stat"><span>Active loans</span><b>${loans}</b></div><div class="stat"><span>Waiting reservations</span><b>${reservations}</b></div><div class="stat"><span>Unpaid fines (₹)</span><b>${fines}</b></div>
</div>
<div class="card"><h2>Quick guide</h2><p>Add members and books first. Use Loans to check out, renew or return books. A return after the due date automatically creates a fine of ₹5 per late day.</p><p>Digital licences store a link and concurrent-access limit; this demo does not host or sell copyrighted e-books.</p></div>
<%@ include file="bottom.jspf" %>
