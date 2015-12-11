package pisec.security.auth;

import java.util.function.Consumer;

/**
 * Created by jamesrichardson on 10/15/15.
 */
public class AuthRequest {
    private Token token = null;
    private Consumer<Void> action = null;

    public AuthRequest(Token token, Consumer<Void> action){
        this.token = token;
        this.action = action;
    }

    public Token getToken(){
        return token;
    }

    public Consumer<Void> getRequest(){
        return action;
    }
}
