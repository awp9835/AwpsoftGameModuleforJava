package awpsoft.gamemodule;

public class AsgmMath
{
    private static float[] MATHLIST_COSINEDEG;
    private static float[] MATHLIST_SINEDEG;
    static 
    {
        InnerArrayInit();
    }
    private static void InnerArrayInit()
    {
        MATHLIST_COSINEDEG =  new float[36001];
        for(int i = 0; i <= 36000; i++)
        {
            MATHLIST_COSINEDEG[i] = (float)Math.cos(i / 18000.0 * Math.PI);
        }
        MATHLIST_SINEDEG = new float[36001];
        for(int i = 0; i <= 36000; i++)
        {
            MATHLIST_SINEDEG[i] = (float)Math.sin(i / 18000.0 * Math.PI);
        }
    }

	public static final float[] PentagramX = { 0.0f, 0.0f, 0.9510565f, 0.5877852f, -0.5877852f, -0.9510565f }; 
	public static final float[] PentagramY = { 0.0f, -1.0f, -0.3090167f, 0.8090170f, 0.8090170f, -0.3090167f };

    public static float cosD(float thetaDEG)
	{
		if (thetaDEG >= 0.0f)
		{
			return MATHLIST_COSINEDEG[(int)(thetaDEG * 100 + 0.5) % 36000];
		}
		else
		{
			return MATHLIST_COSINEDEG[(int)(-thetaDEG * 100 + 0.5) % 36000];
		}
	};
	public static float sinD(float thetaDEG)
	{
		if (thetaDEG >= 0.0f)
		{
			return MATHLIST_SINEDEG[(int)(thetaDEG * 100 + 0.5) % 36000];
		}
		else
		{
			return -MATHLIST_SINEDEG[(int)(-thetaDEG * 100 + 0.5) % 36000];
		}
	};
	public static float arctanD(float x) //x:[-1, 1]
	{
		float u, v;
		u = x * x;
		v = 57.29578f * x * (1.0f + (0.09234f * u - 0.30563f) * u);
		return v;
	}

	public static float sqrt1AddX(float x)//x:[0, 1]
	{
		return 1 + x * (0.497714f + x * (0.025346f * x - 0.10875f));
	}

	public static float directionD(float deltaX, float deltaY)
	{
		if (deltaX == 0.0f && deltaY == 0.0f) return -90.0f;
		else if (deltaX >= deltaY && deltaY >= -deltaX) return arctanD(deltaY / deltaX);
		else if (deltaX <= deltaY && deltaY <= -deltaX) return arctanD(deltaY / deltaX) + 180.0f;
		else if (deltaX < deltaY && -deltaX < deltaY) return 90.0f - arctanD(deltaX / deltaY);
		else return 270.0f - arctanD(deltaX / deltaY);
	}
	public static float distance(float deltaX, float deltaY)
	{
		float xabs = Math.abs(deltaX);
		float yabs = Math.abs(deltaY);
		float z;
		if (xabs == 0.0f && yabs == 0.0f) return 0.0f;
		if (xabs >= yabs)
		{
			z = yabs / xabs;
			return xabs * sqrt1AddX(z * z);
		}
		else
		{
			z = xabs / yabs;
			return yabs * sqrt1AddX(z * z);
		}
	}
	public static float distance(float deltaX, float deltaY, float deltaZ)
	{
		return distance(distance(deltaX, deltaY), deltaZ);
	}
	public static float distanceSquare(float deltaX, float deltaY)
	{
		return deltaX * deltaX + deltaY * deltaY;
	}
	public static float distanceSquare(float deltaX, float deltaY, float deltaZ)
	{
		return deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;
	}
};