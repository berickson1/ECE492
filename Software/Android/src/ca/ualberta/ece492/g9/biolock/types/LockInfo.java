package ca.ualberta.ece492.g9.biolock.types;

public class LockInfo {

	// private variables
	String ip;
	String name;
	int admin;
	int userPrint;

	// empty constructor
	public LockInfo(){
		
	}
	
	// constructor
	public LockInfo(String ipAddress, String ipName, int isAdmin, int userPrint) {
		this.ip = ipAddress;
		this.name = ipName;
		this.admin = isAdmin;
		this.userPrint = userPrint;
	}

	// getting ip
	public String getIP() {
		return this.ip;
	}

	// setting ip
	public void setIP(String ipAddress) {
		this.ip = ipAddress;
	}

	// getting name
	public String getName() {
		return this.name;
	}

	// setting name
	public void setName(String ipName) {
		this.name = ipName;
	}
	
	// getting admin status
	public int getAdmin () {
		return this.admin;
	}
	
	// setting admin status
	public void setAdmin (int admin){
		this.admin = admin;
	}
	
	public int getUserPrint(){
		return this.userPrint;
	}
	
	public void setUserPrint(int userPrint){
		this.userPrint = userPrint;
	}
}
