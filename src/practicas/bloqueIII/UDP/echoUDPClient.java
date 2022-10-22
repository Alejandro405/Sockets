package practicas.bloqueIII.UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Scanner;

public class echoUDPClient {
    // puerto UDP
    public static final int PuertoServer = 7;
    public static final int PuertoClient = 8;
    // tamaño maximo del paquete
    public static final int Echomax = 255;
    // tiempo retransmision
    public static final int Tiempo= 2*1000;
    // reenvio
    public static final int Maxtri=3;

    public static InetAddress ipA;
    public static DatagramSocket socket;
    public echoUDPClient(String HostName){

        try{
            ipA=InetAddress.getByName(HostName);
            socket=new DatagramSocket();
        }catch(Exception e){
            System.out.println("Error al iniciar cliente");
        }
    }

    public void accionUdp(){
        try{
            Scanner sc=new Scanner(System.in);
            String msj,men;
            men="";
            System.out.println("Introduzca el mensaje que quieres enviar");
            while(true){
                msj=sc.nextLine();
                if(msj.compareTo(".")==0){
                    sc.close();
                    break;
                }else{
                    men=men+msj+"\n";
                }
            }
            char[] cArray=men.toCharArray();
            byte[] sendbuf=new byte[cArray.length];
            for (int offset=0; offset<cArray.length;offset++){
                sendbuf[offset]=(byte)cArray[offset];
            }
            socket.setSoTimeout(Tiempo);
            DatagramPacket conect= new DatagramPacket(sendbuf, sendbuf.length,ipA,PuertoServer);
            socket.send(conect);
            for(int i=0;i<=Maxtri;i++){ //retransmision------------

                DatagramPacket sendPacket= new DatagramPacket(sendbuf, sendbuf.length,ipA,PuertoClient);

                socket.send(sendPacket);
                System.out.println("Enviado correctamente el paquete\n"+men+"Enviado al servidor "+ipA.getHostName());

                //comienza la recepcion de paquete--------------------------------------

                byte[] recibeData=new byte[Echomax];
                DatagramPacket recibePacket=new DatagramPacket(recibeData, Echomax);

                try{
                    socket.receive(recibePacket);

                    if(recibePacket.getPort()!=PuertoClient){
                        System.out.println("Id de Transmision incorrecto");
                        break;
                    }else{
                        recibeData=recibePacket.getData();
                    }
                    System.out.println("Paquete recibido del servidor "+ipA.getHostAddress()+" : "+ipA.getHostName()+"\n**************\n");

                }catch(SocketTimeoutException o){
                    System.out.println("Error: Tiempo agotado");
                }catch(Exception e){
                    System.out.println("Error al recibir paquete");

                }
                //termina la recepcion del paquete------------------------------------------
            }//fin retransmision-------------------------------
            socket.close();
        }catch(Exception e){
            System.out.println("Error en el envio");
        }
    }



    /*public static void main(String args[]){
        echoUDPClient c=new echoUDPClient("localhost");
        c.accionUdp();
    }*/
}
