import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.zeroturnaround.exec.ProcessExecutor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by carlericsson on 30/01/17.
 */
public class LoadTestRunner {

    public static int runJob (String jobName, int userNumber, int maxLoops, int startUpDelay, int duration, int execAgentID, String sslSettings, String additonalOptions) throws Exception{
        List<String> CommandX = new ArrayList<>();

        CommandX.addAll(Arrays.asList(System.getProperty("user.dir")+"/jre/bin/java","-cp",":*","PrxJob","transmitJob","Local Exec Agent",System.getProperty("user.dir")+"/"+jobName+".class", "-u", Integer.toString(userNumber), "-d", Integer.toString(duration), "-t", "60","-maxloops", Integer.toString(maxLoops), "-sampling", "15", "-percpage", "100", "-percurl", "20", "-maxerrmem", "20", "-nolog"));

        CommandX.addAll(Arrays.asList(additonalOptions.split(" ")));

        ProcessExecutor executor = new ProcessExecutor();

        executor = executor.closeTimeout(60, TimeUnit.SECONDS);

        executor= executor.directory(new File(System.getProperty("user.dir")));

        int exit = executor.command(CommandX).execute().getExitValue();

        System.out.println("--- Exec Job setup done ---");

        CommandX = new ArrayList<String>();

        CommandX.addAll(Arrays.asList(System.getProperty("user.dir")+"/jre/bin/java", "-cp", ":*", "PrxJob", "startJob", "Local Exec Agent", Integer.toString(exit)));

        int exit2 = new ProcessExecutor().command(CommandX).execute().getExitValue();

        System.out.println("--- Exec Job "+exit+" running ---");

        return exit;
    }

    public static int checkJobStatus (int jobId){
        String[] CommandX = {System.getProperty("user.dir")+"/jre/bin/java","-cp",":*","PrxJob","getJobState","Local Exec Agent",Integer.toString(jobId)};
        int exitValue = -1;
        try {
            exitValue = new ProcessExecutor().command(CommandX).execute().getExitValue();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return exitValue;
    }

    public static String getJobStatistics (int jobId){
        String[] CommandX = {System.getProperty("user.dir")+"/jre/bin/java","-cp",":*","PrxJob","getJobRealTimeData","Local Exec Agent",Integer.toString(jobId),"-detailed"};

        String output="";
        try {
            output = new ProcessExecutor().command(CommandX)
                    .readOutput(true).execute()
                    .outputUTF8();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        JSONObject json=new JSONObject();
        try {
            json=XML.toJSONObject(output);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json.toString();
    }


}
