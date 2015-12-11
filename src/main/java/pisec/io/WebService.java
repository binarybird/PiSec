package pisec.io;

import pisec.io.jaxws.StatusQuery;
import pisec.util.Logger;
import pisec.util.Settings;
import pisec.util.enums.Setting;

import javax.xml.ws.Endpoint;
import java.util.HashMap;
import java.util.logging.Level;

/**
 * Created by jamesrichardson on 10/23/15.
 */
public class WebService {
    private static boolean once = false;
    private static final int port = Integer.valueOf(Settings.GetSettingForKey(Setting.QUERY_SERVICE_PORT));
    private static Endpoint publish = null;

    private WebService(){}

    public static void StartWebService(){
        if(!once) {
            try {
                HashMap<String,Object> props = new HashMap<>();
                props.put("java.util.logging.ConsoleHandler.level", Level.SEVERE);
                props.put("javax.enterprise.resource.webservices.jaxws.server.level",Level.SEVERE);
                java.util.logging.Logger.getLogger("javax.enterprise.resource.webservices.jaxws.server").setLevel(Level.SEVERE);
                java.util.logging.Logger.getLogger("com.sun.xml.internal.ws.transport.http.HttpAdapter").setLevel(Level.SEVERE);
                publish = Endpoint.publish("http://0.0.0.0:" + port + "/status", new StatusQuery());
                publish.setProperties(props);
                once = true;
            }catch(Exception e){
                Logger.Log(e.getMessage());
            }
        }else{
            Logger.Log("Webservice is already running!");
        }
        Logger.Log(GetServiceStatus());
    }
    public static void StopWebService(){
        if(publish != null){
            publish.stop();
            once = false;
        }
        Logger.Log(GetServiceStatus());
    }
    public static boolean IsRunning(){return once;}
    public static int GetPort(){return port;}
    public static String GetLocation(){return "http://0.0.0.0:" + port + "/status";}

    public static String GetServiceStatus(){
        return WebService.class.getName()+"[Running:\""+once+"\" Port:\""+port+"\"  Location:\""+"http://0.0.0.0:" + port + "/status"+"\"]";
    }

}
