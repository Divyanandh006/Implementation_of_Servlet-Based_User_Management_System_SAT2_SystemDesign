package edu.library;
import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.*;import jakarta.servlet.http.*;import java.io.IOException;
@WebServlet("/admin")
public class AdminServlet extends Web{
 protected void doGet(HttpServletRequest q,HttpServletResponse p)throws ServletException,IOException{if(!auth(q,p))return;page(q,p,"admin");}
 protected void doPost(HttpServletRequest q,HttpServletResponse p)throws ServletException,IOException{
  if(!auth(q,p))return;
  try{
   String u=s(q,"username"),pw=s(q,"password"),role=s(q,"role");
   if(u.isBlank()||pw.length()<8)throw new IllegalArgumentException("Username is required and password must have at least 8 characters.");
   DB.update("INSERT INTO admins(username,password_hash,role) VALUES(?,?,?)",u,DB.hash(pw),role.equals("LIBRARIAN")?"LIBRARIAN":"ADMIN");
   msg(q,"Admin account created.");
  }catch(Exception e){msg(q,"Could not create account: "+e.getMessage());}
  go(q,p,"/admin");
 }
}
