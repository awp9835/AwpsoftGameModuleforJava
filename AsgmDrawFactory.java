package awpsoft.gamemodule;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.CharBuffer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;
import javax.imageio.*;
import javax.swing.*;
import java.awt.geom.AffineTransform;

public class AsgmDrawFactory extends JFrame
{
    protected int ZoomWidth, ZoomHeight; 
    protected int RefreshRate;
    protected float GlobalXScale, GlobalYScale;
    protected float LastWScale, LastHScale, LastRotation;
    protected JPanel DrawPanel;
    protected Rectangle DefaultBounds;
    protected Timer RefreshReadyTimer;
    protected Image SwapChainBuffer;
    protected Graphics2D SwapChainGraphics;
    protected AffineTransform DefaultAffine;
    protected volatile boolean RefreshReady;
    protected int MaxWordLen = 18;
    public  int setMaximumExtendWordLength(int len)
    {
        if(len < 0) len = 0;
        if(len > 32) len = 32;
        MaxWordLen = len;
        return len;
    }
    public int setLimitFrequency(int freq)
    {
        if(freq < 1) freq = 1;
        int divide = 1000 / freq;
        if(divide < 1) divide = 1;
        RefreshReadyTimer.cancel();
        RefreshReadyTimer = new Timer();
        RefreshReadyTimer.schedule(new TimerTask(){public void run(){RefreshReady = true;}}, 0, divide);
        RefreshRate = 1000 / divide;
        return RefreshRate;
    }
    @Override public void update(Graphics g) 
    {
    }
    @Override public void paint(Graphics g) 
    {
    }
   
