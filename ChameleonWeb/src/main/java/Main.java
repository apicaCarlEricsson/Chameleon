import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dfischer.proxysniffer.*;

import java.io.File;
import java.io.IOException;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {

        PrxWorker worker = new PrxWorker();

        Gson gsonPrxDat = new GsonBuilder()
                .setExclusionStrategies(new PrxExclStrat())
                .registerTypeAdapter(HttpResponse.class, new HttpResponseSerializer())
                .create();

        File parent = new File(System.getProperty("user.dir"));
        
        port(7880);

        staticFiles.externalLocation(System.getProperty("user.dir")+"/web2");

        //Renders the webPage
        get("/", (req, res) -> Butler.renderContent("web2/index.html"));

        get("/prxMetaData", (req, res) -> {
                    res.type("application/json");
                    return gsonPrxDat.toJson(worker.fetchPrxDat());
                }
        );

        get("/getAllVars", (req, res) -> {
            res.type("application/json");
            return gsonPrxDat.toJson(worker.getAllVars());

        });
        get("/getInlineScripts", (req, res) -> {
            res.type("application/json");
            return gsonPrxDat.toJson(worker.getAllInlineScripts());

        });

        post("/createInlineScript", (req, res) -> {
            InlineScript script = gsonPrxDat.fromJson(req.body(),InlineScript.class);
            return worker.addInlineScript(script.title,script.execScope,script.sourceCode,script.index,script.inputVars,script.outputVars);
        });

        get("/getURLNumbers", (req, res) -> {
            res.type("application/json");
            return gsonPrxDat.toJson(worker.getURLIndex());
        });

        get("/createVar/:name/:scope", (req, res) ->
                worker.createVariable(req.params(":name"), req.params(":name")));


        get("/openScript/:filename", (req, res) ->
            worker.reloadPrxDat(req.params(":filename")));

        get("/savePrxDat", (req, res) -> {
            res.type("application/json");
            if (!worker.isProjectSaved()) {
                return "{\"isSaved\": false}";
            }
            else{
                worker.saveProject(worker.fetchPrxDat().getProjectName());
                return "{\"isSaved\": true}";
            }
        });

        get("/savePrxDat/:name", (req, res) -> {
            res.type("application/json");
            return worker.saveProject(req.params(":name"));

        });

        get("/listclassfiles", (req, res) -> {
            res.type("application/json");
            return Butler.listFiles();

        });

        get("/clearSession", (req, res) -> {
            worker.clearPrxDat();
            res.type("application/json");
            return "{\"isCleared\":true}";
        });

        get("/startRecording", (req, res) -> {
            String projectname = worker.fetchPrxDat().getProjectName();
            if (!(projectname.equalsIgnoreCase("-1")||projectname.equalsIgnoreCase("tempNull"))){
                RecorderBridge.loadSession(worker.fetchPrxDat().getProjectName());
                return RecorderBridge.startRecording();
            }else {
                return RecorderBridge.startRecording();
            }
        });

        get("/stopRecording", (req, res) -> {
            String projectname = worker.fetchPrxDat().getProjectName();
            RecorderBridge.stopRecording();
            if (!(projectname.equalsIgnoreCase("-1")||projectname.equalsIgnoreCase("tempNull"))){
                worker.reloadPrxDat(projectname, RecorderBridge.saveRecording(projectname));
            }else{
                worker.reloadPrxDat("tempNull", RecorderBridge.saveRecording());
                worker.deleteTempDat();
            }
            return 200;
        });

        get("/clearRecording", (req, res) -> {
            worker.clearPrxDat();
            return RecorderBridge.clearRecording();
        });

        get("/getNumberOfRecordedItems", (req, res) -> {
            res.type("application/json");
            return  RecorderBridge.getNumberOfItems();
        });

        get("/addPageBreakRec/:name", (req, res) -> {
            String projectname = worker.fetchPrxDat().getProjectName();
            RecorderBridge.insertPageBreak(req.params(":name"),3,35);
            if (!(projectname.equalsIgnoreCase("-1")||projectname.equalsIgnoreCase("tempNull"))){
                worker.reloadPrxDat(projectname, RecorderBridge.saveRecording(projectname));
            }else{
                worker.reloadPrxDat("tempNull", RecorderBridge.saveRecording());
                worker.deleteTempDat();
            }
            return 200;
        });

        get("/generateLoadTest", (req, res) -> {
            worker.generateLoadTest();
            res.type("application/json");
            return "{\"generated\": true}";
        });


        /*get("/runTest/:scriptName/:numUsers/:maxLoops/:duration", (req, res) -> {
            try {
                int numUsers = Integer.parseInt(req.params(":numUsers"));
                int maxLoops = Integer.parseInt(req.params(":maxLoops"));
                int duration = Integer.parseInt(req.params(":duration"));

                int jobId = LoadTestRunner.runJob(req.params(":scriptName"), numUsers,maxLoops,1,duration,9999,"tls11");
                res.type("application/json");
                return "{\"jobId\": "+jobId+"}";
            } catch (Exception e) {
                res.type("application/json");
                return "{\"jobId\": -1}";
            }
        });*/

        post("/runTest", (req, res) -> {
            ScriptConfig script = gsonPrxDat.fromJson(req.body(),ScriptConfig.class);
            int jobID=LoadTestRunner.runJob(script.currentScript,script.userNumber, script.iterationsPerUser, 1, script.duration, 9999, "tls11", script.additionalOptions);
            res.type("application/json");
            return gsonPrxDat.toJson(new Responses.runTestResponse(jobID));
        });


        get("/getJobStatus/:jobId", (req, res) -> {
            return LoadTestRunner.checkJobStatus(Integer.parseInt(req.params(":jobId")));
        });

        get("/getJobData/:jobId", (req, res) -> {
            res.type("application/json");
            return LoadTestRunner.getJobStatistics(Integer.parseInt(req.params(":jobId")));
        });

        new Thread(() -> {
            try {
                Butler.runOSCommand(new String[]{parent.getPath()+"/jre/bin/java", "-cp", ":*", "ProxySniffer", "-RESTAPIServer", "-webadmin", "-ExecAgent", "-runtimedatadir", parent.getPath() + "/RuntimeData", "-jobdir", parent.getPath() + "/ExecAgentJobs"});
            } catch (IOException e) {
                System.out.println("Unable to start ZebraTester Console");
            }
        }).start();


    }

}
