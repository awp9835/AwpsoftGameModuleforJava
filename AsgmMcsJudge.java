package awpsoft.gamemodule;

public class AsgmMcsJudge
{
    public static class RelativeCircle implements Cloneable
	{
		//Using left-hand-polar coordinate system
		float CenterDistance;	//Distance from center of this circle to origin
		float ThetaDEG;	//Polar angle
		float Radius; //Radius of this circle
        public RelativeCircle(){}
        @Override public RelativeCircle clone()
        {
            try 
            {
                return (RelativeCircle)super.clone();
            } 
            catch (CloneNotSupportedException e) 
            {
                RelativeCircle tmp = new RelativeCircle();
                tmp.CenterDistance = CenterDistance;
                tmp.ThetaDEG = ThetaDEG;
                tmp.Radius = Radius;
                return tmp;
            }
        }
        public static RelativeCircle getFrom(float[] base, int offset)
        {
            RelativeCircle tmp = new RelativeCircle();
            tmp.CenterDistance = base[offset];
            tmp.ThetaDEG = base[offset + 1];
            tmp.Radius = base[offset + 2];
            return tmp;
        }
        public void setTo(float[] base, int offset)
        {
            base[offset] = CenterDistance;
            base[offset + 1] = ThetaDEG;
            base[offset + 2] = Radius;
        }
	};

	/*function*/
	public static boolean AsgmJudgeAbsoluteCircles
    (
		float x1, float y1, float r1,
		float x2, float y2, float r2
	)
    {
        return (x2 - x1)*(x2 - x1) + (y2 - y1)*(y2 - y1) < (r1 + r2)*(r1 + r2);
    }

	public static boolean AsgmJudgeRelativeCircles(
		float originX1, float originY1,
		float centerOfRotationDEG1, RelativeCircle circle1,
		float originX2, float originY2,
		float centerOfRotationDEG2, RelativeCircle circle2
	)
    {
        return AsgmJudgeAbsoluteCircles(
			originX1 + circle1.CenterDistance * AsgmMath.cosD(circle1.ThetaDEG + centerOfRotationDEG1),
			originY1 + circle1.CenterDistance * AsgmMath.sinD(circle1.ThetaDEG + centerOfRotationDEG1),
			circle1.Radius,
			originX2 + circle2.CenterDistance * AsgmMath.cosD(circle2.ThetaDEG + centerOfRotationDEG2),
			originY2 + circle2.CenterDistance * AsgmMath.sinD(circle2.ThetaDEG + centerOfRotationDEG2),
			circle2.Radius
        );
    }
	public static int AsgmJudgeMultipleRelativeCirclesCNT(
		float originX1, float originY1, float centerOfRotationDEG1,
		EquivalentPointer<RelativeCircle> circleArray1, int circleCount1,
		float originX2, float originY2, float centerOfRotationDEG2,
		EquivalentPointer<RelativeCircle> circleArray2,  int circleCount2
	)
    {
        int cnt = 0;
		for (int i = 0; i < circleCount1; i++)
		{
			for (int j = 0; j < circleCount2; j++)
			{
				cnt += AsgmJudgeRelativeCircles(
					originX1, originY1,
					centerOfRotationDEG1, circleArray1.Buffer[circleArray1.Index + i],
					originX2, originY2,
					centerOfRotationDEG2, circleArray2.Buffer[circleArray2.Index + j]
				)?1:0;
			}
		}
		return cnt;
    }
	public static boolean AsgmJudgeMultipleRelativeCircles(
		float originX1, float originY1, float centerOfRotationDEG1,
		EquivalentPointer<RelativeCircle> circleArray1,  int circleCount1,
		float originX2, float originY2, float centerOfRotationDEG2,
		EquivalentPointer<RelativeCircle> circleArray2,  int circleCount2
	)
    {
        for (int i = 0; i < circleCount1; i++)
		{
			for (int j = 0; j < circleCount2; j++)
			{
				if (AsgmJudgeRelativeCircles(
						originX1, originY1,
						centerOfRotationDEG1, circleArray1.Buffer[circleArray1.Index + i],
						originX2, originY2,
						centerOfRotationDEG2, circleArray2.Buffer[circleArray2.Index + j]
					))
				{
					return true;
				}
			}
		}
		return false;
    }
}
