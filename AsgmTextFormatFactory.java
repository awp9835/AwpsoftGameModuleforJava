package awpsoft.gamemodule;
import java.awt.Font;
import java.io.File;
public class AsgmTextFormatFactory
{
    public Font createTextFormat(String nameorPath, float fontSize)
    {
        return createFont(nameorPath, fontSize, false, false);
    }
    public Font createTextFormat(String nameorPath, float fontSize, boolean bold)
    {
        return createFont(nameorPath, fontSize, bold, false);
    }
    public Font createTextFormat(String nameorPath, float fontSize, boolean bold , boolean italic)
    {
        return createFont(nameorPath, fontSize, bold, italic);
    }
    public static Font createFont(String nameorPath, float fontSize)
    {
        return createFont(nameorPath, fontSize, false, false);
    }
    public static Font createFont(String nameorPath, float fontSize, boolean bold)
    {
        return createFont(nameorPath, fontSize, bold, false);
    }
    public static Font createFont(String nameorPath, float fontSize, boolean bold , boolean italic)
    {
        Font f;
        try
        {
            f = Font.createFont(0, new File(nameorPath)).deriveFont(fontSize);
        }
        catch(Exception ex)
        {
            f = Font.decode(nameorPath).deriveFont(fontSize);
        }
        int style = Font.PLAIN;
        if(bold) style |= Font.BOLD;
        if(italic) style |= Font.ITALIC;
        if(style == 0) return f;
        else return f.deriveFont(style);
    }
    public static boolean isDefaultFont(Font font)
    {
        return  font.getFamily().equals("Dialog") || font.getName().equals("Dialog");
    }
    public boolean isDefaultTextFormat(Font font) {return isDefaultFont(font);}
};