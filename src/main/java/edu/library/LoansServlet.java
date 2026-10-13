package edu.library;
import jakarta.servlet.annotation.WebServlet;import jakarta.servlet.*;import jakarta.servlet.http.*;import java.io.IOException;import java.sql.*;
@WebServlet("/loans")
public class LoansServlet extends Web{
 protected void doGet(HttpServletRequest q,HttpServletResponse p)throws ServletException,IOException{
  if(!auth(q,p))return;
  try{q.setAttribute("items",DB.rows("SELECT l.*,m.full_name,b.title FROM loans l JOIN members m ON m.id=l.member_id JOIN books b ON b.id=l.book_id ORDER BY l.id DESC"));
   q.setAttribute("members",DB.rows("SELECT id,full_name FROM members WHERE status='ACTIVE' ORDER BY full_name"));
   q.setAttribute("books",DB.rows("SELECT id,title,available_copies FROM books WHERE available_copies>0 ORDER BY title"));
  }catch(Exception e){error(q,e);}
  page(q,p,"loans");
 }
 protected void doPost(HttpServletRequest q,HttpServletResponse p)throws ServletException,IOException{
  if(!auth(q,p))return;
  String action=s(q,"action"); int member=i(q,"member_id",0),book=i(q,"book_id",0),id=i(q,"id",0);
  try{
   if(action.equals("checkout")){
    try(Connection c=DB.get()){
     c.setAutoCommit(false);
     try(PreparedStatement b=c.prepareStatement("SELECT available_copies FROM books WHERE id=? FOR UPDATE")){
      b.setInt(1,book);try(ResultSet r=b.executeQuery()){
       if(!r.next()||r.getInt(1)<=0)throw new SQLException("No copies available.");
      }
     }
     try(PreparedStatement p1=c.prepareStatement("INSERT INTO loans(member_id,book_id,checkout_date,due_date,status) VALUES(?,?,CURDATE(),DATE_ADD(CURDATE(),INTERVAL 14 DAY),'BORROWED')")){
      p1.setInt(1,member);p1.setInt(2,book);p1.executeUpdate();
     }
     try(PreparedStatement p2=c.prepareStatement("UPDATE books SET available_copies=available_copies-1 WHERE id=? AND available_copies>0")){p2.setInt(1,book);if(p2.executeUpdate()!=1)throw new SQLException("No copies available.");}
     c.commit();
    }
    msg(q,"Checkout successful. Due date is 14 days from today.");
   }else if(action.equals("return")){
    try(Connection c=DB.get()){
     c.setAutoCommit(false);
     int bookId=-1;Date due=null;
     try(PreparedStatement st=c.prepareStatement("SELECT book_id,due_date FROM loans WHERE id=? AND status='BORROWED' FOR UPDATE")){
      st.setInt(1,id);try(ResultSet r=st.executeQuery()){if(!r.next())throw new SQLException("Active loan not found.");bookId=r.getInt(1);due=r.getDate(2);}
     }
     try(PreparedStatement st=c.prepareStatement("UPDATE loans SET status='RETURNED',return_date=CURDATE() WHERE id=?")){st.setInt(1,id);st.executeUpdate();}
     try(PreparedStatement st=c.prepareStatement("UPDATE books SET available_copies=LEAST(total_copies,available_copies+1) WHERE id=?")){st.setInt(1,bookId);st.executeUpdate();}
     long late=Math.max(0,(System.currentTimeMillis()-due.getTime())/86400000L);
     if(late>0)try(PreparedStatement st=c.prepareStatement("INSERT INTO fines(loan_id,amount,paid) VALUES(?,?,FALSE)")){st.setInt(1,id);st.setDouble(2,late*5.0);st.executeUpdate();}
     c.commit();
    }
    msg(q,"Return processed. Overdue fine: ₹5 per late day, if applicable.");
   }else if(action.equals("renew")){
    int n=DB.update("UPDATE loans SET due_date=DATE_ADD(due_date,INTERVAL 14 DAY) WHERE id=? AND status='BORROWED' AND due_date>=CURDATE()",id);
    msg(q,n==1?"Loan renewed for 14 days.":"Renewal not allowed for this loan.");
   }
  }catch(Exception e){msg(q,"Loan operation failed: "+e.getMessage());}
  go(q,p,"/loans");
 }
}
