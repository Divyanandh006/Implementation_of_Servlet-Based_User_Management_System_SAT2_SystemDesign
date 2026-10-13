package edu.library;
import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.*;import jakarta.servlet.http.*;import java.io.IOException;
@WebServlet("/reservations")
public class ReservationsServlet extends Web{
 protected void doGet(HttpServletRequest q,HttpServletResponse p)throws ServletException,IOException{
  if(!auth(q,p))return;
  try{q.setAttribute("items",DB.rows("SELECT r.*,m.full_name,b.title FROM reservations r JOIN members m ON m.id=r.member_id JOIN books b ON b.id=r.book_id ORDER BY r.created_at,r.id"));
   q.setAttribute("members",DB.rows("SELECT id,full_name FROM members WHERE status='ACTIVE' ORDER BY full_name"));
   q.setAttribute("books",DB.rows("SELECT id,title FROM books ORDER BY title"));}catch(Exception e){error(q,e);}
  page(q,p,"reservations");
 }
 protected void doPost(HttpServletRequest q,HttpServletResponse p)throws ServletException,IOException{
  if(!auth(q,p))return;
  try{if(s(q,"action").equals("cancel"))DB.update("UPDATE reservations SET status='CANCELLED' WHERE id=? AND status='WAITING'",i(q,"id",0));
  else DB.update("INSERT INTO reservations(member_id,book_id,status) VALUES(?,?,'WAITING')",i(q,"member_id",0),i(q,"book_id",0));
  msg(q,"Reservation operation completed.");}catch(Exception e){msg(q,"Reservation failed: "+e.getMessage());}
  go(q,p,"/reservations");
 }
}
