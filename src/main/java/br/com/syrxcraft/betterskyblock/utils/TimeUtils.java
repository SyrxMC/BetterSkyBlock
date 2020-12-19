package br.com.syrxcraft.betterskyblock.utils;

public class TimeUtils {

    public static String formatSec(int secondsLeft){

        int dias = 0;
        int horas = 0;
        int minutos = 0;
        int segundos = 0;


        if (secondsLeft >= 86400){
            dias = secondsLeft / 86400;
            secondsLeft = secondsLeft % 86400;
        }
        if (secondsLeft >= 3600){
            horas = secondsLeft / 3600;
            secondsLeft = secondsLeft % 3600;
        }
        if (secondsLeft >= 60){
            minutos = secondsLeft / 60;
            secondsLeft = secondsLeft % 60;
        }
        segundos = secondsLeft;
        String message = "";

        if (dias > 0){
            message = dias + " Dias";
        }
        if (horas > 0){
            message = message + ((message.isEmpty()) ? "" : " ") + horas +" Horas";
        }
        if (minutos > 0){
            message = message + ((message.isEmpty()) ? "" : " ") + minutos +" Min";
        }
        if (segundos >= 0){
            message = message + ((message.isEmpty()) ? "" : " ") + segundos +" Seg";
        }

        return message;
    }

}
