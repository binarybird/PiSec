package pisec.security.auth;

import java.util.UUID;

/**
 * Created by jamesrichardson on 10/15/15.
 */
public class Token {
    private String uuid = null;
    private Object userData = null;

    private Token(String uuid){
        this.uuid = uuid;
    }

    public String getTokenID(){return uuid;}

    public Object getUserData(){return userData;}
    public void setUserData(Object userData){this.userData=userData;}

    public static Token GenerateToken(){
        return new Token(UUID.randomUUID().toString());
    };
}
