package awpsoft.gamemodule;
public class MovingGameObject2DAsgm extends GameObject2DAsgm
{
   public float VelocityX, VelocityY, AccelerX, AccelerY, OmegaDEG, EpsilonDEG;
   public long MoveTimeRemain;
   public MovingGameObject2DAsgm()
   {
        VelocityX = 0.0f;
        VelocityY = 0.0f;
        AccelerX = 0.0f;
        AccelerY = 0.0f;
        OmegaDEG = 0.0f;
        EpsilonDEG = 0.0f;
        MoveTimeRemain = -1;
   }
   @Override public boolean giveTime(int timeGived)
   {
        if (super.giveTime(timeGived)) return false;
		if (MoveTimeRemain > 0 && (long)timeGived > MoveTimeRemain)
		{
			timeGived = (int)MoveTimeRemain;
		}
		else if (MoveTimeRemain == 0)
		{
			return true;
		}
		MoveTimeRemain -= timeGived;
		PosCenterX += VelocityX * timeGived + AccelerX * (float)timeGived * (float)timeGived / 2.0f;
		PosCenterY += VelocityY * timeGived + AccelerY * (float)timeGived * (float)timeGived / 2.0f;
		RotationDEG += OmegaDEG * timeGived + EpsilonDEG * (float)timeGived * (float)timeGived / 2.0f;
		if (RotationDEG > 360.0f || RotationDEG < -360.0f)
		{
			RotationDEG -= (float)((long)(RotationDEG / 360.0f)) * 360.0f;
		}
		VelocityX += AccelerX * timeGived;
		VelocityY += AccelerY * timeGived;
		OmegaDEG += EpsilonDEG * timeGived;
		return true;
   }
   @Override public void reset()
   {
        super.reset();
        VelocityX = 0.0f;
        VelocityY = 0.0f;
        AccelerX = 0.0f;
        AccelerY = 0.0f;
        OmegaDEG = 0.0f;
        EpsilonDEG = 0.0f;
        MoveTimeRemain = -1;
   }
}
