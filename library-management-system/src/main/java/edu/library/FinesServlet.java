package edu.library;
import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.*;import jakarta.servlet.http.*;import java.io.IOException;
@WebServlet("/fines")
public class FinesServlet extends Web{
 protected void doGet(HttpServletRequest q,HttpServletResponse p)throws ServletException,IOException{
  if(!auth(q,p))return;
  try{q.setAttribute("items",DB.rows("SELECT f.*,l.member_id,m.full_name,b.title FROM fines f JOIN loans l ON l.id=f.loan_id JOIN members m ON m.id=l.member_id JOIN books b ON b.id=l.book_id ORDER BY f.id DESC"));}catch(Exception e){error(q,e);}
  page(q,p,"fines");
 }
 protected void doPost(HttpServletRequest q,HttpServletResponse p)throws ServletException,IOException{
  if(!auth(q,p))return;
  try{DB.update("UPDATE fines SET paid=TRUE,paid_at=NOW() WHERE id=? AND paid=FALSE",i(q,"id",0));msg(q,"Fine marked as paid.");}catch(Exception e){msg(q,"Fine update failed: "+e.getMessage());}
  go(q,p,"/fines");
 }
}
