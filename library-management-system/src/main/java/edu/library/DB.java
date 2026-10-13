package edu.library;

import java.sql.*;
import java.util.*;
import org.mindrot.jbcrypt.BCrypt;

public final class DB {
    private DB() {}
    private static String env(String key, String fallback) {
        String v = System.getenv(key); return v == null || v.isBlank() ? fallback : v;
    }
    static Connection get() throws SQLException {
        String url = env("LIBRARY_DB_URL", "jdbc:mysql://localhost:3306/library_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true");
        String user = env("LIBRARY_DB_USER", "root");
        String pass = env("LIBRARY_DB_PASSWORD", "");
        return DriverManager.getConnection(url, user, pass);
    }
    static List<Map<String,Object>> rows(String sql, Object... args) throws SQLException {
        List<Map<String,Object>> out = new ArrayList<>();
        try (Connection c=get(); PreparedStatement p=c.prepareStatement(sql)) {
            bind(p,args); try(ResultSet r=p.executeQuery()) {
                ResultSetMetaData md=r.getMetaData(); int n=md.getColumnCount();
                while(r.next()){ Map<String,Object> m=new LinkedHashMap<>(); for(int i=1;i<=n;i++)m.put(md.getColumnLabel(i),r.getObject(i)); out.add(m); }
            }
        } return out;
    }
    static int update(String sql, Object... args) throws SQLException {
        try(Connection c=get(); PreparedStatement p=c.prepareStatement(sql)){bind(p,args);return p.executeUpdate();}
    }
    static long insert(String sql, Object... args) throws SQLException {
        try(Connection c=get(); PreparedStatement p=c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)){
            bind(p,args); p.executeUpdate(); try(ResultSet r=p.getGeneratedKeys()){if(r.next())return r.getLong(1);} return -1;
        }
    }
    static Object scalar(String sql, Object... args) throws SQLException {
        try(Connection c=get(); PreparedStatement p=c.prepareStatement(sql)){bind(p,args);try(ResultSet r=p.executeQuery()){return r.next()?r.getObject(1):null;}}
    }
    private static void bind(PreparedStatement p,Object[] args)throws SQLException{for(int i=0;i<args.length;i++)p.setObject(i+1,args[i]);}
    public static String hash(String raw){return BCrypt.hashpw(raw,BCrypt.gensalt(12));}
    public static boolean check(String raw,String hashed){try{return BCrypt.checkpw(raw,hashed);}catch(Exception e){return false;}}
}
