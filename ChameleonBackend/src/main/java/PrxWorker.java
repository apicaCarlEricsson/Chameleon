import dfischer.generator.CreateLoadtestProgram;
import dfischer.keymanager.common.Key;
import dfischer.proxysniffer.*;
import dfischer.utils.Base64Decoder;
import dfischer.utils.LoadtestInlineScriptContext;
import dfischer.utils.NextProxyConfig;
import dfischer.webadmininterface.DisplayListCache;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

/**
 * Created by carlericsson on 30/12/16.
 */
public class PrxWorker {

    private ProxyDataDump prxdat;

    public PrxWorker(){
        prxdat = new ProxyDataDump();

        prxdat.setProjectName("-1");
    }


    public PrxWorker(String fileName){
        prxdat = new ProxyDataDump();
        try {
            prxdat.readObject(new DataInputStream(new FileInputStream(fileName)));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ProxySnifferVarHandlerDupScopeException e) {
            e.printStackTrace();
        } catch (ProxySnifferVarHandlerInvNameException e) {
            e.printStackTrace();
        } catch (ProxySnifferVarHandlerKeywordException e) {
            e.printStackTrace();
        }
        fileName= fileName.replace(".prxdat","");

        prxdat.setProjectName(fileName);

    }

    public List<Integer> getURLIndex(){
        List<Integer> myList = new ArrayList<>();
        for (int i = 0 ; i < prxdat.getProxyData().size(); i++){
            ProxyDataRecord record = (ProxyDataRecord) prxdat.getProxyData().get(i);
            if (!record.isDataTypePageBreak()){
                myList.add(i);
            }
        }
        return myList;
    }

    public void clearPrxDat (){
        prxdat = new ProxyDataDump();

        prxdat.setProjectName("-1");
    }

    public int addInlineScript(String title, String execScope, String sourceCode,int index, String[] inputVars, String[] outputVars){
        ProxySnifferVarSourceInlineScript inlineScript;

        switch (execScope){
            case "all_urls_start":
                inlineScript = new ProxySnifferVarSourceInlineScript(ProxySnifferVarSourceInlineScript.EXEC_SCOPE_ALL_URLS_START,
                        LoadtestInlineScriptContext.RESULT_TYPE_SET_OUTPUT_VARS);
                break;
            case "global_start":
                inlineScript = new ProxySnifferVarSourceInlineScript(ProxySnifferVarSourceInlineScript.EXEC_SCOPE_GLOBAL_START,
                        LoadtestInlineScriptContext.RESULT_TYPE_SET_OUTPUT_VARS);
                break;
            case "global_end":
                inlineScript = new ProxySnifferVarSourceInlineScript(ProxySnifferVarSourceInlineScript.EXEC_SCOPE_GLOBAL_END,
                        LoadtestInlineScriptContext.RESULT_TYPE_SET_OUTPUT_VARS);
                break;
            case "user_start":
                inlineScript = new ProxySnifferVarSourceInlineScript(ProxySnifferVarSourceInlineScript.EXEC_SCOPE_USER_START,
                        LoadtestInlineScriptContext.RESULT_TYPE_SET_OUTPUT_VARS);
                break;
            case "user_end":
                inlineScript = new ProxySnifferVarSourceInlineScript(ProxySnifferVarSourceInlineScript.EXEC_SCOPE_USER_END,
                        LoadtestInlineScriptContext.RESULT_TYPE_SET_OUTPUT_VARS);
                break;
            case "url_end":
                inlineScript = new ProxySnifferVarSourceInlineScript(ProxySnifferVarSourceInlineScript.EXEC_SCOPE_URL_END,index,
                        LoadtestInlineScriptContext.RESULT_TYPE_SET_OUTPUT_VARS);
                break;
            case "url_start":
                inlineScript = new ProxySnifferVarSourceInlineScript(ProxySnifferVarSourceInlineScript.EXEC_SCOPE_URL_START,index,
                        LoadtestInlineScriptContext.RESULT_TYPE_SET_OUTPUT_VARS);
                break;
            case "loop_start":
                inlineScript = new ProxySnifferVarSourceInlineScript(ProxySnifferVarSourceInlineScript.EXEC_SCOPE_LOOP_START,
                        LoadtestInlineScriptContext.RESULT_TYPE_SET_OUTPUT_VARS);
                break;
            case "loop_end":
                inlineScript = new ProxySnifferVarSourceInlineScript(ProxySnifferVarSourceInlineScript.EXEC_SCOPE_LOOP_END,
                        LoadtestInlineScriptContext.RESULT_TYPE_SET_OUTPUT_VARS);
                break;
            case "all_urls_end":
                inlineScript = new ProxySnifferVarSourceInlineScript(ProxySnifferVarSourceInlineScript.EXEC_SCOPE_ALL_URLS_END,
                        LoadtestInlineScriptContext.RESULT_TYPE_SET_OUTPUT_VARS);
                break;
            default:
                inlineScript = new ProxySnifferVarSourceInlineScript(ProxySnifferVarSourceInlineScript.EXEC_SCOPE_URL_END,
                        LoadtestInlineScriptContext.RESULT_TYPE_SET_OUTPUT_VARS);
                break;
        }

        inlineScript.setInlineScriptTitle(title);
        inlineScript.setInlineScriptCode(new String(java.util.Base64.getDecoder().decode(sourceCode)));

        inlineScript = (ProxySnifferVarSourceInlineScript) prxdat.getVarSourceHandler().addVarSource(inlineScript);

        for (int i = 0 ; i < inputVars.length; i++){
            ProxySnifferVarAssignerInlineScript proxySnifferVarAssignerInlineScript =
                    new ProxySnifferVarAssignerInlineScript(inputVars[i],index,inlineScript.getUniqueKey(),i);
            ProxySnifferVar aVar = prxdat.getVarHandler().getVar(inputVars[i]);
            proxySnifferVarAssignerInlineScript=
                    (ProxySnifferVarAssignerInlineScript) aVar.addVarAssigner(proxySnifferVarAssignerInlineScript);
        }

        for (int i = 0 ; i < outputVars.length; i++){
            ProxySnifferVarExtractorInlineScript proxySnifferVarExtractorInlineScript =
                    new ProxySnifferVarExtractorInlineScript(i, outputVars[i], inlineScript.getUniqueKey());
            ProxySnifferVar aVar = prxdat.getVarHandler().getVar(outputVars[i]);
            proxySnifferVarExtractorInlineScript=
                    (ProxySnifferVarExtractorInlineScript) aVar.addVarExtractor(proxySnifferVarExtractorInlineScript);
        }

        return 200;


    }

