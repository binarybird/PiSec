package pisec.io;


import org.w3c.dom.Document;
import org.w3c.dom.Element;
import pisec.device.WiFi;
import pisec.gui.controller.RootPaneController;
import pisec.security.AlarmManager;
import pisec.security.auth.AuthValidator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

/**
 * Created by jamesrichardson on 10/23/15.
 */
public class XMLSerializer {

    private XMLSerializer(){}

    public static String Serialize(){
        String ret = "";

        try{
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element root = doc.createElement("status");

            root.appendChild(createManagerElement(doc));
            root.appendChild(createUIElement(doc));
            root.appendChild(createAlarmElements(doc));
            root.appendChild(createWebServiceElement(doc));
            doc.appendChild(root);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");

            StringWriter writer = new StringWriter();
            transformer.transform(new DOMSource(doc), new StreamResult(writer));
            ret = writer.getBuffer().toString();

        } catch (ParserConfigurationException pce) {
            pce.printStackTrace();
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        }

        return ret;
    }

    private static Element createWebServiceElement(Document doc){
        Element webServiceElement = doc.createElement("webservice");

        Element runningElement = doc.createElement("running");
        runningElement.setTextContent(String.valueOf(WebService.IsRunning()));

        Element portElement = doc.createElement("port");
        portElement.setTextContent(WebService.GetPort()+"");

        Element locationElement = doc.createElement("location");
        locationElement.setTextContent(WebService.GetLocation());

        webServiceElement.appendChild(runningElement);
        webServiceElement.appendChild(portElement);
        webServiceElement.appendChild(locationElement);

        return webServiceElement;

    }

    private static Element createManagerElement(Document doc){
        Element managerElement = doc.createElement("manager");

        Element stateElement = doc.createElement("state");
        stateElement.setTextContent(AlarmManager.GetSharedAlarmManager().getAlarmState().name());

        Element modeElement = doc.createElement("mode");
        modeElement.setTextContent(AlarmManager.GetSharedAlarmManager().getAlarmMode().name());

        Element failedLoginsElement = doc.createElement("failedlogins");
        failedLoginsElement.setTextContent(AuthValidator.getFailedLoginAttemptsTotal()+"");

        managerElement.appendChild(stateElement);
        managerElement.appendChild(modeElement);
        managerElement.appendChild(failedLoginsElement);

        return managerElement;
    }

    private static Element createUIElement(Document doc){
        Element managerElement = doc.createElement("ui");

        Element windowsElement = doc.createElement("windows");
        windowsElement.setTextContent(RootPaneController.GetSharedRootPaneController().getOpenWindows()+"");

        Element messageElement = doc.createElement("message");
        messageElement.setTextContent(RootPaneController.GetSharedRootPaneController().getCurrentMessage());

        Element wifisignalElement = doc.createElement("wifisignal");

        int signalStrength = -1;
        final String sysName = System.getProperty("os.name");
        if(!sysName.equals("Mac OS X") && !sysName.toUpperCase().contains("WINDOWS")) {
            try {
                signalStrength = WiFi.GetSignalStrength();
            }catch(Exception e){
            }
        }
        wifisignalElement.setTextContent(signalStrength+"");

        managerElement.appendChild(windowsElement);
        managerElement.appendChild(messageElement);
        managerElement.appendChild(wifisignalElement);

        return managerElement;
    }

    private static Element createAlarmElements(Document doc){
        Element alarmsElement = doc.createElement("alarms");

        AlarmManager.GetSharedAlarmManager().getPreviousAlarms().forEach(e->{
            alarmsElement.appendChild(createAlarmElement(e, doc));
        });

        return alarmsElement;
    }

    private static Element createAlarmElement(AlarmManager.Alarm alarm, Document doc){
        Element alarmElement = doc.createElement("alarm");

        Element dateElement = doc.createElement("date");
        dateElement.setTextContent(alarm.getDate().toString());

        Element messageElement = doc.createElement("alarmmessage");
        messageElement.setTextContent(alarm.getMessage());

        Element zoneElement = doc.createElement("zone");
        zoneElement.setTextContent(alarm.getZone().toString());

        Element picturelocationElement = doc.createElement("picturelocation");
        picturelocationElement.setTextContent(alarm.getPictureLocation());

        alarmElement.appendChild(dateElement);
        alarmElement.appendChild(messageElement);
        alarmElement.appendChild(zoneElement);
        alarmElement.appendChild(picturelocationElement);

        return alarmElement;
    }

}
