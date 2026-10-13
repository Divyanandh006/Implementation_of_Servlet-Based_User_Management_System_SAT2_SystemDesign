package edu.library;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.*;import jakarta.servlet.http.*;import java.io.IOException;
@WebServlet("/dashboard")
public class DashboardServlet extends Web{
 protected void doGet(HttpServletRequest q,HttpServletResponse p)throws ServletException,IOException{
  if(!auth(q,p))return;
  try{
   q.setAttribute("members",DB.scalar("SELECT COUNT(*) FROM members"));
   q.setAttribute("books",DB.scalar("SELECT COUNT(*) FROM books"));
   q.setAttribute("available",DB.scalar("SELECT COALESCE(SUM(available_copies),0) FROM books"));
   q.setAttribute("loans",DB.scalar("SELECT COUNT(*) FROM loans WHERE status='BORROWED'"));
   q.setAttribute("reservations",DB.scalar("SELECT COUNT(*) FROM reservations WHERE status='WAITING'"));
   q.setAttribute("fines",DB.scalar("SELECT COALESCE(SUM(amount),0) FROM fines WHERE paid=FALSE"));
  }catch(Exception e){error(q,e);}
  page(q,p,"dashboard");
 }
}