    public ProxySnifferVar[] getAllVars(){
        return prxdat.getVarHandler().getVars();
    }

    public ProxySnifferVarSourceDataInstance[] getAllInlineScripts(){
        return prxdat.getVarSourceHandler().getVarSources();
    }

    public void connectInlineWithVar (ProxySnifferVarSourceInlineScript inlineScript){

    }

    public int createVariable (String scope, String varName){
        ProxySnifferVar newVar;
        switch (scope) {
            case "global":
                newVar = new ProxySnifferVar(ProxySnifferVar.SCOPE_GLOBAL,varName);
                break;
            case "loop":
                newVar = new ProxySnifferVar(ProxySnifferVar.SCOPE_LOOP,varName);
                break;
            case "innerloop":
                newVar = new ProxySnifferVar(ProxySnifferVar.SCOPE_INNER_LOOP,varName);
                break;
            case "user":
                newVar = new ProxySnifferVar(ProxySnifferVar.SCOPE_USER,varName);
                break;
            default:
                newVar = new ProxySnifferVar(ProxySnifferVar.SCOPE_GLOBAL,varName);
                break;
        }

        try {
            newVar = prxdat.getVarHandler().addVar(newVar);
        } catch (ProxySnifferVarHandlerDupScopeException e) {
            System.out.println("ProxySniffer VarHandler DupScope exception encountered when trying to create variable "+varName);
        } catch (ProxySnifferVarHandlerInvNameException e) {
            System.out.println("ProxySniffer VarHandler Invname exception encountered when trying to create variable "+varName);
        } catch (ProxySnifferVarHandlerKeywordException e) {
            System.out.println("ProxySniffer VarHandler Keyword exception encountered when trying to create variable "+varName);
        }

        return 200;
    }

