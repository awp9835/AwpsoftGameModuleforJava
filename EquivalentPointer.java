package awpsoft.gamemodule;
public class EquivalentPointer<T> implements Cloneable
{
    public T[] Buffer;
    public int Index; 
    public EquivalentPointer()
    {
        Buffer = null;
        Index = 0;
    }
    public EquivalentPointer(T[] buffer)
    {
        Buffer = buffer;
        Index = 0;
    }
    public EquivalentPointer(T[] buffer, int index)
    {
        Buffer = null;
        Index = index;
    }
    @SuppressWarnings("unchecked")
    @Override public EquivalentPointer<T> clone()
    {
        try 
        {
            return (EquivalentPointer<T>)super.clone();
        } 
        catch (CloneNotSupportedException e) 
        {
            return new EquivalentPointer<T>(Buffer, Index);
        }
    }
} 