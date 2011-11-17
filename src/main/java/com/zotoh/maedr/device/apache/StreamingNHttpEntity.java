/*??
 * COPYRIGHT (C) 2010 CHERIMOIA LLC. ALL RIGHTS RESERVED.
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

package com.zotoh.maedr.device.apache;

import static com.zotoh.core.io.StreamUte.streamToStream;
import static com.zotoh.core.util.CoreUte.tstObjArg;
import static com.zotoh.core.util.LoggerFactory.getLogger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

import org.apache.http.HttpEntity;
import org.apache.http.entity.HttpEntityWrapper;
import org.apache.http.nio.ContentDecoder;
import org.apache.http.nio.IOControl;
import org.apache.http.nio.entity.ConsumingNHttpEntity;
import org.apache.http.nio.util.ByteBufferAllocator;
import org.apache.http.nio.util.HeapByteBufferAllocator;

import com.zotoh.core.io.ByteOStream;
import com.zotoh.core.io.StreamData;
import com.zotoh.core.io.StreamUte;
import com.zotoh.core.util.Logger;
import com.zotoh.core.util.Tuple;

/**
 * @author kenl
 *
 */
public class StreamingNHttpEntity extends HttpEntityWrapper implements
        ConsumingNHttpEntity {

    private Logger ilog() {  return _log=getLogger(StreamingNHttpEntity.class);    }
    private transient Logger _log= ilog();
    public Logger tlog() {  return _log==null ? ilog() : _log;    }    
    
    private ByteBufferAllocator _alloctor;
    private StreamData _data;
    private File _fout;
    private OutputStream _os;
    private boolean _finished= false;
    private long _totalBytes=0L;
    private long _thold= 8L * 1024* 1024 ; // 8Meg
    

    /**
     * @param wrapped
     * @param alloctor
     */
    public StreamingNHttpEntity(HttpEntity wrapped, ByteBufferAllocator alloctor)    {
        super(wrapped);
        _alloctor= new HeapByteBufferAllocator() ;
        _data = new StreamData();        
        _os= new ByteOStream(4096);        
    }

    /* (non-Javadoc)
     * @see org.apache.http.nio.entity.ConsumingNHttpEntity#consumeContent(org.apache.http.nio.ContentDecoder, org.apache.http.nio.IOControl)
     */
    @Override
    public void consumeContent(ContentDecoder decoder,
            IOControl iocontrol) throws IOException    {
        tlog().debug("StreamingNHttpEntity: consumeContent()") ;
        if ( ! _finished) {
            sockItDown(decoder);
        }        
        if (decoder.isCompleted()) {
            finish();
        }        
    }

    /* (non-Javadoc)
     * @see org.apache.http.nio.entity.ConsumingNHttpEntity#finish()
     */
    @Override
    public void finish() throws IOException     {        
        tlog().debug("StreamingNHttpEntity: finished()") ;        
        if (!_data.isDiskFile()) {
            _data.resetMsgContent(_os);
        }        
        _os=StreamUte.close(_os);
        _finished = true;
    }

    /* (non-Javadoc)
     * @see org.apache.http.entity.HttpEntityWrapper#getContent()
     */
    public InputStream getContent() throws IOException     {

        if (!_finished)
            throw new IllegalStateException( "Entity content has not been fully received");

        return _data.getStream();
    }

    /* (non-Javadoc)
     * @see org.apache.http.entity.HttpEntityWrapper#isRepeatable()
     */
    public boolean isRepeatable()     {        return false;    }

    /* (non-Javadoc)
     * @see org.apache.http.entity.HttpEntityWrapper#isStreaming()
     */
    public boolean isStreaming()     {        return true;    }

    /* (non-Javadoc)
     * @see org.apache.http.entity.HttpEntityWrapper#writeTo(java.io.OutputStream)
     */
    public void writeTo(OutputStream outstream) throws IOException     {

        tstObjArg("out-stream", outstream);

        InputStream inp = _data.getStream();
        try   {
            streamToStream(inp, outstream, _data.getSize());
        }
        finally        {
            StreamUte.close(inp);
        }

    }
    
    private void sockItDown(ContentDecoder decoder) throws IOException    {

        tlog().debug("StreamingNHttpEntity: sockItDown()") ;
        
        ByteBuffer buffer;        
        int cnt;
        
        buffer= _alloctor.allocate(4096) ;
        do         {
            
            buffer.clear();
            
            if ((cnt = decoder.read(buffer)) == -1)
            break;
            
            if(cnt == 0)             {
                
                if(buffer.hasRemaining())
                break;
                else
                continue;
            } 
            
            // 
            buffer.flip();
            byte[] bits= new byte[4096];
            int len;
           
            while (buffer.hasRemaining() ) {
                len = Math.min(4096, buffer.remaining());
                buffer.get(bits, 0, len) ;
                storeBytes(bits, len);
            }
        }         
        while(true);
        
    }
    
    private void storeBytes(byte[] data, int len)  throws IOException {
        
        if ( ! _data.isDiskFile()) {
            if ( (_totalBytes + len) > _thold)  {
                Tuple t= StreamUte.createTempFile(true);
                _fout=(File)t.get(0);
                OutputStream os= (OutputStream) t.get(1);
                byte[] bits= ((ByteArrayOutputStream)_os).toByteArray();
                os.write(bits);
                os.flush();
                _os= os;
                _data.resetMsgContent(_fout) ;                
            }
        }
        
        _os.write(data, 0, len) ;
        _os.flush();        
        _totalBytes += len;
        
    }
    
}
