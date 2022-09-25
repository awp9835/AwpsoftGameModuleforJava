package awpsoft.gamemodule;

public class GameObject2D extends TimeVariantObject
{
    public boolean Enable, Visible, WillDestory, ExistLifeTime;
    public float PosCenterX, PosCenterY, PicCenterX, PicCenterY, RotationDEG, SecondaryAlpha, WScale, HScale;
    public long LifeTime;	
    public GameObject2D()
    {
		WillDestory = ExistLifeTime = false;
        Visible = true;
		Enable = true;
		PosCenterX = PosCenterY = PicCenterX = PicCenterY = RotationDEG = 0.0f;
		SecondaryAlpha = WScale = HScale = 1.0f;
		LifeTime = Long.MAX_VALUE;
    }
    @Override public boolean giveTime(int timeGived)
    {
        if (!super.giveTime(timeGived) || !Enable) return false;
		if (ExistLifeTime)
		{
			LifeTime -= timeGived;
			if (LifeTime <= 0)
			{
				Enable = false;
				WillDestory = true;
			}
		}
		return true;
    }
    @Override public void reset()
    {
		WillDestory = ExistLifeTime = false;
		PosCenterX = PosCenterY = PicCenterX = PicCenterY = RotationDEG = 0.0f;
		SecondaryAlpha = WScale = HScale = 1.0f;
		Visible = true;
		Enable = true;
		LifeTime = Long.MAX_VALUE;
    }
};

