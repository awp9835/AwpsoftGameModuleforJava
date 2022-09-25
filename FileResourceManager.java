package awpsoft.gamemodule;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class FileResourceManager 
{
    public static final int __FILETYPE_BMP = 0x504D422E;
	public static final int __FILETYPE_PNG = 0x474E502E;
	public static final int __FILETYPE_JPG = 0x47504A2E;
	public static final int __FILETYPE_GIF = 0x4649472E;
	public static final int __FILETYPE_TXT = 0x5458542E;
	public static final int __FILETYPE_WAV = 0x5641572E;
	public static final int __FILETYPE_PCM = 0x4D43502E;
	public static final int __FILETYPE_DAT = 0x5441442E;
	public static final int __FILETYPE_INI = 0x494E492E;
	public static final int __FILETYPE_BIN = 0x4E49422E;
	public static final int __FILETYPE_CSV = 0x5653432E;
	
	public static class FileResourceInfo implements Cloneable
	{
		public byte[] Buffer;
		public int FileID;
        public int Size; //unsigned int store!
		public int Type;
		public float Param1, Param2;
		FileResourceInfo()
        {
            Buffer = null;
		    FileID = -1;
		    Type = 0x00000000;
		    Size = 0;
		    Param1 = Param2 = 0.0f;
        }
        FileResourceInfo(int fileID, int type, float param1, float param2)
        {
            Buffer = null;
            Size = 0;
            FileID = fileID;
            Type = type;
            Param1 = param1;
            Param2 = param2;
        }
        @Override public FileResourceInfo clone() throws CloneNotSupportedException 
        {
     
            return (FileResourceInfo)super.clone();
        }
	};
    
    protected FileResourceInfo[] FileList;

	public FileResourceManager()
    {
		FileList = new FileResourceInfo[0x1000]; 
    }
	public FileResourceManager(int maxFileCount)
    {
        FileList = new FileResourceInfo[maxFileCount]; 
    }
	@Override public void finalize()
    {
        releaseAllFileResource();
    }
	public int takeOverFileResource(FileResourceInfo fileInfo)
    {
        if(fileInfo == null) return -1;
        if (fileInfo.FileID >= FileList.length || fileInfo.FileID < 0) return -1;
		FileList[fileInfo.FileID] = fileInfo;
		return fileInfo.FileID;
    }
	public FileResourceInfo getFileResourceInfo(int fileID)
    {
        if (fileID >= FileList.length || fileID < 0) return null;
		else return FileList[fileID];
    }
	public FileResourceInfo takeOutFileResource(int fileID)
    {
        if (fileID >= FileList.length || fileID < 0)
		{
			return null;
		}
		else
		{
			FileResourceInfo temp = FileList[fileID];
			FileList[fileID] = null;
			return temp;
		}
    }
	public int loadFile(String fileName, int fileID, int fileType) //return fsize,failed:0
	{
        return loadFile(fileName, fileID, fileType, 0.0f, 0.0f);
    }
    public int loadFile(String fileName, int fileID, int fileType, float param1) //return fsize,failed:0
    {
        return loadFile(fileName, fileID, fileType, param1, 0.0f);
    }
    public int loadFile(String fileName, int fileID, int fileType, float param1, float param2) //return fsize,failed:0
    {
        if (fileID >= FileList.length || fileID < 0 || fileName == null) return 0;
        File fp = new File(fileName);
		try(FileInputStream fis = new FileInputStream(fp))
        {
            FileResourceInfo fri = new FileResourceInfo(fileID, fileType, param1, param1);
            long fsize = fp.length();
            if((fri.Size & 0xFFFFFFFF80000000L) != 0L) return 0; 
            fri.Size = (int)fsize;
            fri.Buffer = new byte[fri.Size];      
            fis.read(fri.Buffer);
            if (takeOverFileResource(fri) != -1) return fri.Size;
            else return 0;
        }
        catch(Exception ex)
        {
            return 0;
        }
    }
    public long releaseFileResource(int fileID)
    {
        if (fileID >= FileList.length || fileID < 0) return 0;
        long ts = 0L;
        if(FileList[fileID] != null) ts = FileList[fileID].Size;
		FileList[fileID] = null;
		return ts;
    }
	public long releaseAllFileResource()
    {
        long ttSize = 0;
		for (int i = 0; i < FileList.length; i++)
		{
			if (FileList[i] != null)
			{
				ttSize += FileList[i].Size;
				FileList[i] = null;
			}
		}
        System.gc();
		return ttSize;
    }
	public int loadFilesFromPackage(String packageFileName)
    {
        if(packageFileName == null) return 0;
        int scnt = 0;
		FileResourceInfo frp = new FileResourceInfo();
        File f = new File(packageFileName);
		try(FileInputStream fp = new FileInputStream(f))
        {
            long fsize = f.length();
            byte[] byte4 = new byte[4]; 
            fp.read(byte4); frp.Type = ByteBuffer.wrap(byte4).order(ByteOrder.LITTLE_ENDIAN).getInt();
            if (frp.Type != 0x50464741) return 0;
            fsize -= 4;
            byte[] byte20 = new byte[20];     
            while (fsize > 0L)
            {
                fp.read(byte20); 
                ByteBuffer bb = ByteBuffer.wrap(byte20).order(ByteOrder.LITTLE_ENDIAN);
                frp.FileID = bb.getInt();
                frp.Type = bb.getInt();
                frp.Param1 = bb.getFloat();
                frp.Param2 = bb.getFloat();
                frp.Size = bb.getInt();
                fsize -= 20;
                if (frp.Size > fsize || frp.Size < 0)
                {
                    break;
                }
                if (frp.FileID >= FileList.length || frp.FileID < 0)
                {
                    fsize -= frp.Size;
                    fp.skip(frp.Size);
                    continue;
                }
                try
                {
                    frp.Buffer = new byte[frp.Size];
                    fp.read(frp.Buffer);
                    fsize -= frp.Size;
                    if(takeOverFileResource(frp.clone()) >= 0) scnt++;
                }
                catch (Exception ex)
                {
                    fsize -= frp.Size;
                    fp.skip(frp.Size);
                    continue;
                }
            }
            return scnt;
        }
        catch(Exception ex)
        {
            return scnt;
        }
    }
    public int loadRangeFilesFromPackage(String packageFileName)
    {
        return  loadRangeFilesFromPackage(packageFileName, 0, 0x7FFFFFFF);
    }
    public int loadRangeFilesFromPackage(String packageFileName, int minFileID)
    {
        return  loadRangeFilesFromPackage(packageFileName, minFileID, 0x7FFFFFFF);
    }
	public int loadRangeFilesFromPackage(String packageFileName, int minFileID, int maxFileID)
    {
        if(packageFileName == null) return 0;
        int scnt = 0;
		FileResourceInfo frp = new FileResourceInfo();
        File f = new File(packageFileName);
		try(FileInputStream fp = new FileInputStream(f))
        {
            long fsize = f.length();
            byte[] byte4 = new byte[4]; 
            fp.read(byte4); frp.Type = ByteBuffer.wrap(byte4).order(ByteOrder.LITTLE_ENDIAN).getInt();
            if (frp.Type != 0x50464741) return 0;
            fsize -= 4;
            byte[] byte20 = new byte[20];     
            while (fsize > 0L)
            {
                fp.read(byte20); 
                ByteBuffer bb = ByteBuffer.wrap(byte20).order(ByteOrder.LITTLE_ENDIAN);
                frp.FileID = bb.getInt();
                frp.Type = bb.getInt();
                frp.Param1 = bb.getFloat();
                frp.Param2 = bb.getFloat();
                frp.Size = bb.getInt();
                fsize -= 20;
                if (frp.Size > fsize || frp.Size < 0)
                {
                    break;
                }
                if (frp.FileID < minFileID || frp.FileID > maxFileID || frp.FileID >= FileList.length || frp.FileID < 0)
                {
                    fsize -= frp.Size;
                    fp.skip(frp.Size);
                    continue;
                }
                try
                {
                    frp.Buffer = new byte[frp.Size];
                    fp.read(frp.Buffer);
                    fsize -= frp.Size;
                    if(takeOverFileResource(frp.clone()) >= 0) scnt++;
                }
                catch (Exception ex)
                {
                    fsize -= frp.Size;
                    fp.skip(frp.Size);
                    continue;
                }
            }
            return scnt;
        }
        catch(Exception ex)
        {
            return scnt;
        }
    }
	public long loadSingleFileFromPackage(String packageFileName, int fileID)
    {
        if(packageFileName == null) return 0;
        if (fileID >= FileList.length || fileID < 0) return 0;
        int ssize = 0;
		FileResourceInfo frp = new FileResourceInfo();
        File f = new File(packageFileName);
		try(FileInputStream fp = new FileInputStream(f))
        {
            long fsize = f.length();
            byte[] byte4 = new byte[4]; 
            fp.read(byte4); frp.Type = ByteBuffer.wrap(byte4).order(ByteOrder.LITTLE_ENDIAN).getInt();
            if (frp.Type != 0x50464741) return 0;
            fsize -= 4;
            byte[] byte20 = new byte[20];     
            while (fsize > 0L)
            {
                fp.read(byte20); 
                ByteBuffer bb = ByteBuffer.wrap(byte20).order(ByteOrder.LITTLE_ENDIAN);
                frp.FileID = bb.getInt();
                frp.Type = bb.getInt();
                frp.Param1 = bb.getFloat();
                frp.Param2 = bb.getFloat();
                frp.Size = bb.getInt();
                fsize -= 20;
                if (frp.Size > fsize || frp.Size < 0)
                {
                    break;
                }
                if (frp.FileID != fileID)
                {
                    fsize -= frp.Size;
                    fp.skip(frp.Size);
                    continue;
                }
                try
                {
                    frp.Buffer = new byte[frp.Size];
                    fp.read(frp.Buffer);
                    fsize -= frp.Size;
                    if(takeOverFileResource(frp.clone()) >= 0) 
                    {   
                        ssize = frp.Size;
                        break;
                    }
                }
                catch (Exception ex)
                {
                    fsize -= frp.Size;
                    fp.skip(frp.Size);
                    continue;
                }
            }
            return ssize;
        }
        catch(Exception ex)
        {
            return ssize;
        }
    }
	public int saveAsPackage(String targetPackageName)
    {
        if(targetPackageName == null) return 0;
        File f= new File(targetPackageName);
        int scnt = 0;
		final byte[] c = {'A','G','F','P'};

        try(FileOutputStream fos = new FileOutputStream(f))
        {
            fos.write(c);
            byte[] byte20 = new byte[20];  
            System.out.print("ID\t类型码   \t附属参数1\t附属参数2\t状态      \t文件大小\n");
            for (FileResourceInfo finfo: FileList)
            {
                if (finfo == null || finfo.FileID < 0) continue;
                ByteBuffer bb = ByteBuffer.wrap(byte20).order(ByteOrder.LITTLE_ENDIAN);
                bb.putInt(finfo.FileID).putInt(finfo.Type).putFloat(finfo.Param1)
                  .putFloat(finfo.Param2).putInt(finfo.Size);
                fos.write(byte20);
                fos.write(finfo.Buffer, 0, finfo.Size);
                scnt++;
                if (finfo.Size / 1024 >= 1024)
                {
                    System.out.printf((Locale)null,"%-6d\t0x%-08X\t%-8f\t%-8f\t%-12s\t打包成功\t%-6.2fMB\n", 
                        finfo.FileID, finfo.Type, finfo.Param1, finfo.Param2,  finfo.Size / 1024.0 / 1024.0);
                }
                else
                {
                    System.out.printf((Locale)null,"%-6d\t0x%-08X\t%-8f\t%-8f\t%-12s\t打包成功\t%-6.2fKB\n", 
                        finfo.FileID, finfo.Type, finfo.Param1, finfo.Param2,  finfo.Size / 1024.0);
                }
            }
        }
        catch(IOException ex)
        {
            System.out.print(ex.toString() + "\nIO异常，写入到Package文件失败！\n");
        }
        finally
        {
            System.gc();
        }
		System.out.println("文件打包结束，成功" + scnt + "个。");
		return scnt;
    }
	public int makePackageFromCSV(String manifestFileName, String targetPackageName)
    {
        return makeDefaultPackageFromCSV(manifestFileName, targetPackageName);
    }
	public static int makeDefaultPackageFromCSV(String manifestFileName, String targetPackageName)
    {
        /*
		Num,,,,
		Id,0xType,FloatPara1,FloatPara2,FileName(CANNOT contain SPACE and CHINESE...!)
		......
		*/
		int scnt = 0, id = 0, num = 0, type = 0;
		float p1 = 0.0f, p2 = 0.0f;
		String name;
		int fsize = 0;
		final byte[] c = {'A','G','F','P'};
        try(Scanner mfp = new Scanner(new File(manifestFileName));
            FileOutputStream pfp = new FileOutputStream(targetPackageName))
        {
            pfp.write(c);
            String[] firstline = mfp.nextLine().split(",");
            num = Integer.parseInt(firstline[0].trim());
            firstline = null;
            System.out.print("共" + num + "个文件\n");
            System.out.print("ID\t类型码   \t附属参数1\t附属参数2\t文件名      \t状态      \t文件大小\n");
            byte[] byte20 = new byte[20];  
            for (int i = 1; i <= num; i++)
            {
                String[] thisline = mfp.nextLine().split(",");
                id = Integer.parseInt(thisline[0].trim()); 
                type = (int) Long.parseLong(thisline[1].trim().substring(2), 16); 
                p1 = Float.parseFloat(thisline[2].trim());
                p2 = Float.parseFloat(thisline[3].trim());
                name = thisline[4].trim();
                thisline = null;

                File fp = new File(name);
                boolean readsucceed = false;
                try(FileInputStream ffp = new FileInputStream(fp))
                {
                    long tl = fp.length();
                    if((tl & 0xFFFFFFFF80000000L) != 0L)
                    {
                        System.out.printf((Locale)null, "%-6d\t0x%-08X\t%-8f\t%-8f\t%-12s\t文件过大，无法载入\n", id, type, p1, p2, name);
                        continue;
                    }
                    fsize = (int)tl;
                    byte[] buffer = ffp.readAllBytes();
                    readsucceed =true;
                    ByteBuffer bb = ByteBuffer.wrap(byte20).order(ByteOrder.LITTLE_ENDIAN);
                    bb.putInt(id).putInt(type).putFloat(p1).putFloat(p2).putInt(fsize);
                    pfp.write(byte20);
                    pfp.write(buffer);
                    if (fsize / 1024 >= 1024)
                    {
                        System.out.printf((Locale)null,"%-6d\t0x%-08X\t%-8f\t%-8f\t%-12s\t打包成功\t%-6.2fMB\n", id, type, p1, p2, name, fsize / 1024.0 / 1024.0);
                    }
                    else
                    {
                        System.out.printf((Locale)null,"%-6d\t0x%-08X\t%-8f\t%-8f\t%-12s\t打包成功\t%-6.2fKB\n", id, type, p1, p2, name, fsize / 1024.0);
                    }
                    scnt++;
                    buffer = null;
                }
                catch (OutOfMemoryError re)
                {
                    System.out.println(re.toString() + "\n文件过大，系统内存不足，操作终止！\n");
                    break;
                }
                catch(IOException|NullPointerException ex)
                {
                    System.out.printf((Locale)null, "%-6d\t0x%-08X\t%-8f\t%-8f\t%-12s\t文件打开、读取或写入失败\n", id, type, p1, p2, name);
                    if(readsucceed) break;
                    else continue;
                }
                finally
                {
                    System.gc();
                }
            }
        }
        catch (FileNotFoundException ex) 
        {
            System.out.print(ex.toString() + "\n未找到文件清单或Package文件创建失败\n");
            return 0;
        }
        catch (SecurityException ex) 
        {
            System.out.print(ex.toString() + "\n权限不足，请先设置相关文件权限\n");
            return 0;
        }
        catch (IndexOutOfBoundsException ex)
        {
            System.out.print(ex.toString() + "\n文件清单格式不正确，请使用表格类软件填写\n");
        }
        catch (NoSuchElementException|IllegalStateException ex)
        {
            System.out.print(ex.toString() + ((num == 0)?"\n文件清单格式不正确\n":"\n文件个数填写错误\n"));
        }
        catch(NumberFormatException ex)
        {
            System.out.print(ex.toString() + "\n文件清单数字格式不正确\n");
        }
        catch (IOException ex)
        {
            System.out.print(ex.toString() + "\n目标Package文件写入失败\n");
        }
        catch (NullPointerException ex)
        {
            ex.printStackTrace();
            System.out.print(ex.toString() + "\n未知错误...\n");
        }
		System.out.print("文件打包结束，成功" + scnt + "个。\n");
		return scnt;
    }
}
