package pisec.io.jaxws;

import pisec.io.XMLSerializer;

import javax.jws.WebService;

/**
 * Created by jamesrichardson on 10/23/15.
 */
@WebService(endpointInterface = "pisec.io.jaxws.IStatusQuery")
public class StatusQuery implements IStatusQuery {
    @Override
    public String getStatus() {
        return XMLSerializer.Serialize();
    }
}
