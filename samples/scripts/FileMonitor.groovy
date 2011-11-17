import com.zotoh.core.io.StreamUte;
import com.zotoh.core.util.CoreUte;
import java.io.File;
import java.util.Date;
import com.zotoh.core.util.GUID;
import com.zotoh.maedr.service.*;
import com.zotoh.maedr.device.*;


println("Demo file directory monitoring... - picking up new files...");

def gCounter=0;
def rootFolder= java.lang.System.getProperties().getProperty("java.io.tmpdir") +
"/"  +
GUID.generate();
new File(rootFolder).mkdirs();

def t= new Thread(new Runnable(){
	public void run() {
		Thread.sleep(5000);
		while (true) {
			drop();
			Thread.sleep(3000);		
		}
	}
	def drop() {
        def out= new File(rootFolder, GUID.generate()+".txt");
        def s= "Current time is " + CoreUte.fmtDate(new Date());
        println("New file: " + out);
        StreamUte.writeFile(out, s, "utf-8");
	}
});
t.setDaemon(true);
t.start();


FilePickerService.create(rootFolder)
.filemask(".*\\.txt")
.intervalsecs(5)
.handler( new FileFoundHandler() {

    public void eval(FileEvent evt, Object... args) {
		def f0= evt.getOrigFilePath();
		def f=evt.getFile();
		println("New file: " + f0);
		println("Content: " + StreamUte.readFile(f, "utf-8"));
        f.delete();
		++gCounter;
		if (gCounter > 3) {
			println("\nPRESS Ctrl-C anytime to end program.\n");
		}
    }
    
}).start();