    public void beginDraw() 
    {
    }
    public void endDraw() 
    {
        while(true) 
        {
            if(RefreshReady) break;
            try{Thread.sleep(1);} catch(Exception ex){} //average 1/2 s
            if(RefreshReady) break;
            try{Thread.sleep(2);} catch(Exception ex){} //average 1s
            if(RefreshReady) break;
            try{Thread.sleep(1);} catch(Exception ex){} //average 1/2 s
            //total average = 2/3 s
        }
        RefreshReady = false;
        Graphics2D g = (Graphics2D)(DrawPanel.getGraphics());
        g.scale(GlobalXScale, GlobalYScale);
        g.drawImage(SwapChainBuffer, 0, 0,null);
    }
    public void clearResidual()
    {
        AffineTransform temp = SwapChainGraphics.getTransform();
        SwapChainGraphics.setTransform(DefaultAffine);
        SwapChainGraphics.clearRect(0,0,ZoomWidth,ZoomHeight);
        SwapChainGraphics.setTransform(temp);
    }
    public void drawStep(DrawParameters params) {drawStep(params, false);}
    public void drawStep(DrawParameters params, boolean forceTransform)
    {
        if (forceTransform || (params.Visible && params.Image != null))
		{
            SwapChainGraphics.setTransform(DefaultAffine);
			SwapChainGraphics.translate(params.PosCenterX, params.PosCenterY);
            SwapChainGraphics.scale(params.WScale, params.HScale);
            SwapChainGraphics.rotate(params.RotationDEG / 180.0f * (float)(Math.PI));
		}
		if (!params.Visible || params.Image == null) return;
        AffineTransform tempat = new AffineTransform();
        tempat.translate(-params.PicCenterX , -params.PicCenterY);
		SwapChainGraphics.drawImage(params.Image, tempat, null);
    }
    public void drawTextStep(TextParameters params) {drawTextStep(params, true);}
    public void drawTextStep(TextParameters params, boolean resetTransform)
    {
        if (!params.Visible) return;
		if (!C.IF(params.StrBuffer) || 0 == params.StrLength) return;
		if (null == params.TextFormat) return;
		params.ColorA *= params.SecondaryAlpha;
		if (resetTransform)	SwapChainGraphics.setTransform(DefaultAffine); 
        String s = params.toString();
        if(s.length() == 0) return;
        s = s.substring(0, params.StrLength);
        SwapChainGraphics.setFont(params.TextFormat);
        Color c1 = SwapChainGraphics.getColor();
        Color c2 = new Color(params.ColorR, params.ColorG, params.ColorB, params.ColorA);
        if(!c2.equals(c1)) SwapChainGraphics.setColor(c2);
        else c2 = null;
		

        FontMetrics fm = SwapChainGraphics.getFontMetrics();
        float maxw = params.XRight - params.XLeft;
        float dy = fm.getHeight();
        float y = params.YTop + fm.getAscent() + fm.getLeading();
        int len = params.StrLength, start = 0, over;
        float linew;  
        while(start < len) 
        {
            over = 0;
            linew = 0.0f;
            while(true)
            {     
                char c = s.charAt(start + over);
                linew += fm.charWidth(c);
                if(linew + fm.charWidth(c) > maxw - 0.5f * fm.getHeight())  
                {
                    if(c == '\n' || Character.isWhitespace(c)) over ++;
                    else 
                    {
                        int cnt = 0;
                        while(over == 0 || c == ','|| c == '.'|| c == '?'||c == ';'||c == ':'||
                        c == '!'||c == '，'||c == '。'||c == '？'||c == '；'||
                        c == '：'||c == '、'||c == '！'||c == '%'||c == '℃'||c == '％'||
                        c == ')'||c == '}'||c == ']'||c == '）'||c == '｝'||
                        c == '】'||c == '”'||c == '》'|| c == '〉'||c == '］'||
                        c == '〗'||c == '〕'||c == '」'||c == '』'||c == '’'||c == '\''
                        ||Character.isLowerCase(c)||Character.isUpperCase(c)
                        ||Character.isDigit(c)
                        ) 
                        {
                            
                            over ++; cnt++;
                            if(start + over >= len || cnt >= MaxWordLen) {linew += fm.charWidth(c); break;}
                            c = s.charAt(start + over);
                            linew += fm.charWidth(c);
                            if(c == '('||c == '{'||c == '['||c == '（'||c == '｛'||
                            c == '【'||c == '“'||c == '《'|| c == '〈'||c == '［'||
                            c == '〖'||c == '〔'||c == '「'||c == '『'||c == '‘') break;
                        }
                        linew -= fm.charWidth(c);
                        if(start + over < len && (s.charAt(start + over) == '\n' ||Character.isWhitespace(s.charAt(start + over)))) over++; //absorb next enter
                    }
                    String st = s.substring(start, start + over);
                    
                    c = st.charAt(over - 1);
                    if(c == '，'||c == '。'||c == '？'||c == '；'||c == '：'||c == '、'||c == '！'
                        ||c == '）'||c == '｝'||c == '】'||c == '》'|| c == '〉'||c == '］'||
                        c == '〗'||c == '〕'||c == '」'||c == '』'
                    ) linew -= fm.charWidth(c) * 0.5f;

                    
                    int lenline = (st.charAt(over - 1) == '\n'?(over - 1): over);
                    float linecw = 0.0f;
                    float dx = 0.0f;
                    if(over > 1 && 
                        (params.TextAlignment == TextAlignmentEnum.Trim 
                        || params.TextAlignment == TextAlignmentEnum.Stretch
                        ||linew > maxw
                        ))dx = (maxw - linew)/ (over - 1); 
                    else if(params.TextAlignment == TextAlignmentEnum.Right) linecw = maxw - linew;
                    else if(params.TextAlignment == TextAlignmentEnum.Center) linecw =  (maxw - linew) * 0.5f;
                    for(int i = 0; i < lenline; i++)
                    {
                        c = st.charAt(i);
                        SwapChainGraphics.drawString(Character.toString(c), params.XLeft + i * dx + linecw, y);
                        linecw += fm.charWidth(c);
                    }       
                    start += over;
                    y += dy;
                    break;
                }
                else if(c == '\n')
                {
                    String st = s.substring(start, start + over);

                    //linew = fm.stringWidth(st); // It's not correct
                    linew = 0.0f; for(int i = 0; i < over; i++) linew += fm.charWidth(st.charAt(i));
                    
                    switch(params.TextAlignment)
                    {
                    default:
                    case Stretch:
                        float linecw = 0.0f;
                        float dx = (over > 1)? ((maxw - linew) / (over - 1)):0.0f; 
                        for(int i = 0; i < over; i++)
                        {
                            c = st.charAt(i);
                            SwapChainGraphics.drawString(Character.toString(c), params.XLeft + i * dx + linecw, y);
                            linecw += fm.charWidth(c);
                        }
                        break;
                    case Trim:
                    case Left:
                        SwapChainGraphics.drawString(st, params.XLeft, y);
                        break;
                    case Right:
                        SwapChainGraphics.drawString(st, params.XRight - linew, y);
                        break;
                    case Center:
                        SwapChainGraphics.drawString(st, (params.XLeft + params.XRight - linew) / 2.0f, y);
                        break;
                    }
                    over++;
                    start += over;
                    y += dy;
                    break;
                }
                over++;
                if(start + over >= len) //start + over == len
                {
                    String st = s.substring(start, len);
                    
                    //linew = fm.stringWidth(st); // It's not correct
                    linew = 0.0f; for(int i = 0; i < over; i++) linew += fm.charWidth(st.charAt(i));

                    switch(params.TextAlignment)
                    {
                    default:
                    case Trim: 
                    case Left:
                        SwapChainGraphics.drawString(st, params.XLeft, y);
                        break;
                    case Stretch:
                        over = len - start;
                        float dx = (over > 1)? ((maxw - linew)/ (over - 1)): 0.0f; 
                        float linecw = 0.0f;
                        for(int i = 0; i < over; i++)
                        {
                            c = st.charAt(i);
                            SwapChainGraphics.drawString(Character.toString(c), params.XLeft + linecw + i * dx, y);
                            linecw += fm.charWidth(c);
                        }
                        break;
                    case Right:
                        SwapChainGraphics.drawString(st, params.XRight - linew, y);
                        break;
                    case Center:
                        SwapChainGraphics.drawString(st, (params.XLeft + params.XRight - linew) / 2.0f, y);
                        break;
                    }              
                    start = len;
                    break;
                }
            }
        }
        return;
    }


