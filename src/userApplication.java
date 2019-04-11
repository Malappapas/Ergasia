import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

public class userApplication {

    //http://ithaki.eng.auth.gr/netlab/vlabProject.php?session=447817593884&x=2&fi=%CE%9A%CF%89%CE%BD%CF%83%CF%84%CE%B1%CE%BD%CF%84%CE%AF%CE%BD%CE%BF%CF%82&fa=%CE%9A%CE%BD%CE%B1%CE%AE%CF%82&am=8967
    //I take the strings from the page above
    //The strings change every day

    private static int clientPort = 48018;
    private static int serverPort = 38018;
    private static String echoCode = "E9019";
    private static String imageCode = "M1683";
    private static String audioCode = "A4389"; //Ad-hoc real-time audio streaming
    private static String copterCode = "Q4878"; // Remote flying micro-platform
    private static String vehicleCode = "v4227"; // Onboard car fault diagnostics
    private static byte[] hostIP = {(byte)155,(byte)207,(byte)222,(byte)240};
    private static Scanner scanner;


    public static void main(String[] args) throws IOException, LineUnavailableException {
        //Make choice what to do and proceed to function
        System.out.println("Choose action: \n" + "1. Echo Packets \n" + "2. Temperature Packets" +"3. Image Packets \n" + "4. Audio Packets \n" + "5. IthakiCopter Packets \n" + "6. Vehicle Packets \n" );
        scanner = new Scanner(System.in);
        int option = scanner.nextInt();
        switch (option){
            case 1:{//Echo Code
                System.out.println("Choose action: \n" + "1. With delay \n" + "2. Without delay");
                scanner = new Scanner(System.in);
                int option_delay = scanner.nextInt();
                switch (option_delay){
                    case 1:{//Echo with delay
                        System.out.println("Echo packets with delay \n");
                        echoPacket();
                        break;
                    }
                    case 2:{//Echo without delay.
                        System.out.println("Echo packets without delay \n");
                        echoCode = "E0000";
                        echoPacket();
                        break;
                    }
                    default: {
                        System.out.println("Not a valid option \n");
                    }
                }
             break;
            }

            case 2:{//Temperature code
                echoCode = echoCode + "T";
                temperaturePacket();
                break;
            }
            case 3:{//Image code
                System.out.println("Choose action \n"+"1. Camera Fixed \n" + "2. Camera with pan/tilt/zoom \n");
                scanner = new Scanner(System.in);
                int option_image = scanner.nextInt();
                switch (option_image){
                    case 1:{//Fixed camera
                        System.out.println("Image packets from fixed camera \n");
                        imagePacket();
                        break;
                    }
                    case 2:{
                        System.out.println("Image packets from pitch/tilt/zoom camera \n");
                        imageCode = imageCode + "PTZ";
                        imagePacket();
                        break;
                    }
                }
            break;
            }
            case 4:{//Audio code
                System.out.println("Choose coding \n"+"1. DCPM \n" + "2. AQ-DCPM \n");
                scanner = new Scanner(System.in);
                int option_audio = scanner.nextInt();
                switch (option_audio){
                    case 1:{//DCPM
                        System.out.println("Audio packets with DCPM \n");
                        //Tone or song
                        System.out.println("Select option: \n" + "1. Tone \n" + "2. Song \n");
                        scanner = new Scanner(System.in);
                        int option_song = scanner.nextInt();
                        switch (option_song){
                            case 1:{//Tone
                                audioCode = audioCode + "T";
                                audioPacket();
                                break;
                            }case 2:{//Song
                                audioCode = audioCode + "F";
                                audioPacket();
                                break;
                            }default:{
                                System.out.println("Not a vallid option");
                            }
                        }
                        break;
                    }
                    case 2:{//AQ-DCPM
                        System.out.println("Audio packets with AQ-DCPM \n");
                        audioCode = audioCode + "1AQ";
                        //Tone or song
                        System.out.println("Select option: \n" + "1. Tone \n" + "2. Song \n");
                        scanner = new Scanner(System.in);
                        int option_song = scanner.nextInt();
                        switch (option_song){
                            case 1:{//Tone
                                audioCode = audioCode + "T";
                                audioAQPacket();
                                break;
                            }case 2:{//Song
                                audioCode = audioCode + "F";
                                audioAQPacket();
                                break;
                            }default:{
                                System.out.println("Not a vallid option");
                            }
                        }
                    break;
                    }
                }
                break;
            }
            case 5:{//Ithaki Copter code
                copterPacket();
                break;
            }
            case 6:{//Vehicle code
                vehiclePacket();
                break;
            }
            default:{
                System.out.println("Not a valid option \n");
            }
        }
    }

//Functions

