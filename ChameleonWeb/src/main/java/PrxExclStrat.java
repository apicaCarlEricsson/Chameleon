import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import dfischer.proxysniffer.HttpRequest;
import dfischer.proxysniffer.HttpResponse;
import dfischer.proxysniffer.ProxyDataDump;

/**
 * Created by carlericsson on 11/01/17.
 */
public class PrxExclStrat implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return (f.getDeclaringClass() == ProxyDataDump.class && f.getName().equals("varSourceHandler") ||
                f.getDeclaringClass() == ProxyDataDump.class && f.getName().equals("varHandler") ||
                f.getDeclaringClass() == ProxyDataDump.class && f.getName().equals("externalResources") ||
                f.getDeclaringClass() == ProxyDataDump.class && f.getName().equals("sessionFilter") ||
                f.getDeclaringClass() == ProxyDataDump.class && f.getName().equals("transactionHandler")||
                f.getDeclaringClass() == HttpRequest.class && f.getName().equals("bin") ||
                f.getDeclaringClass() == HttpResponse.class && f.getName().equals("contentVector") ||
                f.getDeclaringClass() == HttpRequest.class && f.getName().equals("sslServerData"));
    }

    @Override
    public boolean shouldSkipClass(Class<?> aClass) {
        return false;
    }
}
