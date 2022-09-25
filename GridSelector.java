package awpsoft.gamemodule;

public class GridSelector 
{
    public enum SelectorMenuStyle
	{
		MNSTYLE_V,
		MNSTYLE_H,
		MNSTYLE_VL,
		MNSTYLE_HL,
		MNSTYLE_GR
	};
	private int primaryAdd()
    {
        int i, s, t;
		i = CurrentSelect & 0x0000FFFF;
		s = CurrentSelect & 0xFFFF0000;
		switch (MenuStyle)
		{
		case MNSTYLE_H:
		case MNSTYLE_V:
		case MNSTYLE_HL:
		case MNSTYLE_VL:
			while (i < PKinds - 1)
			{
				i++;
				t = UnitAttribute[flatten(i, 0)];
				if (0 != (t & 0xF0000000) && 0 != (t & 0x0F000000))
				{
					CurrentSelect = i;
					return CurrentSelect;
				}
			}
			break;
		case MNSTYLE_GR:
			while (i < PKinds - 1)
			{
				i++;
				t = UnitAttribute[flatten(i, s >>> 16)];
				if (0 != (t & 0xF0000000) && 0 != (t & 0x0F000000))
				{
					CurrentSelect = i | s;
					return CurrentSelect;
				}
			}
			break;
		default:
			break;
		}
		return CurrentSelect;
    }
	private int primarySub()
    {
        int i, s, t;
		i = CurrentSelect & 0x0000FFFF;
		s = CurrentSelect & 0xFFFF0000;
		switch (MenuStyle)
		{
		case MNSTYLE_H:
		case MNSTYLE_V:
		case MNSTYLE_HL:
		case MNSTYLE_VL:
			while (i > 0)
			{
				i--;
				t = UnitAttribute[flatten(i, 0)];
				if (0 != (t & 0xF0000000) && 0 != (t & 0x0F000000))
				{
					CurrentSelect = i;
					return CurrentSelect;
				}
			}
			break;
		case MNSTYLE_GR:
			while (i > 0)
			{
				i--;
				t = getUnitAttribute(i, s >>> 16);
				if (0 != (t & 0xF0000000) && 0 != (t & 0x0F000000))
				{
					CurrentSelect = i | s;
					return CurrentSelect;
				}
			}
			break;
		default:
			break;
		}
		return CurrentSelect;
    }
	private int secondaryAdd()
    {
        int i, s, t;
		i = CurrentSelect & 0x0000FFFF;
		s = CurrentSelect & 0xFFFF0000;
		s >>>= 16;
		switch (MenuStyle)
		{
		case MNSTYLE_H:
		case MNSTYLE_V:
			return CurrentSelect;
		case MNSTYLE_HL:
		case MNSTYLE_VL:
		case MNSTYLE_GR:
			while (s < SKinds - 1)
			{
				s++;
				t = UnitAttribute[flatten(i, s)];
				if (0 != (t & 0xF0000000) && 0 != (t & 0x0F000000))
				{
					CurrentSelect = (s << 16) | i;
					return CurrentSelect;
				}
			}
			break;
		default:
			break;
		}
		return CurrentSelect;
    }
	private int secondarySub()
    {
        int i, s, t;
		i = CurrentSelect & 0x0000FFFF;
		s = CurrentSelect & 0xFFFF0000;
		s >>>= 16;
		switch (MenuStyle)
		{
		case MNSTYLE_H:
		case MNSTYLE_V:
			return CurrentSelect;
		case MNSTYLE_HL:
		case MNSTYLE_VL:
		case MNSTYLE_GR:
			while (s > 0)
			{
				s--;
				t = UnitAttribute[flatten(i, s)];
				if (0 != (t & 0xF0000000) && 0 != (t & 0x0F000000))
				{
					CurrentSelect = (s << 16) | i;
					return CurrentSelect;
				}
			}
			break;
		default:
			break;
		}
		return CurrentSelect;
    }
	
	protected SelectorMenuStyle MenuStyle;
	protected int CurrentSelect; // Secondary:&0xFFFF0000  Primary:&0x0000FFFF
	protected int PKinds, SKinds;
	protected int[] UnitAttribute;     // Valid:&0xFF000000 Unlock:&&0x00FF0000 Value:&0x0000FFFF
	protected int flatten(int index, int secondaryIndex)
    {
        return (int)((long)(index & 0x0000ffff) * (long)(SKinds & 0x0000ffff))  + (secondaryIndex & 0x0000ffff);
    }
	
