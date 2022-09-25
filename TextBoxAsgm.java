package awpsoft.gamemodule;

import java.nio.CharBuffer;
import awpsoft.gamemodule.AsgmDrawFactory.*;
import java.awt.Font;
public class TextBoxAsgm extends MovingGameObject2DAsgm
{
    protected CharBuffer Text;
    protected int StrLength;

    public float TextLeftX, TextRightX, TextTopY, TypedLength, TypeSpeed;
    public boolean BoundAlpha, BoundTrasnform, UseRelativePos, TypingMode;
    public Font TextFormat;
    public float ColorR, ColorG, ColorB, ColorA;
    public TextBoxAsgm()
    {
        Text = null;
		StrLength = 0;
		ColorR = ColorG = ColorB = ColorA = 1.0f;
		TextFormat = null;
		TextLeftX = TextTopY = 0.0f;
		TextRightX = 1280.0f;
		TypingMode = false;
		TypedLength = 0.0f;
		TypeSpeed = 0.020f;
		BoundAlpha = false;
		BoundTrasnform = false;
		UseRelativePos = true;
    }
    public void skipTyping()
    {
        TypedLength = (float)StrLength;
    }
    public boolean isTypingComplete()
    {
		if (TypedLength + 0.5 >= (float)StrLength) return true;
		else return false;
	}
    public void setText(CharBuffer str)
    {
        Text = str; 
        if(str == null)  StrLength = 0;
        else StrLength = Text.length();
    }
    public void setText(String str)
    {
        if(str == null)
        {
            Text = null;
            StrLength = 0;
        }
        else
        {
            Text = CharBuffer.wrap(str.toCharArray());
            StrLength = Text.length();
        }
    }
    @Override public void reset() {if(TypingMode) TypedLength = 0;}
    @Override public boolean giveTime(int timeGived)
    {
        if (super.giveTime(timeGived)) return false;
        if (TypedLength < (float)StrLength)
            TypedLength += TypeSpeed * (float) timeGived;
        if (TypedLength > (float)StrLength)
            TypedLength = (float)StrLength;
        return true;
    }
    public TextParameters getTextParameters()
    {
        TextParameters temp = new TextParameters();
		if (!Enable) return temp;
		temp.Visible = Visible;
		if (!temp.Visible) return temp;
		temp.ColorR = ColorR;
        temp.ColorG = ColorG;
        temp.ColorB = ColorB;
        temp.ColorA = ColorA;
		temp.TextFormat = TextFormat;
		temp.StrBuffer = Text;
		temp.SecondaryAlpha = BoundAlpha ? SecondaryAlpha : 1.0f;
		temp.XLeft = UseRelativePos ? (TextLeftX + PosCenterX) : TextLeftX;
		temp.XRight = UseRelativePos ? (TextRightX + PosCenterX) : TextRightX;
		temp.YTop = UseRelativePos ? (TextTopY + PosCenterY) : TextTopY;
		temp.StrLength = (!TypingMode || isTypingComplete()) ? StrLength : (Math.max(0, (int)(TypedLength + 0.5)));
		return temp;
    }
    @Override public void draw(AsgmDrawFactory drawFactory)
    {
        if (!Enable || !Visible) return;
		if (BoundTrasnform)
		{
			drawFactory.drawStep(getDrawParameters(), true);
			drawFactory.drawTextStep(getTextParameters(), false);
		}
		else
		{
			drawFactory.drawStep(getDrawParameters(), false);
			drawFactory.drawTextStep(getTextParameters(), true);
		}
    } 
}
