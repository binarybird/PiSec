package pisec.util;

/**
 * Created by jamesrichardson on 9/12/15.
 */
public class Flags
{

    /**
     * Contains the current set flag(s)
     */
    private static int CURRENT_FLAG = 0;

    /**
     *Set if this app is running on the raspberry pi
     */
    public static final int ONRASPI = 1;

    public static final int TESTMODE = 2;

    public static final int DEBUG = 4;

    public static final int UNCLAIMED2 = 8;

    public static final int UNCLAIMED3 = 16;

    public static final int UNCLAIMED4 = 32;

    public static final int UNCLAIMED5 = 64;

    public static final int UNCLAIMED6 = 128;

    public static final int UNCLAIMED7 = 256;

    public static final int UNCLAIMED8 = 512;

    public static final int UNCLAIMED9 = 1024;

    private static final int UNCLAIMED10 = 2048;

    private static final int UNCLAIMED11 = 4096;

    private static final int UNCLAIMED12 = 8192;
    /**
     * Contains the sum of all flags
     */
    public static final int ALL_FLAGS = 16383;

    /**
     * @param args The flags to test against
     *
     * @return Boolean weather the flag is set or not
     */
    public static boolean IsSet(int args) {
        if (args == 0)
            return false;
        return ((args & CURRENT_FLAG) == args);
    }

    /**
     * @param args The flags to be set
     */
    public static void SetFlag(int args) {
        if (args == 0)
            return;

        if (CURRENT_FLAG == 0)
            CURRENT_FLAG = args;
        else
            CURRENT_FLAG = CURRENT_FLAG ^ args;
    }

    //just for semantics
    public static void UnSetFlag(int args) {
        if(Flags.IsSet(args))
            SetFlag(args);
    }

    /**
     * Resets all flags
     */
    public static void ResetFlags() {
        CURRENT_FLAG = 0;
    }

    public static int GetAllSetFlags() {
        return new Integer(CURRENT_FLAG);
    }


}
