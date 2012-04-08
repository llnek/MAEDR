# About
A framework designed for building event driven applications. The goal is to make application development fast, effective and without unnecessary complexities.  Use this to build a ESB (Enterprise Service Bus) , SOA (Service oriented Arch.) service end points or a Web Application. 

# Supported Platforms
* Java &gt;= 1.6
* Linux &amp; Windows
* works with Groovy &amp; Scala out of the box.

# Examples
Lots of examples showcasing how to use the framework, from simple to complex.  All samples
are available in Java, groovy or Scala.

# Features
* Code! and then compile and run via simple command-line menu.
* Package and deploy to remote host via SSH.
* Together with Camungo (An EC2 console), can easily deploy and manage your app on EC2.
* Event driven, asynchronous, workflow driven coding style.
* Code your business logic in units and the framework will handle events and schedule your work units.
* Uses proven open-source libraries such as Netty-NIO, Jetty/Web.
* Plugin architecture allows user defined Event Devices.
* Access and Manage your EC2 instances right from command-line menu.

# Supported Event Devices (Event-Sources)
* HTTP Server
* POP3 Email Receiver
* File Monitoring
* JMS Message Listener
* TCP Socket
* Servlet
* RESTful
* WebSocket
* Timer(s)
* RSS/Atom Reader

# Code! Node.JS style ( using groovy ) - for fun only
<pre>
// file: demo.groovy
import com.zotoh.maedr.service.*;
import com.zotoh.maedr.device.*;
HTTPService.create(8080).handler(
// callback
    {   evt, args ->
        def res=args[0];
        res.setData(&lt;html&gt; &lt;body&gt; &lt;h1&gt;Bonjour!&lt;/h1&gt; &lt;/body&gt; &lt;/html&gt;);
        evt.setResult(res);
    } as HTTPHandler
).start();
</pre>

# Making a Workflow
<pre>
import com.zotoh.maedr.core.Job;
import com.zotoh.maedr.wflow.*;
public class MyFlow extends MiniWFlow {
    //this is how the framework assign you the job.
    public MyFlow(Job j) { super(j); }
    // hand to the framework the 1st(initial) step.
    protected Activity onStart() {
        return new PTask(new Work(){
            public void eval(Job j, Object arg) {
                System.out.println("hello, I am step 1");
            }
        }).chain(new PTask(new Work(){  // step2
            public void eval(Job j, Object arg) {
                System.out.println("hello, I am step 2");
            }
        }));
    }
}
</pre>

# Command line menu (terminal friendly)
<pre>
app create/web <app-name>          ' e.g. create helloworld as a webapp.
app create <app-name>              ' e.g. create helloworld
app ide/eclipse                    ' Generate eclipse project files.
app compile                        ' Compile sources.
app test                           ' Run test cases.
app debug <port>                   ' Start & debug the application.
app start[/bg]                     ' Start the application.
app run[/bg] <script-file>         ' Run a Groovy script.
app bundle <output-dir>            ' Package application.
device configure <device-type>     ' Configure a device.
device add <new-type>              ' Add a new  device-type.
crypto generate/serverkey          ' Create self-signed server key (pkcs12).
crypto generate/password           ' Generate a random password.
crypto generate/csr                ' Create a Certificate Signing Request.
crypto encrypt <some-text>         ' e.g. encrypt SomeSecretData
crypto testjce                     ' Check JCE  Policy Files.
demo samples                       ' Generate a set of samples.
version                            ' Show version info.
</pre>

# Cloud (EC2) menu
<pre>
cloud configure                    ' Set cloud info & credential.
cloud sshinfo                      ' Set SSH info.
cloud install <ver> <host:dir>     ' Install MAEDR to host:target-dir.
cloud app/deploy  <host:dir>       ' Deploy app to host:target-dir.
cloud app/run  <host:dir>          ' Deploy and run app.
cloud sync <regions|datacenters>   ' Get latest set of Regions or Zones.
cloud image/set <image-id>         ' Set default Image.
cloud image/*                      ' Launch an Image.
cloud ip/list                      ' List Elastic IPAddrs.
cloud ip/bind <ipaddr> <vm-id>     ' Assign IPAddr to VM.
cloud ip/+                         ' Add a new IPAddr.
cloud ip/- <ipaddr>                ' Remove a IPAddr.
cloud vm/list                      ' List Virtual Machines.
cloud vm/set <vm-id>               ' Set default VM.
cloud vm/? [vm-id]                 ' Describe a VM.
cloud vm/* [vm-id]                 ' Start a VM.
cloud vm/! [vm-id]                 ' Stop a VM.
cloud vm/% [vm-id]                 ' Terminate a VM.
cloud sshkey/list                  ' List SSH Keys.
cloud sshkey/set <keyname>         ' Set default SSH Key.
cloud sshkey/+ <keyname>           ' Add a new SSH Key.
cloud sshkey/- <keyname>           ' Remove a SSH Key.
cloud secgrp/list                  ' List Security Groups.
cloud secgrp/set <group>           ' Set default Security Group.
cloud secgrp/+ <group>             ' Add a new Security Group.
cloud secgrp/- <group>             ' Remove a Security Group.
cloud fwall/+ <group@rule>         ' Add a new Firewall rule.
cloud fwall/- <group@rule>         ' Remove a Firewall rule.
:e.g. xyz@tcp#0.0.0.0/0#1#10       ' From port 1 to port 10.
:e.g. xyz@tcp#0.0.0.0/0#22         ' Port 22.
</pre>

# Lots more to read
Goto [http://maedr.zotoh.com/](http://maedr.zotoh.com)



# Contact
For questions and bug reports, please email [contactzotoh@gmail.com](mailto:contactzotoh@gmail.com)



# Latest binary
Download the latest bundle [1.0.0](http://maedr.zotoh.com/packages/stable/1.0.0/maedr-1.0.0.tar.gz)


