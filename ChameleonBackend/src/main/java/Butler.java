import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlericsson on 26/12/16.
 */
public class Butler {

    public static String renderContent(String htmlFile) {
        try {
            File file = new File(htmlFile);
            return new String(Files.readAllBytes(Paths.get(file.toURI())));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String listFiles() {
        Gson gson = new Gson();
        File folder = new File(System.getProperty("user.dir"));
        File[] listOfFiles = folder.listFiles();
        List<String> classList = new ArrayList();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() && listOfFiles[i].getName().contains(".class")) {
                classList.add(listOfFiles[i].getName().replace(".class",""));
            }
        }
        return gson.toJson(classList.toArray());
    }

    public static void startConsole(String[] args){
        ProxySniffer console = new ProxySniffer();
        console.main(args);
    }

}