package src;



import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class FileHandler {

	public RandomAccessFile fileObject;
	public String fileName;
	
	public FileHandler(String fileName){
		this.fileName = fileName;
		
	}
	
	public RandomAccessFile getFileObject() {
		return fileObject;
	}
	
	public void setFileObject(RandomAccessFile fileObject) {
		this.fileObject = fileObject;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public RandomAccessFile openFile() {
		
		try {
			this.fileObject = new RandomAccessFile(this.fileName, "rw");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.fileObject = null;
		}
		return fileObject;
	}
	
	public byte[] readFile(RandomAccessFile raf){
		
		long len;
		byte[] b = null;
		try {
			
			len = raf.length();
				//       System.out.println("Length : "+ len);
			b  = new byte[(int) len];//
	
			raf.readFully(b);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return b;
		
	}
	
	public void writeFile(String fileName){
		
		try {
			RandomAccessFile raf = new RandomAccessFile(fileName, "w");
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
}
