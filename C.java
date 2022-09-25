package awpsoft.gamemodule;

import java.nio.Buffer;

public class C 
{
    public static boolean IF(Object ptr)
    {
        if(ptr == null) return false;
        else if(ptr instanceof Boolean) return ((Boolean)ptr).booleanValue();
        else if(ptr instanceof EquivalentPointer<?>) return ((EquivalentPointer<?>)ptr).Buffer != null;
        else if(ptr instanceof Number) return ((Number)ptr).longValue()!=0 || ((Number)ptr).doubleValue()!=0 ;
        else if(ptr instanceof Buffer) return ((Buffer)ptr).hasArray()||((Buffer)ptr).isReadOnly();
        else return true;
    }
    public static boolean IF(byte b)    {return b!=0;}
    public static boolean IF(char c)    {return c!=0;}
    public static boolean IF(boolean b) {return b;}
    public static boolean IF(short s)   {return s!=0;}
    public static boolean IF(float f)   {return f!=0.0f;}
    public static boolean IF(int i)     {return i!=0;}
    public static boolean IF(double d)  {return d!=0.0;}
    public static boolean IF(long l)    {return l!=0L;}
    
    public static boolean IF(byte[] ptr)    {return ptr != null;}
    public static boolean IF(char[] ptr)    {return ptr != null;}
    public static boolean IF(boolean[] ptr) {return ptr != null;}
    public static boolean IF(short[] ptr)   {return ptr != null;}
    public static boolean IF(float[] ptr)   {return ptr != null;}
    public static boolean IF(int[] ptr)     {return ptr != null;}
    public static boolean IF(double[] ptr)  {return ptr != null;}
    public static boolean IF(long[] ptr)    {return ptr != null;}
    public static boolean IF(Object[] ptr)  {return ptr != null;}
}
