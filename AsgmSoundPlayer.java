package awpsoft.gamemodule;

import javax.sound.sampled.*;

public class AsgmSoundPlayer 
{
    public static class AsgmSoundBuffer implements Cloneable
    {
        protected byte[] Buffer;
        protected int BufferOffset;
        protected int EndPos;
        protected int LoopStartPos;
        protected int LoopEndPos;
        protected boolean Loop;
        public boolean equals(AsgmSoundBuffer obj) 
        {
            if(this == obj) return true;
            else if(Buffer == null && obj == null) return true;
            else if(Loop != obj.Loop) return false;
            else if(Buffer != obj.Buffer || BufferOffset != obj.BufferOffset || EndPos != obj.EndPos ) return false;
            else if(Loop == false) return true;
            else return LoopStartPos == obj.LoopStartPos && LoopEndPos == obj.LoopEndPos;
        }
        public void setBuffer(EquivalentVoidPointer ptr, int endPos)
        {
            Buffer = ptr.Buffer;
            BufferOffset = ptr.Offset;
            EndPos = endPos;
            repair();
        }
        public void disableLoop()
        {
            Loop = false;
        }
        public void enableLoop()
        {
            Loop = true;
            repair();
        }
        public void setLoop(int loopStartPos, int loopEndPos) 
        {
            Loop = true;
            LoopStartPos = loopStartPos;
            LoopEndPos = loopEndPos;
            repair();
        }
        public AsgmSoundBuffer() {}
        public AsgmSoundBuffer(EquivalentVoidPointer ptr, int endPos)
        {
            Buffer = ptr.Buffer;
            BufferOffset = ptr.Offset;
            EndPos = endPos;
            repair();
        }
        public AsgmSoundBuffer(EquivalentVoidPointer startPtr, int endPos ,boolean loop, int loopStartPos, int loopEndPos)
        {
            Buffer = startPtr.Buffer;
            BufferOffset = startPtr.Offset;
            EndPos = endPos;
            LoopStartPos = loopStartPos;
            LoopEndPos = loopEndPos;
            Loop = loop;
            repair();
        }
        public void repair()
        {
            if(Buffer == null) return;
            if(BufferOffset < 0) BufferOffset = 0;
            int efflen = Buffer.length - BufferOffset;
            if(EndPos < 0) EndPos = 0;
            else if(EndPos > efflen) EndPos = efflen;
            EndPos &= 0xFFFFFFFC;
            if(!Loop) return;
            if(LoopStartPos < 0) LoopStartPos = 0;
            else if(LoopStartPos > EndPos) LoopStartPos = EndPos;
            LoopStartPos &= 0xFFFFFFFC;
            if(LoopEndPos < LoopStartPos) LoopEndPos = LoopStartPos;
            else if(LoopEndPos > EndPos) LoopEndPos = EndPos;
            LoopEndPos &= 0xFFFFFFFC;
        }
        @Override public AsgmSoundBuffer clone()
        {
            try 
            {
                return (AsgmSoundBuffer) super.clone();
            } 
            catch (CloneNotSupportedException e) 
            {
                return new AsgmSoundBuffer();
            }
        }
    }

    protected AsgmSoundBuffer LastPlayed;
    protected AudioFormat ThisFormat;
    protected Clip ThisClip;



    public AsgmSoundPlayer()
    {
        ThisFormat = new AudioFormat(44100.0f, 16, 2, true, false);
    }
    public boolean isEnable() {return true;} //transplant from c++
    public boolean isSafeToReleasePostedBuffers() {return true;} //transplant from c++