    //Echo Packets
    public static void echoPacket()throws IOException {
        System.out.println("Starting echo packets... \n");
        int count=0;//Packet Count
        long endTime; //Set ending time of packet
        long sendTime;//Set send time of packet
        long receiveTime =0; // receiveTime = endTime - sendTime
        byte[] txbyffer;
        byte[] rxbuffer;
        String messages = "";
        String responseTime = "";
        PrintWriter responseTxt = new PrintWriter("responseTime.txt"); //Txt file with the response times. Where does it go?
        PrintWriter messagesTxt = new PrintWriter("messages.txt"); //Txt files with the package messages

        DatagramSocket s  = new DatagramSocket();
        System.out.println("Echo Code: " +echoCode+ "\n");
        txbyffer = echoCode.getBytes();//Make echo code to byte array
        //int serverport = Integer.parseInt(serverPort); // Makes it integer. I already did decleard it
        InetAddress addressHost = InetAddress.getByAddress(hostIP); //Define host IP address
        System.out.println("Address host: " +addressHost);
        System.out.println("serverPort: "+serverPort);
        //Read manual
        DatagramPacket p = new DatagramPacket(txbyffer, txbyffer.length,addressHost, serverPort);

        DatagramSocket r = new DatagramSocket(clientPort);
        r.setSoTimeout(3000);//Check Numbers
        rxbuffer = new byte[32];

        DatagramPacket q = new DatagramPacket(rxbuffer ,rxbuffer.length);

        endTime = System.currentTimeMillis() + 216000;//60 seconds * 3600 = 216000 milliseconds
        //You have 60 seconds
        while (System.currentTimeMillis()<= endTime){
            sendTime = System.currentTimeMillis();
            //System.out.println("SendTime: " +sendTime+ " " +count+ "th time");
            s.send(p);//send packet p
            count++;
            //Send packet, record time. If an error happens print error
            try{
                r.receive(q);
                receiveTime = System.currentTimeMillis();
                messages = new String(rxbuffer,0,q.getLength());
            }catch(Exception x){
                System.out.println(x);
            }

            responseTime = String.valueOf(receiveTime - sendTime);//Makes responceTime as a String
            responseTxt.println(responseTime);//Save Responce Time
            messagesTxt.println(messages);
        }
        System.out.println("Packets Received: \n" + count);
        s.close();
        r.close();
        responseTxt.close();
        messagesTxt.close();
        System.out.println("Finished echo packets. \n");
    }

