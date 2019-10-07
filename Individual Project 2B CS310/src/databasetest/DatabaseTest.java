package databasetest;

import java.sql.*;
import java.util.LinkedHashMap;
import org.json.simple.*;


public class DatabaseTest {

      public static void main(String[] args) {
          
        DatabaseTest test = new DatabaseTest();
        test.getJSONData();
    }


    public JSONArray getJSONData(){  

        JSONArray results = null;
        Connection conn = null;
        PreparedStatement pstSelect = null, pstUpdate = null;
        ResultSet resultset = null;
        ResultSetMetaData metaData = null;

        String query, value;
        String[] headers;

        JSONArray record = new JSONArray();
      
        boolean hasResults;       
        int resultCount, columnCount;

        try {           

            /* Indentify the Server */
            
            String server = ("jdbc:mysql://localhost/p2_test");           
            String username = "root";            
            String password = "CS488";
            
            /* Load the MySQL JDBC Driver */
            
            Class.forName("com.mysql.jdbc.Driver").newInstance();
           
            /* Open Connection */

            conn = DriverManager.getConnection(server, username, password);
            
            if (conn.isValid(0)){
                
                /* Connection Open! */
                
                System.out.println("Connected Successfully!");
              
                // Prepare Select Query
               
                query = "SELECT * FROM people";

                pstSelect = conn.prepareStatement(query);
                
                hasResults = pstSelect.execute();
                
                /* Get Results */
                
                while (hasResults || pstSelect.getUpdateCount() != -1){
                
                    if (hasResults){
                        
                        resultset = pstSelect.getResultSet();
                        metaData = resultset.getMetaData();
                        columnCount = metaData.getColumnCount();

                        headers = new String[columnCount - 1];

                        
                        for(int i = 0; i < headers.length; i++){
                            headers[i] = metaData.getColumnLabel(i + 2);

                        }

                        LinkedHashMap data = new LinkedHashMap();
                        
                       
                        while(resultset.next()){

                            data = new LinkedHashMap();
                          
                            for(int i = 0; i < headers.length; i++){
                                value = resultset.getString(i + 2);

                                if(resultset.wasNull()){
                                    data.put(headers[i], "");

                                } else {
                                    data.put(headers[i], value);
                                }
                            }
                            record.add(data);
                        }
                        
                    } else {                       
                        resultCount = pstSelect.getUpdateCount();
                        
                        if (resultCount == -1){
                            break;
                        }
                    }
                   
                    /* Check for More Data */
                   
                    hasResults = pstSelect.getMoreResults();
                }                
                results = record;           
            }
           
            /* Close Database Connection */
            
            System.out.println();          
            conn.close();           

        } catch (Exception e){
            System.err.println(e.toString());
        }
        
        /* Close Other Database Objects */
        
        finally {
            
            if (resultset != null) { try { resultset.close(); resultset = null; } catch (Exception e) {} }
            
            if (pstSelect != null) { try { pstSelect.close(); pstSelect = null; } catch (Exception e) {} }
            
            if (pstUpdate != null) { try { pstUpdate.close(); pstUpdate = null; } catch (Exception e) {} }
        }
        
        return results;
    } 
}