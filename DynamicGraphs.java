package awpsoft.gamemodule;
import java.util.*;
import java.awt.Image;

public class DynamicGraphs extends TimeVariantObject implements Cloneable
{
	    protected boolean Loop;
	    protected int CurrentPos;
	    protected float SwitchSpeed, Remnant;
	    protected Vector<Image> ThisClip;

        @SuppressWarnings("unchecked")
        @Override public DynamicGraphs clone() throws CloneNotSupportedException
        {
            DynamicGraphs temp = (DynamicGraphs)super.clone();
            temp.ThisClip = (Vector<Image>)ThisClip.clone();
            return temp;
        }
        public DynamicGraphs()
        {
            CurrentPos = 0;
            SwitchSpeed = 0.0f;
            Remnant = 0.0f;
            Loop = true;
        }
		public DynamicGraphs(Vector<Image> clip)
        {
            CurrentPos = 0;
            SwitchSpeed = 0.0f;
            Remnant = 0.0f;
            Loop = true;
            setClip(clip);
        }
        public void setClip(Vector<Image> clip)
        {
            setClip(clip, true);
        }
		public void setClip(Vector<Image> clip, boolean resetPos)
        {
            if (resetPos)
            {
                Remnant = 0.0f;
                CurrentPos = 0;
            }
            else if (clip == null || clip.isEmpty())
            {
                CurrentPos = 0;
            }
            else if (Loop)
            {
                CurrentPos %= clip.size();
            }
            else if (CurrentPos > (int)clip.size())
            {
                CurrentPos = (int)clip.size() - 1;
            }
            ThisClip = clip;
        }
		public void setSwitchParams(float speed, boolean enableLoop)
        {
            SwitchSpeed = speed;
		    Loop = enableLoop;
        }
		public void setSwitchSpeed(float speed)
        {
            SwitchSpeed = speed;
        }
		public void setEnableLoop(boolean enableLoop)
        {
            Loop = enableLoop;
        }
		public void resetCurrentPos()
        {
            Remnant = 0.0f;
            CurrentPos = 0;
        }
		public Image getCurrentFrame()
        {
            if (ThisClip == null || ThisClip.isEmpty()) return null;
		    else return ThisClip.get(CurrentPos);
        }
		public Image getLastFrame()
        {
            if (ThisClip == null || ThisClip.isEmpty()) return null;
		    else return ThisClip.get(ThisClip.size() - 1);
        }
		public Image getFirstFrame()
        {
            if (ThisClip == null || ThisClip.isEmpty()) return null;
            else return ThisClip.firstElement();
        }
		public Image getFrame(int index)
        {
            if (ThisClip == null || ThisClip.isEmpty()) return null;
            else if (index >= ThisClip.size()) return ThisClip.lastElement();
            else if (index < 0) return ThisClip.firstElement();
            else return ThisClip.get(index);
        }
		public @Override boolean giveTime(int timeGived)
        {
            if (DisableTimeVariant) return false;
            else if (SwitchSpeed == 0.0f) return true;
            else if (!Loop)
            {
                if (ThisClip == null || ThisClip.isEmpty()) return true;
                if (SwitchSpeed > 0.0f && CurrentPos == ThisClip.size() - 1)return true;
                if (SwitchSpeed < 0.0f && CurrentPos == 0) return true;
            }
            float temp1;
            int temp2;
            temp1 = SwitchSpeed * (float)timeGived + Remnant;
            Remnant = temp1 - (float)((int)temp1);
            temp2 = CurrentPos + (int)temp1;
            int frames;
            if (ThisClip == null || ThisClip.isEmpty()) frames = 0;
            else frames = ThisClip.size();
            if (Loop)
            {
                if (frames == 0)
                {
                    CurrentPos = 0;
                    return true;
                }
                temp2 = temp2 % frames;
                if (temp2 < 0) temp2 += frames;
                CurrentPos = temp2;
                return true;
            }
            if (temp2 < 0) temp2 = 0;
            if (temp2 >= frames) temp2 = frames - 1;
            if (temp2 < 0) temp2 = 0;
            CurrentPos = temp2;
            return true;
        }
};

