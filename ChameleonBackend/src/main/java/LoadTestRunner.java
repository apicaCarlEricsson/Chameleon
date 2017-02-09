import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Created by carlericsson on 30/01/17.
 */
public class LoadTestRunner {

    public static int runJob (String jobName, int userNumber, int maxLoops, int startUpDelay, int duration, int execAgentID, String sslSettings) throws Exception{
        Runtime rt = Runtime.getRuntime();
        //System.out.println("java -classpath .:/Users/carlericsson/Desktop/New Zebra/New Design/ZebraV2/ZebraUI/prxsniff.jar:/Users/carlericsson/Desktop/New Zebra/New Design/ZebraV2/ZebraUI:/Users/carlericsson/Desktop/New Zebra/New Design/ZebraV2/ZebraUI/iaik_jce_full.jar:/Users/carlericsson/Desktop/New Zebra/New Design/ZebraV2/ZebraUI/iaik_ssl.jar:/Users/carlericsson/Desktop/New Zebra/New Design/ZebraV2/ZebraUI/iaik_eccelerate.jar:/Users/carlericsson/Desktop/New Zebra/New Design/ZebraV2/ZebraUI/iaikPkcs11Provider.jar "+jobName+" -u "+userNumber+" -d "+duration+" -t 60 -sdelay "+startUpDelay+" -maxloops "+maxLoops+" -ssl "+sslSettings+" -sampling 15 -percpage 100 -percurl 20 -maxerrmem 20 -nolog -execAgentJobId "+execAgentID);
        //Process pr = rt.exec("java -classpath .:/Users/carlericsson/Desktop/NewZebra/NewDesign/ZebraV2/ZebraUI/prxsniff.jar:/Users/carlericsson/Desktop/NewZebra/NewDesign/ZebraV2/ZebraUI:/Users/carlericsson/Desktop/NewZebra/NewDesign/ZebraV2/ZebraUI/iaik_jce_full.jar:/Users/carlericsson/Desktop/NewZebra/NewDesign/ZebraV2/ZebraUI/iaik_ssl.jar:/Users/carlericsson/Desktop/NewZebra/NewDesign/ZebraV2/ZebraUI/iaik_eccelerate.jar:/Users/carlericsson/Desktop/NewZebra/NewDesign/ZebraV2/ZebraUI/iaikPkcs11Provider.jar "+jobName+" -u "+userNumber+" -d "+duration+" -t 60 -sdelay "+startUpDelay+" -maxloops "+maxLoops+" -ssl "+sslSettings+" -sampling 15 -percpage 100 -percurl 20 -maxerrmem 20 -nolog -execAgentJobId "+execAgentID);


        //String[] CommandX = {"java", "-classpath", ".:"+System.getProperty("user.dir")+"/prxsniff.jar:"+System.getProperty("user.dir")+":"+System.getProperty("user.dir")+"/iaik_jce_full.jar:"+System.getProperty("user.dir")+"iaik_ssl.jar:"+System.getProperty("user.dir")+"/iaik_eccelerate.jar:"+System.getProperty("user.dir")+"/iaikPkcs11Provider.jar", "PrxJob","transmitJob","Local Exec Agent",jobName+".class", "-u", Integer.toString(userNumber), "-d", Integer.toString(duration), "-t", "60" ,"-maxloops", Integer.toString(maxLoops), "-sampling", "15", "-percpage", "100", "-percurl", "20", "-maxerrmem", "20"};
        //String[] CommandX = {"java", "-classpath", ".:"+System.getProperty("user.dir")+"/prxsniff.jar:"+System.getProperty("user.dir")+":"+System.getProperty("user.dir")+"/iaik_jce_full.jar:"+System.getProperty("user.dir")+"iaik_ssl.jar:"+System.getProperty("user.dir")+"/iaik_eccelerate.jar:"+System.getProperty("user.dir")+"/iaikPkcs11Provider.jar", jobName, "-u", Integer.toString(userNumber), "-d", Integer.toString(duration), "-t", "60" ,"-maxloops", Integer.toString(maxLoops), "-sampling", "15", "-percpage", "100", "-percurl", "20", "-maxerrmem", "20", "-nolog"};
        String[] CommandX = {"transmitJob","Local Exec Agent",jobName+".class", "-u", Integer.toString(userNumber), "-d", Integer.toString(duration), "-t", "60" ,"-maxloops", Integer.toString(maxLoops), "-sampling", "15", "-percpage", "100", "-percurl", "20", "-maxerrmem", "20"};



        int myInt = PrxJob.call(CommandX);

        for (String command : CommandX){
            System.out.print(command+" ");
            System.out.println();
        }

        PrxJob.call(new String[]{"startJob", "Local Exec Agent",Integer.toString(myInt)});



        /*ProcessBuilder builder = new ProcessBuilder(CommandX);

        System.out.println();
        Process pr = builder.start();

        run(pr);

        for (String i : ){

        }
        //System.out.println("java -classpath .:/Users/carlericsson/Desktop/NewZebra/NewDesign/ZebraV2/ZebraUI/prxsniff.jar:/Users/carlericsson/Desktop/NewZebra/NewDesign/ZebraV2/ZebraUI:/Users/carlericsson/Desktop/NewZebra/NewDesign/ZebraV2/ZebraUI/iaik_jce_full.jar:/Users/carlericsson/Desktop/NewZebra/NewDesign/ZebraV2/ZebraUI/iaik_ssl.jar:/Users/carlericsson/Desktop/NewZebra/NewDesign/ZebraV2/ZebraUI/iaik_eccelerate.jar:/Users/carlericsson/Desktop/NewZebra/NewDesign/ZebraV2/ZebraUI/iaikPkcs11Provider.jar "+jobName+" -u "+userNumber+" -d "+duration+" -t 60 -sdelay "+startUpDelay+" -maxloops "+maxLoops+" -ssl "+sslSettings+" -sampling 15 -percpage 100 -percurl 20 -maxerrmem 20 -nolog -execAgentJobId "+execAgentID);
        while(pr.isAlive()){
            System.out.println("Transmitting job...");
        }

        int exitValue = pr.exitValue();

        String[] CommandX2 = {"java", "-classpath", ".:"+System.getProperty("user.dir")+"/prxsniff.jar:"+System.getProperty("user.dir")+":"+System.getProperty("user.dir")+"/iaik_jce_full.jar:"+System.getProperty("user.dir")+"iaik_ssl.jar:"+System.getProperty("user.dir")+"/iaik_eccelerate.jar:"+System.getProperty("user.dir")+"/iaikPkcs11Provider.jar", "PrxJob","startJob","Local Exec Agent",Integer.toString(exitValue)};


        builder = new ProcessBuilder(CommandX2);
        pr = builder.start();

        System.out.println("Job "+exitValue+" is running");

        Thread.sleep(1000*duration);*/

        return 200;
    }


}
