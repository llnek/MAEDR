import com.zotoh.maedr.service.*;
import com.zotoh.maedr.device.*;

// for demo reason only
System.getProperties().put("maedr.pop3.mockstore", "com.zotoh.maedr.mock.mail.MockPop3Store");
def gCounter=0;

println("Demo receiving emails...");

POP3Service.create("").port(110).user("joe").password("secret").intervalsecs(5).handler( new POP3Handler() {

    public void eval(POP3Event evt, Object... args) {
		def bits=evt.getMsg().getBytes();
		println("########################");
		print(evt.getSubject() + "\r\n");
		print(evt.getFrom() + "\r\n");
		print(evt.getTo() + "\r\n");
		print("\r\n");
		println(new String(bits,"utf-8"));
		
		++gCounter;
		
		if (gCounter > 3) {
				println("\nPRESS Ctrl-C anytime to end program.\n");
		}
    }
    
}).start();
