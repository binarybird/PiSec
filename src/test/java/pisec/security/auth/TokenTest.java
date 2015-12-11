package pisec.security.auth;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by jamesrichardson on 10/30/15.
 */
public class TokenTest {

    @Test
    public void testGetTokenID(){
        final Token token = Token.GenerateToken();
        Assert.assertNotNull(token.getTokenID());
        Assert.assertNotEquals(token.getTokenID(),"");

    }

    @Test
    public void testGetUserData(){
        final Token token = Token.GenerateToken();
        Object o = new Object();
        token.setUserData(o);
        Assert.assertEquals(token.getUserData(),o);
    }

}