    public Vector<String> splitLines(String s, Font f, float maxWidth)
    {
        Vector<String> result = new Vector<String>();
		if (s == null) return result;
		if (f == null)
        {
            String[] sl = s.split("\\n");
            for(String st: sl) result.add(st);
            return result;
        } 
        int len = s.length();
        if(len == 0) return result;		

        FontMetrics fm = SwapChainGraphics.getFontMetrics(f);
        float maxw = maxWidth;
        int start = 0, over;
        float linew;  
        while(start < len) 
        {
            over = 0;
            linew = 0.0f;
            while(true)
            {     
                char c = s.charAt(start + over);
                linew += fm.charWidth(c);
                if(linew + fm.charWidth(c) > maxw - 0.5f * fm.getHeight())  
                {
                    if(c == '\n' || Character.isWhitespace(c)) over ++;
                    else 
                    {
                        int cnt = 0;
                        while(over == 0 || c == ','|| c == '.'|| c == '?'||c == ';'||c == ':'||
                        c == '!'||c == '，'||c == '。'||c == '？'||c == '；'||
                        c == '：'||c == '、'||c == '！'||c == '%'||c == '℃'||c == '％'||
                        c == ')'||c == '}'||c == ']'||c == '）'||c == '｝'||
                        c == '】'||c == '”'||c == '》'|| c == '〉'||c == '］'||
                        c == '〗'||c == '〕'||c == '」'||c == '』'||c == '’'||c == '\''
                        ||Character.isLowerCase(c)||Character.isUpperCase(c)
                        ||Character.isDigit(c)
                        ) 
                        {
                            
                            over ++; cnt++;
                            if(start + over >= len || cnt >= MaxWordLen) {linew += fm.charWidth(c); break;}
                            c = s.charAt(start + over);
                            linew += fm.charWidth(c);
                            if(c == '('||c == '{'||c == '['||c == '（'||c == '｛'||
                            c == '【'||c == '“'||c == '《'|| c == '〈'||c == '［'||
                            c == '〖'||c == '〔'||c == '「'||c == '『'||c == '‘') break;
                        }
                        linew -= fm.charWidth(c);
                        if(start + over < len && (s.charAt(start + over) == '\n' ||Character.isWhitespace(s.charAt(start + over)))) over++; //absorb next enter
                    }
                    String st = s.substring(start, start + over);
                    if(st.charAt(over - 1) != '\n') st += '\n';
                    result.add(st);
                    start += over;
                    break;
                }
                else if(c == '\n')
                {
                    String st = s.substring(start, start + over) + '\n';
                    result.add(st);
                    over++;
                    start += over;
                    break;
                }
                over++;
                if(start + over >= len) //start + over == len
                {
                    String st = s.substring(start, len);
                    result.add(st);            
                    start = len;
                    break;
                }
            }
        }
        return result;
    }    
    Image getSwapChainImage() {return SwapChainBuffer;}
    protected Image transToVolatileImage(Image img)
    {
        Image irt= DrawPanel.createVolatileImage(img.getWidth(null), img.getHeight(null));
        Graphics g = irt.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return irt;
    }
    public Image createImageFromMemoryBMP(byte[] buffer)
    {
        return createImageFromMemory(buffer);
    }
    public Image createImageFromMemoryJPG(byte[] buffer)
    {
        return createImageFromMemory(buffer);
    }
    public Image createImageFromMemoryPNG(byte[] buffer)
    {
        return createImageFromMemory(buffer);
    }
    public Image createImageFromMemory(byte[] buffer)
    {
        try
        {
            return transToVolatileImage(ImageIO.read(new ByteArrayInputStream(buffer)));
        }
        catch(Exception ex)
        {
            return null;
        }
    }
    public Image createImageFromPath(String path)
    {
        try
        {
            FileInputStream fips = new FileInputStream(path);
            byte[] buffer = fips.readAllBytes();
            fips.close();
            return ImageIO.read(new ByteArrayInputStream(buffer));
        }
        catch(Exception ex)
        {
            return null;
        }
    }
    public Vector<Image> createMultipleImagesFromMemoryGif(byte[] buffer)
    {
        Vector<Image> result = new Vector<Image>();
        try
        {
            ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
            reader.setInput(new ByteArrayInputStream(buffer));
            int num = reader.getNumImages(true);
            for(int i = 0; i < num; i++)
            {
                result.add(transToVolatileImage(reader.read(i)));
            }
            return result;
        }
        catch(Exception ex)
        {
            return result;
        }
    }
    public Vector<Image> createMultipleImagesFromGifPath(String path)
    {
        try
        {
            FileInputStream fips = new FileInputStream(path);
            byte[] buffer = fips.readAllBytes();
            fips.close();
            return createMultipleImagesFromMemoryGif(buffer);
        }
        catch(Exception ex)
        {
            return new Vector<Image>();
        }
    }

