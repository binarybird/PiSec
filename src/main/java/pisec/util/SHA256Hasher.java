package pisec.util;

import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;

/**
 * Created by jamesrichardson on 10/30/15.
 */
public class SHA256Hasher {

    public static String hash(File file){
        String ret = "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            FileInputStream fis = new FileInputStream(file);

            byte[] dataBytes = new byte[1024];

            int nread = 0;
            while ((nread = fis.read(dataBytes)) != -1) {
                md.update(dataBytes, 0, nread);
            }

            byte[] mdbytes = md.digest();

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }

            ret = sb.toString();

        }catch(Exception e){
            e.printStackTrace();
        }

        return ret;
    }
}
