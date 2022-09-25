package awpsoft.gamemodule;

public class EquivalentVoidPointer 
{
    public byte[] Buffer;
    public int Offset; 
    public EquivalentVoidPointer()
    {
        Buffer = null;
        Offset = 0;
    }
    public EquivalentVoidPointer(byte[] buffer)
    {
        Buffer = buffer;
        Offset = 0;
    }
    public EquivalentVoidPointer(byte[] buffer, int offset)
    {
        Buffer = null;
        Offset = offset;
    }
    public byte[] getBuffer() {return Buffer;}
    public void setBuffer(byte[] buffer) {Buffer = buffer;}
    public int getOffset() {return Offset;}
    public void setOffset(int offset) {Offset = offset;}
    public void skip(int numbytes) { Offset += numbytes;}
    public void back(int numbytes) {Offset -= numbytes;}
    public int getAvailableSize() {return Buffer.length - Offset;}

    public static void memcpy(EquivalentVoidPointer dst,EquivalentVoidPointer src, int size)
    {
        byte[] db = dst.Buffer, sb = src.Buffer;
        int dsto = dst.Offset, srco = src.Offset;
        for(int i = 0; i < size; i++) db[dsto + i] = sb[srco + i];
    }
    public static void memset(EquivalentVoidPointer dst,byte val, int size)
    {
        byte[] db = dst.Buffer;
        int dsto = dst.Offset;
        for(int i = 0; i < size; i++) db[dsto + i] = val;
    }
    public static int memcmp(EquivalentVoidPointer a,EquivalentVoidPointer b, int size)
    {
        byte[] ab = a.Buffer, bb = b.Buffer;
        int ao = a.Offset, bo = b.Offset;
        for(int i = 0; i < size; i++) 
        {
            int tmp = (int)ab[ao + i] - (int)bb[bo + i];
            if(tmp != 0) return tmp;
        }
        return 0;
    }
    @Override public EquivalentVoidPointer clone() 
    {
        try 
        {
            return (EquivalentVoidPointer)super.clone();
        } 
        catch (CloneNotSupportedException e) 
        {
            return new EquivalentVoidPointer(Buffer, Offset);
        }
    }
}
