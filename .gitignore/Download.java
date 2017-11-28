package src;



import java.util.ArrayList;

public class Download {

	ArrayList<byte[]> pieces = new ArrayList<byte[]>();
	ArrayList<byte[]> fullpiece = new ArrayList<byte[]>();
	
	Download(){
		//this.pieces = pieces;
	}

	public ArrayList<byte[]> getPieceArray() {
		return pieces;
	}

	public void setPieceArray(ArrayList<byte[]> pieces) {
		this.pieces = pieces;
	}
	public byte[] getPiece(int index){
		return this.pieces.get(index);
		
	}
	public void setPiece(byte[] piece, int index1){
		fullpiece.add(index1,piece);
	}
	public void setPieceToArray(byte[] fullpiece2,int index){
		pieces.add(index, fullpiece2); //have complete pieces for given index
	}
}
