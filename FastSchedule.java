package awpsoft.gamemodule;

import java.util.Vector;

public class FastSchedule<T> 
{
    
	protected Vector<T> ScheduleList;
	protected int Length, Pos, Occupied, Save;
	public FastSchedule(int len)
	{
		if (len < 0x10) len = 0x10;
		Length = len;
		Pos = Length - 1;
		Occupied = 0;
		Save = 0;
		ScheduleList = new Vector<T>(len);
        for(int i = 0; i < len ;i++) ScheduleList.add(null);
	}
	@Override public void finalize()
    {
        if(!ScheduleList.isEmpty()) 
        {
            clearAll();
            //System.gc();
        }
    }
	public Vector<T> getScheduleListReference(){return ScheduleList;}
	public int getLength(){return Length;}
	public boolean full(){return Occupied == Length;}
	public T gotoNextPos()
    {
        if (Pos + 1 >= Length)
		{
			Pos = 0;
		}
		else
		{
			Pos++;
		}
		return ScheduleList.get(Pos);
    }
	public int gotoNextNullPos()
	{
		if (Length - Occupied == 0) return -1;
		for (int i = 1; i <= Length; i++)
		{
			gotoNextPos();
			if (ScheduleList.get(Pos) == null) return Pos;
		}
		return -1;
	}
	public T getCurrentObject(){return ScheduleList.get(Pos);}
	public void clearAll()
    {
        int len = Length;
        for (int i = 0; i < len; i++)
		{
			ScheduleList.set(i, null);
		}
		Occupied = 0;
    }
	public void saveCurrentPos(){Save = Pos;}
	public void loadSavedPos(){Pos = Save;}
	public void clearCurrentObject()
    {		
        if (ScheduleList.get(Pos) != null)
		{
			//delete ScheduleList[Pos];
			ScheduleList.set(Pos, null);
			Occupied--;
		}
    }
	public int takeOverObject(T obj)
    {
        if (full())
		{
			obj = null;
			return -1;
		}
		if (obj == null) return Pos;
		Occupied++;
		ScheduleList.set(Pos, obj);
		gotoNextNullPos();
		return Pos;
    }
	public T takeOutCurrentObject()
    {
        T temp  = ScheduleList.get(Pos);
        ScheduleList.set(Pos, null);
        if(temp != null) Occupied--;
        return temp;
    }   
}
