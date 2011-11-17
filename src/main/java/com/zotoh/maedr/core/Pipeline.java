/*??
 * COPYRIGHT (C) 2010-2011 CHERIMOIA LLC. ALL RIGHTS RESERVED.
 *
 * THIS IS FREE SOFTWARE; YOU CAN REDISTRIBUTE IT AND/OR
 * MODIFY IT UNDER THE TERMS OF THE APACHE LICENSE, 
 * VERSION 2.0 (THE "LICENSE").
 *
 * THIS LIBRARY IS DISTRIBUTED IN THE HOPE THAT IT WILL BE USEFUL,
 * BUT WITHOUT ANY WARRANTY; WITHOUT EVEN THE IMPLIED WARRANTY OF
 * MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE.
 *   
 * SEE THE LICENSE FOR THE SPECIFIC LANGUAGE GOVERNING PERMISSIONS 
 * AND LIMITATIONS UNDER THE LICENSE.
 *
 * You should have received a copy of the Apache License
 * along with this distribution; if not, you may obtain a copy of the 
 * License at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 ??*/
 
package com.zotoh.maedr.core;


import static com.zotoh.core.util.LoggerFactory.getLogger;

import java.sql.Timestamp;
import java.util.Properties;

import org.json.JSONObject;

import com.zotoh.core.db.DBRow;
import com.zotoh.core.db.DELETEStmt;
import com.zotoh.core.db.JConnection;
import com.zotoh.core.db.JDBC;
import com.zotoh.core.db.SELECTStmt;
import com.zotoh.core.io.StreamData;
import com.zotoh.core.util.CoreUte;
import com.zotoh.core.util.JSONUte;
import com.zotoh.core.util.Logger;
import com.zotoh.core.util.Tuple;

/**
 * @author kenl
 *
 */
public abstract class Pipeline implements Vars {

    private Logger ilog() {  return _log=getLogger(Pipeline.class);    }
    private transient Logger _log= ilog();    
    public Logger tlog() {  return _log==null ? ilog() : _log;    }    
    private WState _state;
    
    /**
     * 
     */
    protected Pipeline() 
    {}
    
    /**
     * @return
     */
    public abstract Properties getEngineProperties();

    /**
     * @return
     */
    public abstract long getPID();
    
    /**
     * @return
     */
    public abstract AppEngine<?,?> getEngine();
    
    /**
     * @return
     */
    public WState getState() { return _state; }
    
    /**
     * @return
     */
    public abstract Job getJob();
    
	/**
	 * @return
	 */
	public abstract boolean isActive();

    // -------------------- state management
	
	/**
	 * @return
	 */
	protected void createState() {
	    _state=new WState(this);
	}
	
    
    /**
     * @param prevState
     */
    public void reconcileState(JSONObject prevState) throws Exception {
        if (prevState==null) { return; }
        WState cur=getState();
        if (cur==null) { 
        	createState(); 
            cur=getState();
        }
        cur.setRoot(prevState);
    }
	
    /**
     * @param col
     * @param key
     * @throws Exception
     */
    public void retrievePreviousState(ColPicker col, Object key) throws Exception    {
        JSONObject old= retrieveStateViaXXX(col.toString(), key);
        if (old != null) {
            reconcileState(old);
        }
    }
    
    /**
     * @param col
     * @param key
     * @return
     * @throws Exception
     */
    protected JSONObject retrieveStateViaXXX(String col, Object key) throws Exception     {
        JDBC j= getEngine().newJdbc();
        JSONObject rc= null;        
        DBRow row;
        Object obj= null;
        byte[] bits=null;
        
        row=maybeGetStateRow(j, col, key);
        if (row != null) {
            obj= row.get(COL_BIN);                
        }
        
        if (obj instanceof StreamData) {
            StreamData dd= (StreamData) obj;
            bits=dd.getBytes();
        }
        else
        if (obj instanceof byte[]) {
            bits= (byte[]) obj;
        }
        
        if (bits != null && bits.length > 0) {
            rc= JSONUte.read( CoreUte.asString(bits));
        }
        
        return rc;
    }

    /**
     * @throws Exception
     */
    public void persistState() throws Exception    {
        JDBC j= getEngine().newJdbc();
        JConnection tx= j.beginTransaction();
        
        if ( getState().hasKey())
        try  {
            j.deleteRows(tx, delete());
            j.insertOneRow(tx, insert());            
            j.commitTransaction(tx) ;
        }
        catch (Exception e) {
            tlog().error("", e);
            j.cancelTransaction(tx) ;
        }
        finally {
            j.closeTransaction(tx) ;
        }
        
    }

    /**
     * @throws Exception
     */
    public void removeState() throws Exception    {
        getEngine().newJdbc().deleteRows(delete());
    }
    
    private SELECTStmt query(String col, Object key)     {
        String sql= "select * from " + DB_STATE_TBL + " where " + col + "=?";
        if (key==null) { return null; }
        return new SELECTStmt(sql , new Tuple( key ));
    }
    
    private DELETEStmt delete()  {
        String sql="delete from " + DB_STATE_TBL + " where " + COL_KEYID + " =?";
        Object key=this.getState().getKey();
        if (key==null) { return null; }
        return new DELETEStmt(sql , new Tuple(key ));
    }
    
    private DBRow insert() throws Exception    {
        //Timestamp ts= new Timestamp(new Date().getTime()); 
        return insertWithExpiry(null);
    }
    
    private DBRow insertWithExpiry(Timestamp expiry) throws Exception    {
        DBRow row= new DBRow(DB_STATE_TBL);
        WState s= getState();
        if (s==null) { return null; }
        Object key= s.getKey();
        if (key==null) { return null; }
        byte[] bits= CoreUte.asBytes(JSONUte.asString(s.getRoot()));
        row.add(COL_TRACKID, s.getTracker());
        row.add(COL_KEYID, key);
        row.add(COL_BIN, bits);
        row.add(COL_EXPIRY, expiry);
        return row;
    }
    
    /**
     * @param jdbc
     * @param col
     * @param key
     * @return
     * @throws Exception
     */
    protected DBRow maybeGetStateRow(JDBC jdbc, String col, Object key) throws Exception    {
        return jdbc.fetchOneRow(  query(col, key) );        
    }
    
    
}
