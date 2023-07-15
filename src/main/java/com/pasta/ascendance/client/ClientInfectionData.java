package com.pasta.ascendance.client;

public class ClientInfectionData {

    public static int playerInfection;

    public static void set(int infection){
        ClientInfectionData.playerInfection = infection;
    }

    public static int getPlayerInfection(){
        return playerInfection;
    }
}
