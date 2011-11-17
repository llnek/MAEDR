import com.zotoh.maedr.service.*;
import com.zotoh.maedr.device.*;

println("Demo HTTP Server: point your browser to http://" + 
com.zotoh.netio.NetUte.getLocalHost() + ":8080/test/helloworld");

HTTPService.create(8080).handler( new HTTPHandler() {

    public void eval(HttpEvent evt, Object... args) {
        // HttpEventResult
        def res= args[0];
        res.setData("<html><body><h1>Bonjour!</h1></body></html>");
        evt.setResult(res);
    }
    
}).start();