    public void play(AsgmSoundBuffer soundinfo){play(soundinfo, true);}
    public void play(AsgmSoundBuffer soundinfo, boolean resetIfNoChange)
    {
        if(LastPlayed == null ||!LastPlayed.equals(soundinfo)) 
        {
            LastPlayed = soundinfo;
            if(ThisClip != null)
            {
                ThisClip.close();
            }
            if(LastPlayed.Buffer == null) return;
            try 
            {
                ThisClip = AudioSystem.getClip();
                ThisClip.open(ThisFormat, soundinfo.Buffer, soundinfo.BufferOffset, soundinfo.EndPos);
                if(soundinfo.Loop) 
                {
                    ThisClip.setLoopPoints(soundinfo.LoopStartPos >>> 2, (soundinfo.LoopEndPos >>> 2) - 1);
                    ThisClip.loop(Clip.LOOP_CONTINUOUSLY);
                }
                else 
                {
                    ThisClip.start();
                }
            } 
            catch (LineUnavailableException e1) 
            {
                ThisClip = null;
            }
        }
        else if(resetIfNoChange)
        {
            ThisClip.setFramePosition(0);
            if(ThisClip.isRunning()) return;
            if(soundinfo.Loop) ThisClip.loop(Clip.LOOP_CONTINUOUSLY);
            else ThisClip.start();
        }
        else
        {
            if(ThisClip == null || ThisClip.isRunning()) return;
            ThisClip.setFramePosition(0);
            if(soundinfo.Loop) ThisClip.loop(Clip.LOOP_CONTINUOUSLY);
            else ThisClip.start();
        }
    }
    
    //old transplant function
    ////public void play(EquivalentVoidPointer startAddress, int endPos, boolean resetIfNoChange = true,boolean loop = false,int loopStartPos = 0,int loopEndPos = 0x7FFFFFFF);
    public void play(EquivalentVoidPointer startPtr, int endPos) {play(startPtr, endPos, true, false, 0, Integer.MAX_VALUE);}
    public void play(EquivalentVoidPointer startPtr, int endPos, boolean resetIfNoChange) {play(startPtr, endPos, resetIfNoChange, false, 0, Integer.MAX_VALUE);}
    public void play(EquivalentVoidPointer startPtr, int endPos, boolean resetIfNoChange,boolean loop) {play(startPtr, endPos, resetIfNoChange, loop, 0, Integer.MAX_VALUE);}
    public void play(EquivalentVoidPointer startPtr, int endPos, boolean resetIfNoChange,boolean loop,int loopStartPos) { play(startPtr, endPos, resetIfNoChange, loop, loopStartPos, Integer.MAX_VALUE); }
    public void play(EquivalentVoidPointer startPtr, int endPos, boolean resetIfNoChange ,boolean loop, int loopStartPos, int loopEndPos)
    {
        play (new AsgmSoundBuffer(startPtr,endPos, loop, loopStartPos, loopEndPos), resetIfNoChange);
    }
    public void replay()
    {
        if(ThisClip == null) return;
        ThisClip.setFramePosition(0);
        if(ThisClip.isRunning()) return;
        if(LastPlayed.Loop) ThisClip.loop(Clip.LOOP_CONTINUOUSLY);
        else ThisClip.start();
    }
    public void stop()
    {
        if(ThisClip == null) return;
        ThisClip.setFramePosition(0);
        if(ThisClip.isRunning()) 
        {
            ThisClip.stop();
            ThisClip.flush();
        }
    }
    public void pause()
    {
        if(ThisClip == null) return;
        if(ThisClip.isRunning()) ThisClip.stop();
    }
    public void continuePlay()
    {
        if(ThisClip == null || ThisClip.isRunning()) return;
        if(LastPlayed.Loop) ThisClip.loop(Clip.LOOP_CONTINUOUSLY);
        else ThisClip.loop(0);
    }
    public void close()
    {
        if(ThisClip == null) return;
        ThisClip.close();
        ThisClip = null;
    }
    public int getCurrentPos()
    {
        if(ThisClip == null) return 0;
        else return ThisClip.getFramePosition() << 2;
    }
    public void setCurrentPos(int posByte)
    {
        if(ThisClip == null ) return;
        else ThisClip.setFramePosition(posByte >>> 2) ;
    }
    public void setVolume(float volume)
    {
        FloatControl fc =  (FloatControl) ThisClip.getControl(FloatControl.Type.MASTER_GAIN);
        fc.setValue((fc.getMaximum() - fc.getMinimum()) * volume + fc.getMinimum());
    }
    public float getVolume()
    { 
        FloatControl fc =  (FloatControl) ThisClip.getControl(FloatControl.Type.MASTER_GAIN);
        return (fc.getValue() - fc.getMinimum()) / (fc.getMaximum()- fc.getMinimum());
    }
    public void setInputSampleRate(float sampleRate)
    {
        ThisFormat = new AudioFormat((float)sampleRate, 16, 2, true, false);
    }
    public void setInputSampleRate()
    {
        ThisFormat.getSampleRate();
    }
}