    //Temperature
    public static void temperaturePacket() throws  IOException{

        System.out.println("Starting Temperature packets... \n");
        int count=0;//Packet Count
        byte[] txbyffer;
        byte[] rxbuffer;
        String messages = "";
        String sensorCode = "";

        PrintWriter temperaturesTxt = new PrintWriter("Temperatures.txt");

        DatagramSocket s  = new DatagramSocket();
        txbyffer = echoCode.getBytes();//Make echo code to byte array
        InetAddress addressHost = InetAddress.getByAddress(hostIP); //Define host IP address

        DatagramPacket p = new DatagramPacket(txbyffer, txbyffer.length,addressHost, serverPort);

        DatagramSocket r = new DatagramSocket(clientPort);
        r.setSoTimeout(3000);
        rxbuffer = new byte[54];

        DatagramPacket q = new DatagramPacket(rxbuffer ,rxbuffer.length);

        //Check sensors 0-99.
        for(int i=0; i<100 ;i++){
            if (i<10){
                sensorCode = echoCode + "0" + String.valueOf(i); //T00-T09 I have to add a 0 in front
            }else{
                sensorCode = echoCode + String.valueOf(i);//Add number to echocode
            }
            txbyffer = sensorCode.getBytes();
            //send packet p
            p.setData(txbyffer);
            p.setLength(txbyffer.length);
            s.send(p);
            count++;

            try {
                //Receive packet q
                r.receive(q);
                messages = new String(rxbuffer, 0, q.getLength()); //Save packet q
            }catch (Exception x){
                System.out.println(x);//Print exception
            }
            temperaturesTxt.println(messages);//Print received packet
        }
        System.out.println("Number of temperature packets received packets: %d \n" +count);
        s.close();
        r.close();
        temperaturesTxt.close();
        System.out.println("Finished temperature pakcets ");
    }

    //Image
    public static void imagePacket() throws IOException{
        //The fixed camera has resolution 640p x 640p
        //The PTZ camera has resolution 320p x 240p

        //Flow ON/OFF
        System.out.println("Choose action: \n" + "1. Flow on \n" +"2. Flow off \n");
        scanner = new Scanner(System.in);
        int option_flow = scanner.nextInt();
        switch (option_flow) {
            case  1:{
                imageCode = imageCode + "ON";
                break;
            }
            case 2:{
                break;
            }
            default:{
                System.out.println("Not a vaid option \n");
            }
        }

        //Package Length
        System.out.println("Choose length: \n" + "1. 128 \n" +"2. 256 \n" + "3. 512 \n" + "3. 10024 \n");
        scanner = new Scanner(System.in);
        int option_length = scanner.nextInt();
        switch (option_length) {
            case  1:{
                imageCode = imageCode + "128";
                break;
            }
            case 2:{
                imageCode = imageCode + "256";
                break;
            }
            case 3:{
                imageCode = imageCode + "512";
                break;
            }
            case 4:{
                imageCode = imageCode + "1024";
                break;
            }
            default:{
                imageCode = imageCode + "128";
                break;
            }
        }

        System.out.println("Starting image packets... \n");

        byte[] txbyffer;
        byte[] rxbuffer;

        FileOutputStream picture = new FileOutputStream("picture.jpg");

        DatagramSocket s  = new DatagramSocket();

        txbyffer = imageCode.getBytes();//Make image code to byte array
        InetAddress addressHost = InetAddress.getByAddress(hostIP); //Define host IP address

        DatagramPacket p = new DatagramPacket(txbyffer, txbyffer.length,addressHost, serverPort);

        DatagramSocket r = new DatagramSocket(clientPort);
        r.setSoTimeout(9000);//Check Numbers
        rxbuffer = new byte[128];

        DatagramPacket q = new DatagramPacket(rxbuffer ,rxbuffer.length);

        s.send(p);//send packet p

        while(true){//Infinite Loop - Exits at an exeption
            try {
                r.receive(q);
                picture.write(rxbuffer);
                picture.flush();
            }catch (Exception x){
                System.out.println(x);
                break;//The loop exits when i have an exeption, so when image finishes?
            }
        }
        r.close();
        picture.close();
        s.close();
        System.out.println("Finished image packets \n");
    }

