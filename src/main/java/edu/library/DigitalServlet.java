package edu.library;
import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.*;import jakarta.servlet.http.*;import java.io.IOException;
@WebServlet("/digital")
public class DigitalServlet extends Web{
 protected void doGet(HttpServletRequest q,HttpServletResponse p)throws ServletException,IOException{
  if(!auth(q,p))return;
  try{q.setAttribute("items",DB.rows("SELECT * FROM digital_licences ORDER BY id DESC"));q.setAttribute("books",DB.rows("SELECT id,title FROM books ORDER BY title"));}catch(Exception e){error(q,e);}
  page(q,p,"digital");
 }
 protected void doPost(HttpServletRequest q,HttpServletResponse p)throws ServletException,IOException{
  if(!auth(q,p))return;
  try{
   String a=s(q,"action");
   if(a.equals("delete"))DB.update("DELETE FROM digital_licences WHERE id=?",i(q,"id",0));
   else if(a.equals("update"))DB.update("UPDATE digital_licences SET book_id=?,format=?,digital_url=?,concurrent_limit=?,active_count=? WHERE id=?",i(q,"book_id",0),s(q,"format"),s(q,"digital_url"),i(q,"concurrent_limit",1),i(q,"active_count",0),i(q,"id",0));
   else DB.update("INSERT INTO digital_licences(book_id,format,digital_url,concurrent_limit,active_count) VALUES(?,?,?,?,0)",i(q,"book_id",0),s(q,"format"),s(q,"digital_url"),i(q,"concurrent_limit",1));
   msg(q,"Digital licence operation completed.");
  }catch(Exception e){msg(q,"Digital licence operation failed: "+e.getMessage());}
  go(q,p,"/digital");
 }
}