    public AsgmDrawFactory() 
    {
        this(1280, 720);
    }
    public AsgmDrawFactory(int width, int height)
    {
        super();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        GlobalXScale = 1.0f;
        GlobalYScale = 1.0f;
        RefreshRate = 60;
        ZoomWidth = width;
        ZoomHeight = height;
        setLayout(new BorderLayout());
        setSize(width, height);
        setBackground(Color.BLACK);
        setVisible(true);
        setSize(width * 2 - getContentPane().getWidth(), height * 2 - getContentPane().getHeight());
        setLocationRelativeTo(null); 
        DefaultBounds = getBounds();
        addComponentListener(new InnerComponentListener());
        DrawPanel = new JPanel();
        DrawPanel.setBackground(Color.BLACK);
        add(DrawPanel);

        ((Graphics2D)DrawPanel.getGraphics()).setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        ((Graphics2D)DrawPanel.getGraphics()).setRenderingHint(RenderingHints.KEY_INTERPOLATION,RenderingHints.VALUE_INTERPOLATION_BILINEAR);
       
        SwapChainBuffer = DrawPanel.createVolatileImage(width, height);
        SwapChainGraphics = (Graphics2D)SwapChainBuffer.getGraphics();
        SwapChainGraphics.setBackground(Color.BLACK);
        SwapChainGraphics.clearRect(0, 0, width, height);
        SwapChainGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        SwapChainGraphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        SwapChainGraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        DefaultAffine = SwapChainGraphics.getTransform();
        RefreshReady = true;
        RefreshReadyTimer = new Timer();
        RefreshReadyTimer.schedule(new TimerTask(){public void run(){RefreshReady = true;}}, 0, 16);
    }

