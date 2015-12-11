package pisec.io.jaxws;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * Created by jamesrichardson on 10/23/15.
 */
@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface IStatusQuery {

    @WebMethod
    String getStatus();
}
