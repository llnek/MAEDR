import com.zotoh.maedr.service.*;
import com.zotoh.maedr.device.*;

import javax.jms.Message;
import javax.jms.TextMessage;

println("Demo JMS client receiving messages from MOCK JMS server...");

def gCounter=0;

JMSMsgService.create("com.zotoh.maedr.mock.jms.MockContextFactory")
.connectionFactory("tcf")
.jndiUser("root")
.jndiPwd("root")
.jmsUser("anonymous")
.jmsPwd("anonymous")
.durable(false)
.provideUrl("java://aaa")
.destination("topic.xyz")
.handler( new JMSMsgHandler() {

    public void eval(JmsEvent evt, Object... args) {
		
		Message msg= evt.getMsg();
		
		println("-> Correlation ID= " + msg.getJMSCorrelationID());
		println("-> Msg ID= " + msg.getJMSMessageID());
		println("-> Type= " + msg.getJMSType());
					
		if (msg instanceof TextMessage) {
			TextMessage t= (TextMessage)msg;
			println("-> Text Message= " + t.getText());
		}
					
		++gCounter;
					
		if (gCounter > 3) {
				println("\nPRESS Ctrl-C anytime to end program.\n");
		}
		
    }
    
}).start();
