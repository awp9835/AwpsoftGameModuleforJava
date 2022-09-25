package awpsoft.gamemodule;

public class TimeVariantObject 
{
    public long TimeRemain;
	public boolean DisableTimeVariant;


	public TimeVariantObject()
    {
        DisableTimeVariant = false;
		TimeRemain = 0;
    }
	public void clearTimeRemain()
    {
        TimeRemain = 0;
    }
	public void reset()
    {
        DisableTimeVariant = false;
		TimeRemain = 0;
    }
	public boolean giveTime(int timeGived)
    {
        if (DisableTimeVariant) return false;
		if (timeGived < 0) return false;
		TimeRemain += timeGived;
		return true;
    }

    public static class TimerTrigger extends TimeVariantObject
	{
        public boolean Ready;
		public int Cycle;
        public TimerTrigger()
        {
            Cycle = 1;
            Ready = false;
        }
		public TimerTrigger(int timerCycle)
        {
            if (timerCycle < 0) timerCycle = 0;
            Cycle = timerCycle;
            Ready = false;
        }
		public void clearStates()
        {
            TimeRemain = 0;
            Ready = false;
        }
		public boolean tryTriggerOnce()
        {
            if (Ready)
            {
                Ready = false;
                return true;
            }
            return false;
        }
		@Override public void reset() {clearStates();}
		@Override public boolean giveTime(int timeGived)
        {
            if (super.giveTime(timeGived)) return false;
            if (Cycle == 0)
            {
                TimeRemain = 0;
                Ready = true;
                return true;
            }
            if (TimeRemain >= Cycle) 
            {
                TimeRemain = TVOSafeModInt64(TimeRemain, Cycle);
                Ready = true;
                return true;
            }
            return true;
        }
	};
	public static class TimerClip extends TimeVariantObject
	{
        public int Charged;
        public int MaxCharge;
		public int Cycle;
        public TimerClip()
        {
            Cycle = 1;
            MaxCharge = 1;
            Charged = 0;
        }
        public TimerClip(int maximumCharge)
		{
            if (maximumCharge < 0) maximumCharge = 0;
            MaxCharge = maximumCharge;
            Cycle = 1;
            Charged = 0;
        }
        public TimerClip(int maximumCharge, int timerCycle)
        {
            if (timerCycle < 0) timerCycle = 0;
            if (maximumCharge < 0) maximumCharge = 0;
            Cycle = timerCycle;
            MaxCharge = maximumCharge;
            Charged = 0;
        }
		public void clearStates()
        {
            TimeRemain = 0;
            Charged = 0;
        }
		public boolean tryConsume(int count)
        {
            if (count <= 0) return true;
            if (Charged >= count)
            {
                Charged -= count;
                return true;
            }
            return false;
        }
        public boolean tryConsume()
        {
            if (Charged >= 1)
            {
                Charged -= 1;
                return true;
            }
            return false;
        }
		public int tryConsumePart(int count)
        {
            if (count <= 0) return 1;
            int TempCharged = Charged;
            count = Math.min(TempCharged, count);
            Charged -= count;
            return count;
        }
        public int tryConsumePart()
        {
            int TempCharged = Charged;
            Charged -= Math.min(TempCharged, 1);
            return 1;
        }
		@Override public void reset() {clearStates();}
		@Override public boolean giveTime(int timeGived)
        {
            if (Charged >= MaxCharge)
            {
                TimeRemain = 0;
                return false;
            }
            if (!super.giveTime(timeGived)) return false;
            if (Cycle == 0 && Charged < MaxCharge)
            {
                Charged = MaxCharge;
                TimeRemain = 0;
                return true;
            }
            int ChargeInc = (int)TVOSafeDivInt64(TimeRemain, Cycle);
            if (ChargeInc == 0) return false;
            if (Charged + ChargeInc >= MaxCharge || Charged + ChargeInc < 0)
            {
                Charged = MaxCharge;
                TimeRemain = 0;
                return true;
            }
            Charged += ChargeInc;
            TimeRemain = TVOSafeModInt64(TimeRemain, Cycle);
            return true;
        }
	};


    private static long TVOSafeModInt64(long a, long b)
	{
		if (b == 0L) return 0L;
		return a % b;
	}
	private static long TVOSafeDivInt64(long a, long b)
	{
		if (b == 0L)
		{
			if (a > 0L) return Long.MAX_VALUE;
			if (a == 0L) return 1;
			if (a < 0L) return Long.MIN_VALUE;
		}
		return a / b;
	}
}
