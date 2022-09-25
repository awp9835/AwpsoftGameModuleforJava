package awpsoft.gamemodule;
import java.awt.Image;
import awpsoft.gamemodule.AsgmDrawFactory.*;
public class GameObject2DAsgm extends GameObject2D
{
    protected Image Image; 
	public GameObject2DAsgm() {Image = null;}
	public void setImage(Image img){Image = img;}
    public DrawParameters getDrawParameters()
    {
        DrawParameters tmp = new DrawParameters();
		if (!Enable) return tmp;
		tmp.Image = Image;
		if (null != tmp.Image) return tmp;
		tmp.Visible = Visible;
		tmp.PosCenterX = PosCenterX;
		tmp.PosCenterY = PosCenterY;
		tmp.PicCenterX = PicCenterX;
		tmp.PicCenterY = PicCenterY;
		tmp.RotationDEG = RotationDEG;
		tmp.WScale = WScale;
		tmp.HScale = HScale;
		tmp.SecondaryAlpha = SecondaryAlpha;
		return tmp;
    }
    public void draw(AsgmDrawFactory drawFactory)
    {
        if (!Enable || !Visible) return;
		drawFactory.drawStep(getDrawParameters());
    }
    @Override public void reset()
    {
        super.reset();
		Image = null;
    }
}
