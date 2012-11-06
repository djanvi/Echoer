import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;


public class Echoer
{
 static final int packetsize=1024;

 static List<List<String>> alternateDS = new ArrayList<List<String>>();
	static int connID = 0, count = 1,same=0;
	Socket[] clientsocket = new Socket[7];
	
 public static void main(String args[])throws Exception
  {
	Echoer e = new Echoer();
	//int udpport, tcpport;
	String command;
	Socket s= new Socket("www.google.com",80);
	try{
	if ( args.length<2){
		System.out.println("Port(s) are empty; Program will exit");
		System.out.println("Please enter in this format: Echoer.java <UDPport> <TCPport>");
		System.exit(0);
	}
	
	
	int udpport=Integer.parseInt(args[1]);
	int tcpport=Integer.parseInt(args[0]);

	if (udpport > 60000 || tcpport > 60000){
		System.out.println("Port value(s) incorrect");
		System.exit(0);
	}
	
	UDPThread udpt = new UDPThread(udpport);
	TCPThread tcpt =new TCPThread(tcpport);
	udpt.start();
	tcpt.start();	
	
	while(true)
	{
		System.out.println("echoer>>");
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		command=in.readLine();
		String[] temp;
		if(command.equals("info"))
		{
			System.out.println("IPAddress      Hostname                   UDP port   TCP port");
			
			System.out.println(s.getLocalAddress().getHostAddress()+" "+InetAddress.getLocalHost().getCanonicalHostName()+"  "+udpport + "         " + tcpport);
			s.close();
		}
		else if(command.startsWith("connect"))
		{
			
			temp=command.split(" ");
			if(temp[2].equals(args[0]))
			{
			System.out.println("Self connection is blocked.");
			}
			else
			{e.connect(temp,connID);}			
			
		}
		else if(command.startsWith("show"))
		{
			
			e.showInfo();
		}
		else if(command.contains("sendto"))
		{	
			temp=command.split(" ",4);
			e.sendTo(temp[1],temp[2],temp[3]);
		}		
		else if(command.contains("send"))
		{
			
			temp=command.split(" ",3);
			e.send(temp[1],temp[2]);
		}
		else if(command.contains("disconnect"))
		{
			temp=command.split(" ");			
			e.disconnect(temp[1]);
		}
		else
		{
			System.out.println("unknown command");
		}
	
	}
	}
	catch(NumberFormatException n){
		System.out.println("Wrong ports entered");
		System.out.println("Correct format: Echoer.java <UDPport> <TCPport>");
		System.exit(0);
	}
	
	
	


}
 
   public  void connect(String[] temp, int id)
 {		
		for(int j=0;j<alternateDS.size();j++)
		{	
				
			if((alternateDS.get(j).get(1)).equalsIgnoreCase("/"+temp[1]))
			{
				same=1;
		System.out.println("Connection cannot be established as it is a duplicate connection. Program will exit.");
			
			}
		}
 		if(same==0){
	  	try{
	  	clientsocket[id] = new Socket(InetAddress.getByName(temp[1]),Integer.parseInt(temp[2]));
		System.out.println("Connected to :" + temp[1]);
	  	String cip=clientsocket[id].getInetAddress().toString();
		String chname=clientsocket[id].getInetAddress().getCanonicalHostName().toString();
	  	String localPort= Integer.toString(clientsocket[id].getLocalPort());
		String remotePort=Integer.toString(clientsocket[id].getPort());
		SaveInfo(cip,chname,localPort,remotePort);
		
	  	}catch(Exception e){}}
 		else
 			same=0;
 }
 
