package org.example;

import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {

        Tasks lesInn = new Tasks();
        boolean gyldig_fil = false;
        while (!gyldig_fil) {
            String vedlegg = lesInn.filnavn();
            gyldig_fil = lesInn.readInn(vedlegg + ".txt");
        }
        lesInn.createNetwork();
        lesInn.nextWeek();
        String rapport = "";

        while (!rapport.equals("exit")) {
            rapport = lesInn.rapport();
            if (rapport.equals("program")) {
                lesInn.writeNextweek();
            } else if (rapport.equals("topp 10")) {
                lesInn.writeTop10();
            } else if (rapport.equals("topp nettverk")) {
                lesInn.writeNetwork();
            } else if (rapport.equals("alle")) {
                lesInn.writeSummary();
            } else if (rapport.equals("topp episode")) {
                lesInn.writeBestEpisode();
            } else if (rapport.equals("exit")) {
                break;
            } else {
                System.out.println("Den rapporten finnes ikke. Sjekk at du har skrevet inn riktig og prøv igjen.");
            }
        }
    }
}
