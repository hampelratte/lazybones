/* $Id: Server.java,v 1.1 2007-05-03 20:54:53 hampelratte Exp $
 * 
 * Copyright (c) 2005, Henrik Niehaus & Lazy Bones development team
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 * 3. Neither the name of the project (Lazy Bones) nor the names of its 
 *    contributors may be used to endorse or promote products derived from this 
 *    software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package test.dummyserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private int port = 2001;
    
    private BufferedReader br;
    
    private PrintStream ps;
    
    private Socket socket;
    
    public Server() {
        try {
            ServerSocket ss = new ServerSocket(port);
            System.out.println("Listening on port " + port);
            while(true) {
                socket = ss.accept();
                System.out.println("Connection from " + socket.getRemoteSocketAddress());
                
                br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                ps = new PrintStream(socket.getOutputStream());
                
                System.out.println("Sending welcome message.");
                sendWelcomeMessage();
                
                while(true) {
                    if(!readRequest()) {
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean readRequest() throws IOException {
        System.out.println("Reading request:");
        if(socket.isClosed()) {
            return false;
        }
            
        String request = br.readLine();
        
        System.out.println("<-- " + request);
        if(request == null) return false;
        
        if("quit".equalsIgnoreCase(request)) {
            socket.close();
        } else if ("lstc".equalsIgnoreCase(request)) {
            printChannelList();
        } else if ("lstt".equalsIgnoreCase(request)) {
            printTimerList();
        } else if ("lstr".equalsIgnoreCase(request)) {
            printRecordingsList();
        }
        
        return true;
    }

    private void printRecordingsList() {
        ps.println("250-1 08.03.07 16:13* Tagesthemen\n" + 
                "250 2 08.03.07 16:13* Tagesthemen oder wie oder was");
    }

    private void printTimerList() {
        ps.println("550 No timers defined");
        
    }

    private void printChannelList() {
        ps.println("250-1 Das Erste;ARD:522000000:I999C999D12M16B8T8G8Y0:T:27500:1401:1402=deu:1404:0 :14:8468:258:0\n" + 
                "250-2 ZDF;ZDFmobil:570000000:I999C23D12M16B8T8G4Y0:T:27500:545:546=deu,547=2ch;5 59=dd:551:0:514:8468:514:0\n" + 
                "250-3 rbb Berlin;ARD:522000000:I999C23D12M16B8T8G8Y0:T:27500:1201:1202=deu:1204: 0:12:8468:258:0\n" + 
                "250-4 RTL Television,RTL;RTL World:506000000:I999C23D12M16B8T8G8Y0:T:27500:337:3 38=deu:343:0:16405:8468:773:0\n" + 
                "250-5 SAT.1;ProSiebenSat.1:658000000:I999C23D12M16B8T8G8Y0:T:27500:385:386=deu;392=deu:391:0:16408:8468:769:0\n" + 
                "250-6 ProSieben;ProSiebenSat.1:658000000:I999C23D12M16B8T8G8Y0:T:27500:305:306=deu;312=deu:311:0:16403:8468:769:0\n" + 
                "250-7 KABEL1;ProSiebenSat.1:658000000:I999C23D12M16B8T8G8Y0:T:27500:161:162=deu:167:0:16394:8468:769:0\n" + 
                "250-8 VOX;RTL World:506000000:I999C23D12M16B8T8G8Y0:T:27500:545:546=deu:551:0:16418:8468:773:0\n" + 
                "250-9 RTL2;RTL World:506000000:I999C23D12M16B8T8G8Y0:T:27500:353:354=deu:359:0:16406:8468:773:0\n" + 
                "250-10 Super RTL,S RTL;RTL World:506000000:I999C23D12M16B8T8G8Y0:T:27500:433:434=deu:439:0:16411:8468:773:0\n" + 
                "250-11 DSF;BetaDigital:754000000:I999C23D12M16B8T8G8Y0:T:27500:129:130=deu:135:0:16392:8468:774:0\n" + 
                "250-12 Eurosport;T-Systems:754000000:I999C23D12M16B8T8G8Y0:T:27500:577+8190:578=deu:583:0:16420:8468:774:0\n" + 
                "250-13 Kika-Doku,Doku/KiKa;ZDFmobil:570000000:I999C23D12M16B8T8G4Y0:T:27500:593:594=deu:599:0:517:8468:514:0\n" + 
                "250-14 arte;ARD:191500000:I999C23D12M16B7T8G8Y0:T:27500:201:202=deu,203=fra:204:0:2:8468:257:0\n" + 
                "250-15 3sat;ZDFmobil:570000000:I999C23D12M16B8T8G4Y0:T:27500:561:562=deu,563=2ch;575=dd:567:0:515:8468:514:0\n" + 
                "250-16 MDR FERNSEHEN,MDR;ARD:191500000:I999C23D12M16B7T8G8Y0:T:27500:101:102=deu:104:0:1:8468:257:0\n" + 
                "250-17 EinsExtra;ARD:522000000:I999C23D12M16B8T8G8Y0:T:27500:1501:1502=deu:1404:0:15:8468:258:0\n" + 
                "250-18 WDR K÷ln;ARD:177500000:I999C34D12M16B7T8G8Y0:T:27500:241:242=deu:247:0:15:8468:772:0\n" + 
                "250-19 SWR BW,SWR BW;ARD:177500000:I999C34D12M16B7T8G8Y0:T:27500:257:258=deu:263:0:16:8468:772:0\n" + 
                "250-20 NDR FERNSEHEN,NDR;ARD:191500000:I999C23D12M16B7T8G8Y0:T:27500:301:302=deu:304:0:3:8468:257:0\n" + 
                "250-21 Phoenix;ARD:522000000:I999C23D12M16B8T8G8Y0:T:27500:1301:1302=deu:1304:0:13:8468:258:0\n" + 
                "250-22 n-tv;RTL World:778000000:I999C23D12M16B8T8G8Y0:T:27500:257:258=deu:263:0:16400:8468:2305:0\n" + 
                "250-23 N24;ProSiebenSat.1:658000000:I999C23D12M16B8T8G8Y0:T:27500:225:226=deu:231:0:16398:8468:769:0\n" + 
                "250-24 EuroNews;GlobeCast:778000000:I999C23D12M16B8T8G8Y0:T:27500:593:594=deu,595=eng,596=fra:599:0:16421:8468:2305:0\n" + 
                "250-25 FAB;T-Systems:177500000:I999C34D12M16B7T8G8Y0:T:27500:3073:3074=deu:3079:0:16576:8468:772:0\n" + 
                "250-26 TV.Berlin;T-Systems:754000000:I999C23D12M16B8T8G8Y0:T:27500:3121:3122=deu:3127:0:16579:8468:774:0\n" + 
                "250-27 9Live;BetaDigital:754000000:I999C23D12M16B8T8G8Y0:T:27500:273:274=deu:279:0:16401:8468:774:0\n" + 
                "250-28 HSE-MonA,HSE/MonA TV;T-Systems:177500000:I999C34D12M16B7T8G8Y0:T:27500:49:50=deu:55:0:16387:8468:772:0\n" + 
                "250-29 STAR FM 87.9 MAXIMUM ROCK!,STAR FM;T-Systems:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:466=deu:0:0:24605:8468:2305:0\n" + 
                "250-30 radio ffn;T-Systems:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:2258=deu:0:0:26509:8468:2305:0\n" + 
                "250-31 OldieStar Radio,OldieStar;T-Systems:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:2242=deu:0:0:26508:8468:2305:0\n" + 
                "250-32 DefJay 100% R&B;T-Systems:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:2162=deu:0:0:26503:8468:2305:0\n" + 
                "250-33 Klassik Radio;T-Systems:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:338=deu:0:0:24597:8468:2305:0\n" + 
                "250-34 ENERGY BERLIN;T-Systems:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:2066=deu:0:0:26497:8468:2305:0\n" + 
                "250-35 TRUCKRADIO;TRUCKRADIO:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:354=deu:0:0:24598:8468:2305:0\n" + 
                "250-36 Radio Horeb;Eurociel:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:306=deu:0:0:24595:8468:2305:0\n" + 
                "250-37 ERF Radio,ERF;BetaDigital:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:322=deu:0:0:24596:8468:2305:0\n" + 
                "250-38 sunshine live,sunshine;BetaDigital:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:242=deu:0:0:24591:8468:2305:0\n" + 
                "250-39 ANTENNE BAYERN,ANTENNE;BetaDigital:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:450=deu:0:0:24604:8468:2305:0\n" + 
                "250-40 HIT RADIO FFH;T-Systems:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:226=deu:0:0:24590:8468:2305:0\n" + 
                "250-41 WILANTIS;T-Systems:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:194=deu:0:0:24588:8468:2305:0\n" + 
                "250-42 Euroklassik 1;TechniSat:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:146=deu:0:0:24585:8468:2305:0\n" + 
                "250-43 Radio Viola;TechniSat:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:162:0:0:24586:8468:2305:0\n" + 
                "250-44 StarSat Gold;TechniSat:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:82:0:0:24581:8468:2305:0\n" + 
                "250-45 StarSat Country;TechniSat:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:34:0:0:24578:8468:2305:0\n" + 
                "250-46 StarSat Hit-Express;TechniSat:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:98=deu:0:0:24582:8468:2305:0\n" + 
                "250-47 StarSat EASYTI;TechniSat:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:66:0:0:24580:8468:2305:0\n" + 
                "250-48 Kinderradio 1- Konzertsaal 1;TechniSat:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:210:0:0:24589:8468:2305:0\n" + 
                "250-49 Motor FM:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:274=deu:0:0:24593:8468:2305:0\n" + 
                "250-50 StarSat Melodie;TechniSat:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:50:0:0:24579:8468:2305:0\n" + 
                "250-51 RADIOROPA-BERLIN;TechniSat:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:178:0:0:24587:8468:2305:0\n" + 
                "250-52 Jazz Time;TechniSat:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:130:0:0:24584:8468:2305:0\n" + 
                "250-53 RADIOROPA-H÷rbuch 4;TechniSat:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:114:0:B00:24583:8468:2305:0\n" + 
                "250-54 InfoRadio;ARD rbb:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:386=deu:0:0:24600:8468:2305:0\n" + 
                "250-55 kulturradio;ARD rbb:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:434=deu:0:0:24603:8468:2305:0\n" + 
                "250-56 radioeins:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:418=deu:0:0:24602:8468:2305:0\n" + 
                "250-57 Fritz:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:402=deu:0:0:24601:8468:2305:0\n" + 
                "250-58 WDR 2;ARD WDR:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:482=deu:0:0:24606:8468:2305:0\n" + 
                "250-59 WDR 5;ARD WDR:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:498=deu:0:0:24607:8468:2305:0\n" + 
                "250-60 BluRadio - Dancemusic non-stop,BluRadio;T-Systems:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:2226=deu:0:0:26507:8468:2305:0\n" + 
                "250-61 RADIOROPA-H÷rbuch 2;TechniSat:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:546:0:B00:24610:8468:2305:0\n" + 
                "250-62 RADIOROPA-H÷rbuch 1;TechniSat:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:530:0:B00:24609:8468:2305:0\n" + 
                "250 63 RADIOROPA-H÷rbuch 3;TechniSat:778000000:I999C23D12M16B8T8G8Y0:T:27500:0:562:0:B00:24611:8468:2305:0");
    }

    private void sendWelcomeMessage() {
        String welcome = "220 M740AV SVDRP VideoDiskRecorder 1.4.6; Thu May  3 01:23:43 2007";
        ps.println(welcome);
    }

    public static void main(String[] args) {
        new Server();
    }
}