  public  void SaveInfo(String cip,String chname,String localPort, String remotePort)
	{
		
		
		List<String> tempList = new ArrayList<String>();		
		
		tempList.add(0, Integer.toString(count));
		tempList.add(1, cip);
		tempList.add(2, chname);
		tempList.add(3, localPort);
		tempList.add(4, remotePort);
		
		alternateDS.add(connID, tempList);
		connID++;count++;	
		//count = alternateDS.size();
	
		
		
	}	


 
public  void showInfo()
{		System.out.println("ConnectionID |  IPAddress        | HostName         |LocalPort  |  RemotePort |");
		System.out.println("-------------------------------------------------------------------------------");
		//System.out.println(alternateDS);
		//alternateDS.get(0).
		for(int k=0;k<alternateDS.size();k++)
		{
			for(int j=0;j<5;j++)
			{
				System.out.print(alternateDS.get(k).get(j)+"     |");
			}
			System.out.println("");
		}
}

public void send(String cid, String msg)
{
	try{
		PrintWriter out = new PrintWriter(clientsocket[Integer.parseInt(cid)-1].getOutputStream(), true);
    		BufferedReader in = new BufferedReader(new InputStreamReader(clientsocket[Integer.parseInt(cid)-1].getInputStream()));
    		out.println(msg);
	
    		String receivedmsg = in.readLine();
		System.out.println("Server sent  back the message:  "+receivedmsg);
	
	
	}catch(Exception e){}
	
	
}


public void sendTo(String ip,String udpport, String msg) throws IOException
{
	try{
		
	      DatagramSocket clientSocket = new DatagramSocket(); 
		  
	      byte[] sendData = new byte[1024]; 
	      byte[] receiveData = new byte[1024]; 
	      
	      sendData = msg.getBytes();         

	      System.out.println ("Sending data to UDP server");
	      DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, InetAddress.getByName(ip), Integer.parseInt(udpport)); 
	      
	      clientSocket.send(sendPacket); 
	  
	      DatagramPacket receivePacket =new DatagramPacket(receiveData, receiveData.length); 
	  	      
	      
	           clientSocket.receive(receivePacket); 
	           String receivedData = new String(receivePacket.getData()); 
	           System.out.println("Server sent back:  " + receivedData); 

	      
	      
	   }
	catch (Exception ex) { 
	     System.err.println(ex);
	    }
}



public void disconnect(String cid) throws Exception
{
	
	clientsocket[Integer.parseInt(cid)-1].close();
	removeInfo(cid);
	
}
	
public void removeInfo(String cid)
{
	alternateDS.remove(Integer.parseInt(cid)-1);
	connID = alternateDS.size();
	System.out.println("ConnectionID |  IPAddress        | HostName         |LocalPort  |  RemotePort |");
	System.out.println("-------------------------------------------------------------------------------");
	//System.out.println(alternateDS);
	//alternateDS.get(0).
	for(int k=0;k<alternateDS.size();k++)
	{
		for(int j=0;j<5;j++)
		{
			System.out.print(alternateDS.get(k).get(j)+"     |");
		}
		System.out.println("");
	}
}



}



class UDPThread extends Thread
{
	int udpport;
	UDPThread(int udpport)
	{
		this.udpport= udpport;
	}
	public void run()
	{
		
			DatagramSocket udpsocket=null;
			DatagramPacket udppacket;
			try
			{
				udpsocket=new DatagramSocket(udpport);
				byte[] receiveData = new byte[1024]; 
			        byte[] sendData  = new byte[1024]; 
			  
			      while(true) 
			        { 
			  
			          receiveData = new byte[1024]; 

			          DatagramPacket receivePacket =new DatagramPacket(receiveData, receiveData.length); 
	                  	  udpsocket.receive(receivePacket); 
			          String receivedData = new String(receivePacket.getData()); 
			          InetAddress IPAddress = receivePacket.getAddress(); 
			          int port = receivePacket.getPort(); 
			  
			          System.out.println ("From: " + IPAddress + ":" + port);
			          System.out.println ("Message: " + receivedData);
			          System.out.println("Echoing message: "+ receivedData+ "to IP Address: "+ IPAddress);
			          System.out.println("Type=UDP");


			          sendData = receivedData.getBytes(); 
			  
			          DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress,port); 
			  		  udpsocket.send(sendPacket); 

			        } 

			   }catch (Exception ex) {System.out.println("Exception");}

		
	
		
	}

}




class TCPThread extends Thread
{
	int id;
	int tcpport;
	int remoteport;
	String cip;
	String chostname;
	
	TCPThread (int tcpport)
	{
		this.tcpport= tcpport;
	}
	public void run()
	{
			int id=0;
			ServerSocket tcpsocket=null;		
			try
			{
				tcpsocket=new ServerSocket(tcpport);
			}
			catch(Exception e)
			{
				
				e.printStackTrace();
			}
		
			
			while(true)
			{
				try
				{	
					Socket s=null;
					s=tcpsocket.accept();
					id ++;
					System.out.println("Got connection request from "+ " " +s.getInetAddress());
					
					BufferedReader in = new BufferedReader(new InputStreamReader(s.getInputStream()));
					PrintWriter out = new PrintWriter(s.getOutputStream(),true);
					
					String inputLine = in.readLine();
					if((inputLine!= null))
					{
						System.out.println ("Client sent: " + inputLine); 
						System.out.println("Echoing" +" "+ inputLine+ " to IP " + s.getInetAddress());
						System.out.println("Type= TCP");
				    	out.println(inputLine); 
					}
				          

				    out.close(); 
				    in.close(); 
				     
				    
					
				
				}
				catch(Exception e){System.out.println("Couldnot listen to port");}

			//System.out.println("Got Connection from"+ " " +s.getInetAddress());
				
			
			
			}
							
	}
	
	
}