    //DCPM Audio
    public static void audioPacket() throws IOException, LineUnavailableException {
        //Number of packets
        System.out.println("Select number of audio packets \n");
        int packets = 1000;
        do{
            scanner = new Scanner(System.in);
            packets = scanner.nextInt();
        }while (packets >1000);

        System.out.println("Starting audio packets... \n");

        int count=0;//Packet Count
        byte[] txbyffer;
        byte[] rxbuffer;
        int bpp = 128; //Bits per packet
        int size = 2*bpp*packets;
        int[] samples = new int[size];
        int[] soundClip = new int[size];
        byte[] received = new byte[size];

        PrintWriter audioSamples = new PrintWriter("Audio Samples.txt");
        PrintWriter samplesDiffs = new PrintWriter("Audio Diffs Samples.txt");

        DatagramSocket s  = new DatagramSocket();
        txbyffer = audioCode.getBytes();//Make audio code to byte array
        InetAddress addressHost = InetAddress.getByAddress(hostIP); //Define host IP address

        DatagramPacket p = new DatagramPacket(txbyffer, txbyffer.length,addressHost, serverPort);

        DatagramSocket r = new DatagramSocket(clientPort);

        r.setSoTimeout(7000);//Check Numbers
        rxbuffer = new byte[bpp];

        DatagramPacket q = new DatagramPacket(rxbuffer ,rxbuffer.length);

        s.send(q);
        while(true){//Infinite loop
            try {
                r.receive(q);
                for(int i=0;i<bpp;i++){
                    received[count*bpp+i] = rxbuffer[i];//Place packets all together. E.G.: count(5) * bpp(127) + i(123) (5th packet, bit 123 goes to number 758 of the received array)
                    //rxbuffer has bpp(127) bits. Count keeps track of the packets
                }
                count++;
            }catch (Exception x){ // System exits at exeption
                System.out.println(x);
                break;
            }
        }
        System.out.println("Packets Received: %d \n" + count);
        //I dont quite get these 3 lines
        AudioFormat linearPCM = new AudioFormat(8000,8,1,true,false);
        SourceDataLine lineOut = AudioSystem.getSourceDataLine(linearPCM);
        lineOut.open(linearPCM,size);

        for(int i=0;i<bpp*packets;i++){
            //Wtf
            int k= (int)received[i];
            samples[2*i] = (((k>>4)& 15)-8);//xi-1 Ζυγοι
            samples[2*i+1] = ((k&15)-8);//xi Μονοι
        }

        for (int i=1;i<size;i++){
            soundClip[i] = samples[i]; //Move to soundClip starting from 1
        }
        System.out.println("soundClip[0]: %d \n" +soundClip[0]);
        byte[] audioBuffer = new byte[2*bpp*packets];
        audioBuffer[0] = (byte)(2*soundClip[0]);
        for (int i=1;i<size;i++){
            soundClip[i] = 2*soundClip[i] + audioBuffer[i-1];
            audioBuffer[i] = (byte)soundClip[i];
        }
        for(int i=0;i<size;i++){
            audioSamples.println(audioBuffer[i]);
            samplesDiffs.println(samples[i]);
        }
        lineOut.start();
        lineOut.write(audioBuffer,0,size);
        lineOut.stop();
        lineOut.close();

        s.close();
        r.close();
        audioSamples.close();
        samplesDiffs.close();
        System.out.println("Finished audio packets. \n");

    }


