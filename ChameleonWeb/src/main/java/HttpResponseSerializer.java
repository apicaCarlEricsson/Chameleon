import com.google.gson.*;
import dfischer.proxysniffer.HttpResponse;

import java.lang.reflect.Type;
import java.util.Base64;

/**
 * Created by carlericsson on 16/01/17.
 */
public class HttpResponseSerializer implements JsonSerializer<HttpResponse> {

    @Override
    public JsonElement serialize(final HttpResponse httpResponse, final Type type, final JsonSerializationContext context) {
        final JsonObject jsonObj = new JsonObject();
        jsonObj.add("content", context.serialize(createContent(httpResponse)));
        jsonObj.add("header", context.serialize(httpResponse.getHeader()));
        jsonObj.add("headerContentType", context.serialize(httpResponse.getContentType()));
        jsonObj.add("headerServerStatus", context.serialize(httpResponse.getStatus()));
        jsonObj.add("headerServerStatusText", context.serialize(httpResponse.getStatusText()));
        jsonObj.add("headerContentLength", context.serialize(httpResponse.getContentLength()));

        return jsonObj;

    }

    private String createContent(HttpResponse response){
        if (response.hasContent()){
            if (response.getContentType()!=null && response.getContentType().contains("IMAGE")){
                return new String(Base64.getEncoder().encode(response.getContent()));
            }else if(response.isCompressedContent()){
                String content = new String(response.getDecompressedContent());
                String content2 = content.replace("<","&lt;");
                String content3 = content2.replace(">","&gt;");
                return new String(content3);
            }else {
                String content = new String(response.getContent());
                String content2 = content.replace("<","&lt;");
                String content3 = content2.replace(">","&gt;");
                return new String(content3);
            }
        }else {
            return "null";
        }
    }

}
