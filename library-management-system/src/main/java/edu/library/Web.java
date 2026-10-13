package edu.library;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;

public abstract class Web extends HttpServlet {
    protected void page(HttpServletRequest req,HttpServletResponse res,String jsp)throws ServletException,IOException{
        req.getRequestDispatcher("/WEB-INF/views/"+jsp+".jsp").forward(req,res);
    }
    protected void msg(HttpServletRequest req,String text){req.getSession().setAttribute("flash",text);}
    protected boolean auth(HttpServletRequest req,HttpServletResponse res)throws IOException{
        if(req.getSession().getAttribute("admin")==null){res.sendRedirect(req.getContextPath()+"/login");return false;}return true;
    }
    protected String s(HttpServletRequest r,String k){String v=r.getParameter(k);return v==null?"":v.trim();}
    protected int i(HttpServletRequest r,String k,int fallback){try{return Integer.parseInt(s(r,k));}catch(Exception e){return fallback;}}
    protected void go(HttpServletRequest r,HttpServletResponse p,String path)throws IOException{p.sendRedirect(r.getContextPath()+path);}
    protected void error(HttpServletRequest req,Exception e){req.setAttribute("error","Operation failed. Check required fields and database configuration. ("+e.getMessage()+")");}
}