    public AsgmDrawFactory(String title, int width, int height) 
    {
        this(width, height);
        setTitle(title);
    }

    public void stop()
    {
        RefreshReadyTimer.cancel();
        RefreshReady = true;
    }

    
    protected class InnerComponentListener implements ComponentListener
    {
        @Override
        public void componentResized(ComponentEvent e) 
        {
            int width = ZoomWidth;
            int height = ZoomHeight;
            if(width < 1) width = 1;
            if(height < 1) height = 1;
            GlobalXScale = (float)DrawPanel.getWidth() / width;
            GlobalYScale = (float)DrawPanel.getHeight() / height;  
        }
        @Override public void componentMoved(ComponentEvent e) {}
        @Override public void componentShown(ComponentEvent e) {}
        @Override public void componentHidden(ComponentEvent e) {}
    }

    public static class DrawParameters implements Cloneable
    {
        public boolean Visible;
        public float PosCenterX, PosCenterY, PicCenterX, PicCenterY, RotationDEG, SecondaryAlpha, WScale, HScale;
        public Image Image;
        private static DrawParameters Mother = new DrawParameters();
        public DrawParameters()
        {
            PosCenterX = PosCenterY = PicCenterX = PicCenterY = RotationDEG = 0.0f;
		    SecondaryAlpha = WScale = HScale = 1.0f;
		    Visible = false;
		    Image = null;
        }
        public DrawParameters(boolean init)
        {
            if(!init) return;
            PosCenterX = PosCenterY = PicCenterX = PicCenterY = RotationDEG = 0.0f;
		    SecondaryAlpha = WScale = HScale = 1.0f;
		    Visible = false;
		    Image = null;
        }
        public static DrawParameters newDrawParameters()
        {
            try 
            {
                return (DrawParameters)(Mother.clone());
            } 
            catch (CloneNotSupportedException e) 
            {
                return new DrawParameters();
            }
        }
    }
    public enum TextAlignmentEnum{Trim, Left, Center, Right, Stretch};
    public static class TextParameters implements Cloneable
    {
        public boolean Visible;
		public float SecondaryAlpha, XLeft, XRight, YTop;
		public CharBuffer StrBuffer;
        public int StrLength;
		public Font TextFormat;
		public float ColorR, ColorG, ColorB, ColorA;
        public TextAlignmentEnum TextAlignment;
        private static TextParameters Mother = new TextParameters();
		public TextParameters()
        {
            Visible = false;
		    SecondaryAlpha = 1.0f;
		    StrBuffer = null;
            StrLength = 0;
            ColorR = ColorG = ColorB = ColorA = 1.0f;
		    TextFormat = null;
		    XLeft = YTop = 0.0f;
		    XRight = 1280.0f;  
            TextAlignment = TextAlignmentEnum.Trim;    
        }
        public static TextParameters newDrawParameters() throws CloneNotSupportedException
        {
            return (TextParameters)(Mother.clone());
        }
        @Override public String toString()
        {
            if(StrBuffer == null) return "";
            return StrBuffer.toString().substring(0, StrLength);
        }
    }
}