	public static final int Attribute_Valid = 0xF0000000;
	public static final int Attribute_Unlock = 0x0F000000;
    public static final int Attribute_Enable = 0xFF000000;
	public GridSelector(SelectorMenuStyle style, int sorts, int secondarySorts)
    {
        MenuStyle = style;
		PKinds = 0x0000FFFF & sorts;
		SKinds = 0x0000FFFF & secondarySorts;
		CurrentSelect = 0x0;
		switch (style)
		{
		case MNSTYLE_V:
		case MNSTYLE_H:
			SKinds = 1;
			break;
		case MNSTYLE_HL:
		case MNSTYLE_VL:
		case MNSTYLE_GR:
			break;

		}
        int len = PKinds * SKinds;
		UnitAttribute = new int[ PKinds * SKinds];
        for(int i =0; i<len; i++) UnitAttribute[i] = 0xff000000;

    }
    public GridSelector(SelectorMenuStyle style, int sorts)
    {
        MenuStyle = style;
		PKinds = 0x0000ffff & sorts;
		SKinds = 1;
		CurrentSelect = 0x0;
		switch (style)
		{
		case MNSTYLE_V:
		case MNSTYLE_H:
			SKinds = 1;
			break;
		case MNSTYLE_HL:
		case MNSTYLE_VL:
		case MNSTYLE_GR:
			break;

		}
        int len = PKinds * SKinds;
		UnitAttribute = new int[ PKinds * SKinds];
        for(int i =0; i < len; i++) UnitAttribute[i] = 0xFF000000;
    }


    /* Valid:&0xF0000000 Unlock:&&0x0F000000 Value:&0x00FFFFFF */
    public void setUnitAttribute(int attribute, int index){setUnitAttribute(attribute, index, 0);}
    public void setUnitAttribute(int attribute, int index, int secondaryIndex)
    {
        index = Math.min(0x0000FFFF & index, PKinds - 1);
		secondaryIndex = Math.min(0x0000FFFF & secondaryIndex, SKinds - 1);
		UnitAttribute[flatten(index, secondaryIndex)] = attribute;
    }
    public void setCurrentAttribute(int attribute)
    {		
		UnitAttribute[flatten(CurrentSelect, CurrentSelect >>> 16)] = attribute;
    }
    public int getUnitAttribute(int index){return getUnitAttribute(index, 0);}
    public int getUnitAttribute(int index, int secondaryIndex)
    {
        index = Math.min(0x0000FFFF & index, PKinds - 1);
		secondaryIndex = Math.min(0x0000FFFF & secondaryIndex, SKinds - 1);
		return UnitAttribute[flatten(index, secondaryIndex)];
    }
    public int getCurrentSelectIndex(){return CurrentSelect & 0x0000FFFF;}
    public int getCurrentSelectSecondaryIndex(){return CurrentSelect >>> 16;}
	public void setCurrentSelectIndex(int index){setCurrentSelectIndex(index, 0);}
    public void setCurrentSelectIndex(int index, int secondaryIndex)
    {
        index = Math.min(0x0000FFFF &index, PKinds - 1);
		secondaryIndex = Math.min(0x0000FFFF &secondaryIndex, SKinds - 1);
		CurrentSelect = (secondaryIndex << 16) | index;
    }
	public int getCurrentSelectAttribute()
    {
		return UnitAttribute[flatten(CurrentSelect, CurrentSelect >>> 16)];
	}
	public int up()
    {
		switch (MenuStyle)
		{
		case MNSTYLE_H:
			return CurrentSelect;
		case MNSTYLE_V:
		case MNSTYLE_VL:
		case MNSTYLE_GR:
			return primarySub();
		case MNSTYLE_HL:
			return secondarySub();
		default:
			break;
		}
		return CurrentSelect;
	}
	public int down()
    {
		switch (MenuStyle)
		{
		case MNSTYLE_H:
			return CurrentSelect;
		case MNSTYLE_V:
		case MNSTYLE_VL:
		case MNSTYLE_GR:
			return primaryAdd();
		case MNSTYLE_HL:
			return secondaryAdd();
		default:
			break;
		}
		return CurrentSelect;
	}
	public int left()
    {
		switch (MenuStyle)
		{
		case MNSTYLE_V:
			return CurrentSelect;
		case MNSTYLE_H:
		case MNSTYLE_HL:
			return primarySub();
		case MNSTYLE_GR:
		case MNSTYLE_VL:
			return secondarySub();
		default:
			break;
		}
		return CurrentSelect;
	}
	public int right()
    {
		switch (MenuStyle)
		{
		case MNSTYLE_V:
			return CurrentSelect;
		case MNSTYLE_H:
		case MNSTYLE_HL:
			return primaryAdd();
		case MNSTYLE_GR:
		case MNSTYLE_VL:
			return secondaryAdd();
		default:
			break;
		}
		return CurrentSelect;
	}
}
