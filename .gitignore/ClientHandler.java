package src;





public class ClientHandler {
	public String info_hash;
	public TorrentInfo ti;
	
	public ClientHandler(TorrentInfo ti){
		this.ti = ti;
			
	}

	/**
	 * @return the info_hash
	 */
	public String getInfo_hash() {
		info_hash = byteArrayToURLString(ti.info_hash.array());
		return info_hash;
	}

	/**
	 * @param info_hash the info_hash to set
	 */
	public void setInfo_hash(String info_hash) {
		this.info_hash = info_hash;
	}

	/**
	 * @return the ti
	 */
	public TorrentInfo getTi() {
		return ti;
	}

	/**
	 * @param ti the ti to set
	 */
	public void setTi(TorrentInfo ti) {
		this.ti = ti;
	}
	//http://www.java2s.com/Code/Android/Network/ConvertabytearraytoaURLencodedstring.htm
		public static String byteArrayToURLString(byte in[]) {
			byte ch = 0x00;
			int i = 0;
			if (in == null || in.length <= 0)
				return null;

			String pseudo[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
					"A", "B", "C", "D", "E", "F" };
			StringBuffer out = new StringBuffer(in.length * 2);

			while (i < in.length) {
				// First check to see if we need ASCII or HEX
				if ((in[i] >= '0' && in[i] <= '9')
						|| (in[i] >= 'a' && in[i] <= 'z')
						|| (in[i] >= 'A' && in[i] <= 'Z') || in[i] == '$'
						|| in[i] == '-' || in[i] == '_' || in[i] == '.'
						|| in[i] == '!') {
					out.append((char) in[i]);
					i++;
				} else {
					out.append('%');
					ch = (byte) (in[i] & 0xF0); // Strip off high nibble
					ch = (byte) (ch >>> 4); // shift the bits down
					ch = (byte) (ch & 0x0F); // must do this is high order bit is
					// on!
					out.append(pseudo[(int) ch]); // convert the nibble to a
					// String Character
					ch = (byte) (in[i] & 0x0F); // Strip off low nibble
					out.append(pseudo[(int) ch]); // convert the nibble to a
					// String Character
					i++;
				}
			}

			String rslt = new String(out);

			return rslt;

		}
	
}
