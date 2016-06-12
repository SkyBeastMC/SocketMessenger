package fr.skybeastmc.socketmessenger;


public enum Command{
    EXIT(0),
    IDENTIFY(127),
    BROADCAST(1),
    SEND_DATA(2),
    FORWARD_DATA(3),
    CONNECT(4),
    PLAYER_COUNT(5),
    PLAYER_LIST(6),
    GET_SERVERS(7),
    MESSAGE(8),
    GET_SERVER(9),
    KICK_PLAYER(10),
    
    
    STOP_SERVERS(20),
    STOP_ALL(21);
    
    private byte id;
    
    Command(int id){
        this.id = (byte)id;
    }
    
    public static Command get(byte command){
    	for(Command c : Command.values())
    		if(c.getByte() == command)
    			return c;
		return null;
    }
    
    public byte getByte(){
        return id;
    }
}