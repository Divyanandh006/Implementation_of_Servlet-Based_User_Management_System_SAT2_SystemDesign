package edu.library;
import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.*;import jakarta.servlet.http.*;import java.io.IOException;
@WebServlet("/members")
public class MembersServlet extends Web{
 protected void doGet(HttpServletRequest q,HttpServletResponse p)throws ServletException,IOException{
  if(!auth(q,p))return;
  try{q.setAttribute("items",DB.rows("SELECT * FROM members ORDER BY id DESC"));}catch(Exception e){error(q,e);}
  page(q,p,"members");
 }
 protected void doPost(HttpServletRequest q,HttpServletResponse p)throws ServletException,IOException{
  if(!auth(q,p))return;
  try{
   String action=s(q,"action");
   if(action.equals("delete")) DB.update("DELETE FROM members WHERE id=?",i(q,"id",0));
   else if(action.equals("update")) DB.update("UPDATE members SET full_name=?,email=?,phone=?,status=? WHERE id=?",s(q,"full_name"),s(q,"email"),s(q,"phone"),s(q,"status"),i(q,"id",0));
   else DB.update("INSERT INTO members(full_name,email,phone,status) VALUES(?,?,?,'ACTIVE')",s(q,"full_name"),s(q,"email"),s(q,"phone"));
   msg(q,"Member operation completed.");
  }catch(Exception e){msg(q,"Member operation failed: "+e.getMessage());}
  go(q,p,"/members");
 }
}
