package awpsoft.gamemodule;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class FunctionToKey implements KeyListener
{
    public int[] KeysOfFunction;
    private boolean[] KeyState;
		//A Function Has Four Virtual Keys, Use >>> And &0xFF Get,Use << And | Set 
    public FunctionToKey()
    {
        KeysOfFunction = new int[0x100];
        KeyState = new boolean[0x100];
    }

		//Your Set At This
    public void setKeysOfFunction(int functionIndex, int key1, int key2, int key3, int key4)
    {
        if (functionIndex < 0x0 || functionIndex >= 0x100) return;
		int temp = key4 & 0x000000ff;
		temp <<= 8;
		temp |= key3 & 0x000000ff;
		temp <<= 8;
		temp |= key2 & 0x000000ff;
		temp <<= 8;
		temp |= key1 & 0x000000ff;
		KeysOfFunction[functionIndex] = temp;
    }
    public void setKeysOfFunction(int functionIndex, int key1, int key2, int key3)
    {
        if (functionIndex < 0x0 || functionIndex >= 0x100) return;
		int temp = 0;
		temp |= key3 & 0x000000ff;
		temp <<= 8;
		temp |= key2 & 0x000000ff;
		temp <<= 8;
		temp |= key1 & 0x000000ff;
		KeysOfFunction[functionIndex] = temp;
    }
    public void setKeysOfFunction(int functionIndex, int key1, int key2)
    {
        if (functionIndex < 0x0 || functionIndex >= 0x100) return;
		int temp = 0;
		temp |= key2 & 0x000000ff;
		temp <<= 8;
		temp |= key1 & 0x000000ff;
		KeysOfFunction[functionIndex] = temp;
    }
	public void setKeysOfFunction(int functionIndex, int key1)
    {
        if (functionIndex < 0x0 || functionIndex >= 0x100) return;
		KeysOfFunction[functionIndex] = key1 & 0x000000ff;
    }
    int getKeysOfFunction(int functionIndex)
    {
        if (functionIndex < 0x0 || functionIndex >= 0x100)
		{
			return 0x0;
		}
		return KeysOfFunction[functionIndex];
    }
	boolean getFunctionState(int functionIndex)
    {
        if (functionIndex < 0x0 || functionIndex >= 0x100)
		{
			return false;
		}
		int temp = KeysOfFunction[functionIndex];
		if ((temp & 0xFF) != 0 && KeyState[temp & 0xFF]) return true;
		temp >>>= 8;
		if ((temp & 0xFF) != 0 && KeyState[temp & 0xFF]) return true;
		temp >>>= 8;
		if ((temp & 0xFF) != 0 && KeyState[temp & 0xFF]) return true;
		temp >>>= 8;
		if ((temp & 0xFF) != 0 && KeyState[temp & 0xFF]) return true;
		return false;
	}

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyPressed(KeyEvent e) 
    {
        int code = e.getKeyCode();
        if(code >=0 && code <0x100) KeyState[code]  = true;
    }

    @Override
    public void keyReleased(KeyEvent e) 
    {
        int code = e.getKeyCode();
        if(code >=0 && code < 0x100) KeyState[code]  = false;
    }
}
