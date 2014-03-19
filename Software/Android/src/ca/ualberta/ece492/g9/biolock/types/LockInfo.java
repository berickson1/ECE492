package ca.ualberta.ece492.g9.biolock.types;

public class LockInfo {

	// private variables
	String ip;
	String name;

	// empty constructor
	public LockInfo(){
		
	}
	
	// constructor
	public LockInfo(String ipAddress, String ipName) {
		this.ip = ipAddress;
		this.name = ipName;
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
}
