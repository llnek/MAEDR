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
 

package com.zotoh.maedr.device;


import static com.zotoh.core.util.CoreUte.getResourceStr;
import static com.zotoh.core.util.CoreUte.isNilArray;
import static com.zotoh.core.util.CoreUte.niceFPath;
import static com.zotoh.core.util.CoreUte.tstEStrArg;
import static com.zotoh.core.util.LangUte.ST;
import static com.zotoh.core.util.StrUte.isEmpty;
import static com.zotoh.core.util.StrUte.trim;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.io.filefilter.RegexFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.json.JSONObject;

import com.zotoh.core.io.CmdLineMandatory;
import com.zotoh.core.io.CmdLineQuestion;
import com.zotoh.core.io.CmdLineSequence;
import com.zotoh.core.util.FileUte;

/**
 * The FilePicker device inspects  a directory for files periodically.  When it detects
 * a file is present, an event will be generated.
 * 
 * The set of properties:
 * 
 * <b>rootdir</b>
 * The full path of the directory to inspect.
 * <b>fmask</b>
 * Optional file mask to selectively look for certain files in regular expression format.
 * <b>automove</b>
 * If set to boolean value <i>true</i>, the file will be moved out of the directory to
 * avoid repeated scan.  If false (by default), it is up to the application to move or
 * delete the file.
 * <b>destdir</b>
 * The full path of a target directory to move files to, effective only iff automove is true.
 * 
 * @see com.zotoh.maedr.device.RepeatingTimer
 * 
 * @author kenl
 * 
 */
public class FilePicker extends ThreadedTimer {

    private final Set<File> _dirs= ST();
    private File _folder, _destMove;
    private FilenameFilter _mask;
    
    /**
     * @param mgr
     */
    public FilePicker(DeviceManager<?,?> mgr)     {
        super(mgr);
    }

    /**
     * @return
     */
    public File getDestDir() { return _destMove; }
    
    /**
     * @return
     */
    public File getSrcDir() { return _folder; }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.RepeatingTimer#inizWithProperties(org.json.JSONObject)
     */
    @Override
    protected void inizWithProperties(JSONObject deviceProperties)
            throws Exception     {
        super.inizWithProperties(deviceProperties) ;
        
        boolean move= deviceProperties.optBoolean("automove") ;
        String mask= trim(deviceProperties.optString("fmask") );
        String root= trim(deviceProperties.optString("rootdir") );
        String dest= trim(deviceProperties.optString("destdir") );
        
        tstEStrArg("file-root-folder", root) ;
        
        if (isEnabled()) {
            
            _folder= new File(root);
            _dirs.add( testDir(_folder)) ;
        
            if (move) {
                tstEStrArg("file-automove-folder", dest) ;
                _destMove= new File(dest);
                _destMove= testDir(_destMove);
            }
        }
        
        _mask=isEmpty(mask) ? TrueFileFilter.TRUE : new RegexFileFilter(mask);        
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.RepeatingTimer#supportsConfigMenu()
     */
    public boolean supportsConfigMenu() { return true; }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.ThreadedTimer#preLoop()
     */
    protected void preLoop() throws Exception {    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.ThreadedTimer#endLoop()
     */
    @Override
    protected void endLoop()    {    }
        
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.ThreadedTimer#onOneLoop()
     */
    protected void onOneLoop() throws Exception {        
        for (File f : _dirs) {
            scanOneDir(f);
        }
    }
    
    /* (non-Javadoc)
     * @see com.zotoh.maedr.device.RepeatingTimer#getCmdSeq(java.util.ResourceBundle, java.util.Properties)
     */
    protected CmdLineSequence getCmdSeq(ResourceBundle rcb, Properties props) 
    throws Exception {
        CmdLineQuestion q4= new CmdLineQuestion("fmask", getResourceStr(rcb, "cmd.fp.mask")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("fmask", answer);
                return "";
            }};
        final CmdLineQuestion q3= new CmdLineQuestion("dest", getResourceStr(rcb, "cmd.fp.dest")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("destdir", answer);
                return "fmask";
            }};
        CmdLineQuestion q2= new CmdLineQuestion("move", getResourceStr(rcb, "cmd.fp.move"), "y/n","n") {
            protected String onAnswerSetOutput(String answer, Properties props) {
                boolean b="Yy".indexOf(answer)>=0;
                if (b) { q3.setMandatory(true); }
                props.put("automove", b);
                return b ? "dest" : "fmask";
            }};
        final CmdLineQuestion q1= new CmdLineMandatory("root", getResourceStr(rcb, "cmd.fp.root")) {
            protected String onAnswerSetOutput(String answer, Properties props) {
                props.put("rootdir", answer);
                return "move";
            }};
        return new CmdLineSequence(super.getCmdSeq(rcb, props),q1,q2,q3,q4){
            protected String onStart() {
                return q1.getId();
            }           
        };
    }

    private void scanOneDir(File dir) throws Exception {        
        File[] paths= dir.listFiles(_mask);
        if ( !isNilArray(paths))
        { postPoll(paths); }                
    }
    
    private File testDir(File dir) throws Exception {
        if (dir.exists() && dir.isDirectory() && dir.canRead() && dir.canWrite())
        {}
        else{
            throw new Exception("FilePicker: Folder: " + 
                    dir.getCanonicalPath() + " must be a valid directory with RW access") ;
        }      
        return dir;
    }
    
    private void postPoll(File[] files)     {
        Exception error;
        String fn;
        File cf;        
        for (int i=0; i < files.length; ++i)
        try {
            tlog().debug("FilePicker: new file : {}" , files[i]);
            error=null;
            cf= files[i];
            fn= niceFPath(cf);
            if (_destMove != null) 
            try {
                cf= FileUte.moveFileToDir(cf, _destMove, false);                
            }
            catch (IOException e) {
                error=e ;
            }
            dispatch(new FileEvent(this, fn, cf, error)); 
        }
        catch (Exception e) {
            tlog().warn("",e);
        }
        
    }

    
    
    
}
