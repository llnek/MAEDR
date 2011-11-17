import java.io.BufferedInputStream;
import java.io.InputStream;

import com.zotoh.core.util.ByteUte;
import com.zotoh.maedr.service.*;
import com.zotoh.maedr.device.*;

println("Demo send & receive messages via tcpip...");

// create a dummy client, sending messages to the server
def t=new Thread(new Runnable(){
	public void run() {
		Thread.sleep(5000);
		while (true) {
			send();
			Thread.sleep(3000);
		}
	}
	def void send() {
		def soc= new Socket("localhost", 7070),
		bits= "Hello World!".getBytes();
		println("TCP Client: about to send message...") ;
		def os= soc.getOutputStream();
		os.write(ByteUte.readAsBytes(bits.length));
		os.write(bits);
		os.flush();
		soc.close();
	}
});
t.setDaemon(true);
t.start();


TCPService.create(7070).host("localhost").handler( new TCPHandler() {

    public void eval(TCPEvent evt, Object... args) {
		def bf= new BufferedInputStream( evt.getSockIn()),
		buf= new byte[4],
		clen=0;
		
		bf.read(buf);
		clen=ByteUte.readAsInt(buf);
		buf= new byte[clen];
		bf.read(buf);
		println("TCP Server Received: " + new String(buf) ) ;
    }
    
}).start();


