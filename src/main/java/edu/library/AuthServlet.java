package edu.library;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import jakarta.servlet.*;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns={"/login","/logout"})
public class AuthServlet extends Web {
    @Override protected void doGet(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{
        if(req.getServletPath().equals("/logout")){req.getSession().invalidate();res.sendRedirect(req.getContextPath()+"/login");return;}
        page(req,res,"login");
    }
    @Override protected void doPost(HttpServletRequest req,HttpServletResponse res)throws ServletException,IOException{
        String username=s(req,"username"), password=s(req,"password");
        try{
            var rows=DB.rows("SELECT id,username,password_hash,role FROM admins WHERE username=?",username);
            if(!rows.isEmpty() && DB.check(password,String.valueOf(rows.get(0).get("password_hash")))){
                HttpSession session=req.getSession();session.setAttribute("admin",rows.get(0).get("username"));session.setAttribute("role",rows.get(0).get("role"));
                res.sendRedirect(req.getContextPath()+"/dashboard");
            }else{req.setAttribute("error","Invalid username or password.");page(req,res,"login");}
        }catch(Exception e){req.setAttribute("error","Cannot connect to database. Run database.sql and verify DB settings.");page(req,res,"login");}
    }
}