    //AQ-DCPM Audio
    public static void audioAQPacket() throws IOException, LineUnavailableException {
        int packets = 1000;
        do{
            System.out.println("Select number of audio packets \n");
            scanner = new Scanner(System.in);
            packets = scanner.nextInt();
        }while (packets >1000);
        int bpp = 132; //page 11
        int size = 2*bpp*packets;
        int count = 0;
        int lsb;
        int msb;
        int[] mean =new int[packets];
        int[] step = new int [packets];
        int[] samples = new int[size];
        int[] clip = new int[size];
        byte[] tx;
        byte[] rx = new byte[bpp];;
        byte[] received = new byte[bpp*packets];

        PrintWriter samplesAQAudio = new PrintWriter("Audio Samples AQDCPM.txt");
        PrintWriter samplesAQDiffs = new PrintWriter("Audio Samples Diffs AQDCPM.txt");
        PrintWriter means = new PrintWriter("means.txt");
        PrintWriter steps = new PrintWriter("steps.txt");

        DatagramSocket s = new DatagramSocket();

        tx = audioCode.getBytes();
        InetAddress addressHost = InetAddress.getByAddress(hostIP);

        DatagramPacket p = new DatagramPacket(tx,tx.length,addressHost,serverPort);
        s.send(p);

        DatagramSocket r = new DatagramSocket(clientPort);
        r.setSoTimeout(1000);

        DatagramPacket q = new DatagramPacket(rx, rx.length);
        //Receive Packets
        while (true){
            try{
                r.receive(q);
                for(int i=0;i<bpp;i++){
                    received[count*bpp+i] = rx[i];
                }
                count++;
            }catch (Exception x){
                System.out.println(x);
                break;
            }
        }
        System.out.println("Number of AQ audio packets \n" +count);
        AudioFormat linearPCM = new AudioFormat(8000,8,1,true,false);
        SourceDataLine lineOut = AudioSystem.getSourceDataLine(linearPCM);
        lineOut.open(linearPCM,size);

        for(int i=0;i<packets;i++){
            lsb = (int)received[bpp*i];
            msb = (int)received[bpp*i+1];
            mean[i] = (256*msb)+(lsb & 0x00FF);//Why
            lsb = (int)received[bpp*i+2];
            msb=(int)received[bpp*i+3];
            step[i]=(256*(msb&0x00FF))+(lsb & 0x00FF);
        }
        for(int i=0;i<packets;i++){
            means.println(mean[i]);
            steps.println(step[i]);
        }
        count=0;
        for(int i=0;i<packets;i++){
            for (int j=4;j<bpp;j++){
                int k = (int)received[i*bpp+j];
                samples[2*count]=(((k>>4)&15)-8)*step[i];
                samples[2*count+1]=((k&15)-8)*step[i];
                count++;
            }
        }
        byte[] audioBuffer = new byte[512*packets];

        for(int i=1;i<256*packets;i++){
            clip[i]=samples[i];
        }
        for (int i=0;i<packets;i++){
            for(int j=0;j<256;j++){
                if (i==0 && j==0) continue;
                clip[i*256 +j] = clip[i*256+j]+clip[i*256+j-1];
            }
        }
        for(int i=0;i<256*packets;i++){
            audioBuffer[2*i] = (byte)(clip[i]&0xFF);
            audioBuffer[2*i+1]=(byte)((clip[i]>>0)&0xFF);
        }
        for(int i=0;i<256*packets;i++){
            samplesAQAudio.println(audioBuffer[i]);
            samplesAQDiffs.println(samples[i]);
        }

        lineOut.start();
        lineOut.write(audioBuffer,0,audioBuffer.length);
        lineOut.start();
        lineOut.close();
        s.close();
        r.close();
        samplesAQAudio.close();
        samplesAQDiffs.close();
        means.close();
        steps.close();

    }
    //IthakiCopter
    public static void copterPacket() throws IOException{
        System.out.println("Starting IthakiCopeter packets... \n");

        InetAddress addressHost = InetAddress.getByAddress(hostIP);

        PrintWriter ithaki = new PrintWriter("IthakiCopter.txt");
        Socket s =new Socket(addressHost, serverPort);//Check if it has a different server port
        s.setSoTimeout(1000);
        InputStream in = s.getInputStream();
        OutputStream out = s.getOutputStream();

        while (true){
            try{
                out.write("AUTO FLIGHTLEVEL = 150 LMOTOR = 125 RMOTOR = 125 \r \n".getBytes());
                int k = in.read();
                System.out.println((char) k);
                ithaki.print((char) k);
            }catch (Exception x){
                System.out.println(x);
                break;
            }
        }
        s.close();
        ithaki.close();
    }

    //Vehicle
    public static void vehiclePacket(){
        System.out.println("Starting vehicle packets... \n");


    }
}



