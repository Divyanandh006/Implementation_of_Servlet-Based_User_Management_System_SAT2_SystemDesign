package edu.library;
import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.*;import jakarta.servlet.http.*;import java.io.IOException;
@WebServlet("/books")
public class BooksServlet extends Web{
 protected void doGet(HttpServletRequest q,HttpServletResponse p)throws ServletException,IOException{
  if(!auth(q,p))return;
  try{q.setAttribute("items",DB.rows("SELECT * FROM books ORDER BY id DESC"));}catch(Exception e){error(q,e);}
  page(q,p,"books");
 }
 protected void doPost(HttpServletRequest q,HttpServletResponse p)throws ServletException,IOException{
  if(!auth(q,p))return;
  try{
   String a=s(q,"action");
   if(a.equals("delete")) DB.update("DELETE FROM books WHERE id=?",i(q,"id",0));
   else if(a.equals("update")) DB.update("UPDATE books SET title=?,author=?,isbn=?,total_copies=?,available_copies=?,digital_url=? WHERE id=?",s(q,"title"),s(q,"author"),s(q,"isbn"),i(q,"total_copies",1),i(q,"available_copies",1),s(q,"digital_url"),i(q,"id",0));
   else DB.update("INSERT INTO books(title,author,isbn,total_copies,available_copies,digital_url) VALUES(?,?,?,?,?,?)",s(q,"title"),s(q,"author"),s(q,"isbn"),i(q,"total_copies",1),i(q,"available_copies",1),s(q,"digital_url"));
   msg(q,"Book operation completed.");
  }catch(Exception e){msg(q,"Book operation failed: "+e.getMessage());}
  go(q,p,"/books");
 }
}
