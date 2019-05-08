package com.aayushatharva.igi2getserverinfo;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

public class GetServerInfo {

    // Server Data
    private static String ServerName;
    private static String ServerMap;
    private static int Players;
    private static int MaxPlayers;
    private static String ServerUptime;
    private static String ServerMapTime;
    private static String ServerMapStat;
    private static String ServerPassword;

    // Server Address
    private static String ServerIP;
    private static int ServerPort;
    
    // Player Data
    private static String ID;
    private static String Nick;
    private static String Stats;
    private static String Ping;
    private static String Team;

    public static void main(String[] args) throws InterruptedException {

        try {

            getStatus(args[0], Integer.parseInt(args[1]));

        } catch (Exception ex) {

            Scanner getServerIPAndPort = new Scanner(System.in);
            echo("Enter IGI-2 Multiplayer Server IP");
            ServerIP = getServerIPAndPort.next();
            echo("Enter IGI-2 Multiplayer Server Port");
            ServerPort = getServerIPAndPort.nextInt();
            echo("");
            echo("Fetching IGI-2 Multiplayer Server Info From: " + ServerIP + ":" + ServerPort);
            echo("");
            getStatus(ServerIP, ServerPort);

        }

    }

    public static void getStatus(String ServerIP, int ServerPort) {

        try {
            String Packet;
            InetAddress servAddr = InetAddress.getByName(ServerIP);
            byte[] buf = "\\status\\".getBytes();
            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(5000);
            socket.send(new DatagramPacket(buf, buf.length, servAddr, ServerPort));
            buf = new byte[4096];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            socket.close();
            Packet = new String(packet.getData());
            setServer(Packet.trim());
        } catch (Exception ex) {
            echo("Server Not Responding");
        }

    }

    public static void setServer(String status) throws InterruptedException {

        if (status.length() <= 0) {
            return;
        }

        try {

            ServerName = status.substring(status.indexOf("hostname\\") + 9, status.indexOf("\\hostport"));

            ServerMap = status.substring(status.indexOf("mapname\\") + 8, status.indexOf("\\gametype"));

            Players = Integer.parseInt(status.substring(status.indexOf("numplayers\\") + 11, status.indexOf("\\maxplayers")));

            MaxPlayers = Integer.parseInt(status.substring(status.indexOf("maxplayers\\") + 11, status.indexOf("\\gamemode")));;

            int up = Integer.parseInt(status.substring(status.indexOf("uptime\\") + 7, status.indexOf("\\timeleft")));

            ServerUptime = (up / 60 + "h " + up % 60 + " m");

            ServerMapTime = status.substring(status.indexOf("timeleft\\") + 9, status.indexOf("\\mapstat"));

            ServerMapStat = status.substring(status.indexOf("mapstat\\") + 8, status.indexOf("\\timelimit")).replaceFirst("roundlimit_", "");

            String PassStatus = status.substring(status.indexOf("password\\") + 9, status.indexOf("\\team_t0"));

            if (PassStatus.equals("1")) {
                ServerPassword = "Yes";
            } else {
                ServerPassword = "No";
            }

            String StatData = "Server Name: " + ServerName + "\n"
                    + "Server Map: " + ServerMap + "\n"
                    + "Server Map Time: " + ServerMapTime
                    + "Server Map Stats: " + ServerMapStat
                    + "Players: " + Players + "\\" + MaxPlayers + "\n"
                    + "Server Uptime: " + ServerUptime + "\n"
                    + "Server Password: " + ServerPassword;

            echo("Server Info: "
                    + "\n"
                    + "-------------------------------------------------"
                    + "\n"
                    + StatData
                    + "\n"
                    + "-------------------------------------------------");

        } catch (StringIndexOutOfBoundsException ex) {
            echo("Server Info-Update Error: " + ex.getMessage());
            System.exit(1);
        }

        getPlayers(ServerIP, ServerPort);
    }

    public static void getPlayers(String ServerIP, int ServerPort) {
        try {

            String message;
            InetAddress servAddr = InetAddress.getByName(ServerIP);
            byte[] buf = "\\players\\".getBytes();

            DatagramSocket socket = new DatagramSocket();
            socket.setSoTimeout(5000);

            socket.send(new DatagramPacket(buf, buf.length, servAddr, ServerPort));
            buf = new byte[4096];
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            socket.receive(packet);
            socket.close();
            message = new String(packet.getData());
            message = message.trim();

            echo("\n" + "Players: ");
            echo("_____________________________________________" + "\n");

            if (message.length() == 66) {
                echo("No Players Found");
            } else {
                setNewPlayer(message);
            }

            echo("_____________________________________________" + "\n");

        } catch (Exception ex) {
            System.out.println("Player Info-Update Error");
            System.exit(1);
        }

    }

    public static void setNewPlayer(String status) throws InterruptedException {

//        while (status.lastIndexOf("player") > 0) {
        String sfrag = "frags_";
        String sdeath = "deaths_";
        String sping = "ping_";
        String steam = "team_";
        String splayer = "player_";

        if (!status.contains(splayer)) {
            return;
        }

        ID = status.substring(status.indexOf(splayer) + splayer.length(), status.indexOf("\\", status.indexOf(splayer) + splayer.length()));
        splayer = splayer + ID + "\\";
        sfrag = sfrag + ID + "\\";
        sdeath = sdeath + ID + "\\";
        sping = sping + ID + "\\";
        steam = steam + ID + "\\";

        Nick = status.substring(status.indexOf(splayer) + splayer.length(), status.indexOf(sfrag) - 1);

        Stats = status.substring(status.indexOf(sdeath) + sdeath.length(), status.indexOf(sping) - 1);

        Stats = Stats + "/" + status.substring(status.indexOf(sfrag) + sfrag.length(), status.indexOf(sdeath) - 1);

        Ping = status.substring(status.indexOf(sping) + sping.length(), status.indexOf(steam) - 1);

        Team = status.substring(status.indexOf(steam) + steam.length(), status.indexOf(steam) + steam.length() + 1);

        echo("ID: " + ID + " Nick: " + Nick + " Score: " + Stats + " Ping: " + Ping + " Team: " + Team);
        Thread.sleep(2000);

//        }
    }

    public static void echo(String msg) {
        System.out.println(msg);
    }

}