    public void generateLoadTest() {
        DisplayListCache cache = new DisplayListCache();

        Vector dataRecord = prxdat.getProxyData();

        for (int i = 0; i <dataRecord.size();i++ ){
            cache.add((ProxyDataRecord) dataRecord.get(i),i,i);
        }

        System.out.println("--- Generated Cache ----");

        ProxyAdminNetCmd admin = new ProxyAdminNetCmd("localhost",7998);

        CreateLoadtestProgram generator = new CreateLoadtestProgram(new NextProxyConfig(false, true, "",7997,"",7997,null,""), 2,prxdat.getProjectName()+".prxdat");

        System.out.println("--- Create LoadTest Program Done ----");

        try {

            //TODO Fix so that it dynamically loads from file
            //File javaKeyFile = new File(System.getProperty("user.dir")+"/prxsniff.key");
            ProxySnifferSettings.key= new Key("aQAA-DgQR-AAAU-VGgH-*3Q@");
            System.out.println("--- Writing Java File ----");
            generator.write(cache,admin,prxdat.getVarSourceHandler(),prxdat.getVarHandler(),prxdat.getTransactionHandler(),prxdat.getExternalResources()
                    ,new PrintWriter(new FileOutputStream(System.getProperty("user.dir")+"/"+prxdat.getProjectName()+".java")));
            System.out.println("--- Done Writing Java File ----");
        } catch (Exception e) {
            System.out.println("--- IO Exception encountered when creating "+prxdat.getProjectName()+".java"+" ----");
            e.printStackTrace();
        }


        File javaFile = new File(prxdat.getProjectName()+".java");

        //compiler.getTask(null, sfm, null, optionList, null, sfm.getJavaFileObjects(new File[]{javaFile})).call();

        //compiler.run(null, null, null, "-classpath "+System.getProperty("user.dir")+"/iaik_eccelerate.jar;"+System.getProperty("user.dir")+"/iaik_eccelerate_ssl.jar;"+System.getProperty("user.dir")+"/iaik_jce_full.jar;"+System.getProperty("user.dir")+"/iaik_ssl.jar;"+System.getProperty("user.dir")+"/iaikPkcs11Provider.jar;"+System.getProperty("user.dir")+"/iaikPkcs11Wrapper.jar;"+System.getProperty("user.dir")+"/prxsniff.jar "+System.getProperty("user.dir")+"/"+javaFile.getPath());

        String[] CommandX = {System.getProperty("user.dir")+"/jre/bin/javac", "-cp", System.getProperty("user.dir")+"/prxsniff.jar:"+System.getProperty("user.dir")+":"+System.getProperty("user.dir")+"/iaik_jce_full.jar:"+System.getProperty("user.dir")+"/iaik_ssl.jar:"+System.getProperty("user.dir")+"/iaik_eccelerate.jar:"+System.getProperty("user.dir")+"/iaikPkcs11Provider.jar", System.getProperty("user.dir")+"/"+javaFile.getPath()};

        ProcessBuilder builder = new ProcessBuilder(CommandX);

        for (String string : builder.command()){
            System.out.print(string+" ");
        }
        System.out.println();

        try {
            Process p = builder.start();

            String line;

            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = input.readLine()) != null) {
                System.out.println(line);
            }
            input.close();
        }catch (IOException e){e.printStackTrace();}
    }


    public void reloadPrxDat (String name, InputStream stream) {
        prxdat = new ProxyDataDump();
        try {
            prxdat.readObject(new DataInputStream(stream));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO Exception");
        } catch (ProxySnifferVarHandlerDupScopeException e) {
            e.printStackTrace();
            System.out.println("PrxVarDup Exception");
        } catch (ProxySnifferVarHandlerInvNameException e) {
            e.printStackTrace();
            System.out.println("PrxVarInv Exception");
        } catch (ProxySnifferVarHandlerKeywordException e) {
            e.printStackTrace();
            System.out.println("PrxVarKey Exception");
        }
        prxdat.setProjectName(name);
    }

    public int reloadPrxDat (String fileName) {
        prxdat = new ProxyDataDump();
        try {
            prxdat.readObject(new DataInputStream(new FileInputStream(fileName)));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ProxySnifferVarHandlerDupScopeException e) {
            e.printStackTrace();
        } catch (ProxySnifferVarHandlerInvNameException e) {
            e.printStackTrace();
        } catch (ProxySnifferVarHandlerKeywordException e) {
            e.printStackTrace();
        }
        fileName= fileName.replace(".prxdat","");

        prxdat.setProjectName(fileName);

        return 200;
    }

    public void deleteTempDat () throws IOException {
        Path fileToDeletePath = Paths.get("tempNull.prxdat");
        Files.deleteIfExists(fileToDeletePath);
    }


    public ProxyDataDump fetchPrxDat () {
        return prxdat;
    }

    public byte[] fetchResponseContent(int index){
        ProxyDataRecord record = (ProxyDataRecord) prxdat.getProxyData().get(index);
        return record.getHttpResponse().getContent();
    }



    public Collection fetchProxyData() {
        return prxdat.getProxyData();
    }

    public boolean isProjectSaved(){
        if (prxdat.getProjectName().equalsIgnoreCase("tempNull")||(prxdat.getProjectName().equalsIgnoreCase("-1"))){
            return false;
        }else{
            return true;
        }
    }

    public String saveProject(String name) throws Exception {

        File prx = new File(name+".prxdat");
        prxdat.setProjectName(name);

        prxdat.writeObject(new DataOutputStream(new FileOutputStream(prx)));
        return "{isSaved: true}";
    }



    public void fetchAllData(){
    }

    //Fetches a list containing only pageBreaks
    public Vector<ProxyDataRecord> fetchPageBreaks (){
        Vector pageBreakVector = new Vector<ProxyDataRecord>();

        for (Object record : prxdat.getProxyData()){
            if (((ProxyDataRecord) record).isDataTypePageBreak()){
                pageBreakVector.add(record);
            }
        }
        return pageBreakVector;
    }

    //Fetches a list containing only HTTP Data
    public Vector<ProxyDataRecord> fetchUrlData() {
        Vector urlVector = new Vector<ProxyDataRecord>();

        for (Object record : prxdat.getProxyData()){
            if (((ProxyDataRecord) record).isDataTypeHttpData()){
                urlVector.add(record);
            }
        }
        return urlVector;
    }


}
