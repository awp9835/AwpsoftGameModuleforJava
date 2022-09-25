package awpsoft.gamemodule;

import java.util.concurrent.atomic.AtomicBoolean;

public class DoubleThreadLock
{
    private	volatile AtomicBoolean __Lock1, __Lock2, __RL1, __RL2;
	public	DoubleThreadLock()
    {
        __Lock1 = new AtomicBoolean(false);
        __Lock2 = new AtomicBoolean(false);
        __RL1 = new AtomicBoolean(false);
        __RL2 = new AtomicBoolean(false);
    }
	public	void lock1()
    {
        __RL1.set(true);;
		__Lock1.set(true);
		while (__Lock2.get())
		{
			if (__Lock1.get())
			{
				__Lock1.set(false);
				Thread.yield();
				__Lock1.set(true);
			}
		}
    }
	public	void lock2()
    {
        __RL2.set(true);;
		__Lock2.set(true);
		while (__Lock1.get())
		{
			if (__Lock2.get())
			{
				__Lock2.set(false);
				Thread.yield();
				__Lock2.set(true);
			}
		}
    }
	public	void unlock1()
    {
        __Lock1.set(false);
		__RL1.set(false);
    }
	public	void unlock2()
    {
        __Lock2.set(false);
		__RL2.set(false);
    }
	public	void waitUnlock1(){while (__RL1.get()) Thread.yield();}
	public	void waitUnlock2(){while (__RL2.get()) Thread.yield();}
};

