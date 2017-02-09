import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Created by calle on 2017-01-23.
 */
public class RecorderBridge {

    public static int startRecording() throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.get("http://127.0.0.1:7996/?cmd=startRecording").asJson();

        return response.getStatus();
    }

    public static int stopRecording() throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.get("http://127.0.0.1:7996/?cmd=stopRecording").asJson();

        return response.getStatus();
    }

    public static int loadSession(String name) throws Exception{
        final InputStream stream = new FileInputStream(name+".prxdat");
        final byte[] bytes = new byte[stream.available()];
        stream.read(bytes);
        stream.close();
        final HttpResponse<JsonNode> jsonResponse = Unirest.post("http://127.0.0.1:7996/?cmd=loadSession")
                .header("Content-Type", "femto/binary")
                .body(bytes)
                .asJson();
        return jsonResponse.getStatus();
    }

    public static int clearRecording() throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.get("http://127.0.0.1:7996/?cmd=clearRecording").asJson();

        return response.getStatus();
    }

    public static int insertPageBreak(String comment, int delaySeconds, int randomization) throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.get("http://127.0.0.1:7996/?cmd=insertPageBreak")
                .queryString("comment", comment)
                .queryString("delaySeconds", delaySeconds)
                .queryString("randomization", randomization)
                .asJson();

        return response.getStatus();
    }

    public static String getNumberOfItems() throws UnirestException {
        HttpResponse<JsonNode> response = Unirest.get("http://127.0.0.1:7996/?cmd=getNumRecordedItems").asJson();
        return response.getBody().toString();
    }

    public static InputStream saveRecording() throws UnirestException{
        HttpResponse<InputStream> response = Unirest.get("http://127.0.0.1:7996/?cmd=getSession&projectName=tempNull&asBinary=true").asBinary();
        return response.getBody();
    }

    public static InputStream saveRecording(String name) throws UnirestException{
        HttpResponse<InputStream> response = Unirest.get("http://127.0.0.1:7996/?cmd=getSession&projectName="+name+"&asBinary=true").asBinary();
        return response.getBody();
    }
}
