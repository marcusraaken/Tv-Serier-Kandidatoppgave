package org.example;

import com.google.gson.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;


public class Tasks {
    Scanner input = new Scanner(System.in);
    ArrayList<Serie> shows = new ArrayList<>();
    ArrayList<Network> networks = new ArrayList<>();
    public String filnavn(){
        System.out.println("Skriv inn filnavn, f.eks 'Vedlegg'");
        String fil = input.nextLine();
        System.out.println("Vent litt...");
        return fil;
    }
    public String rapport(){
        System.out.println("Skriv inn rapporten du ønsker å lese, eller 'exit' for å avslutte");
        System.out.println("Rapportene du kan velge mellom er:");
        System.out.println("'program' for å se hva som sendes neste uke");
        System.out.println("'topp 10' for å se de 10 seriene med høyest rangering");
        System.out.println("'topp episode' for å se den topprangerte episoden til hver serie");
        System.out.println("'topp nettverk' for å se det høyest rangerte TV-nettverket");
        System.out.println("'alle' for å se en oppsummering over alle serier i listen");

        String fil = input.nextLine();
        return fil;
    }

    public boolean readInn(String fil){
        try {
            Scanner showScan = new Scanner(new File(fil));

            String serie;
            String root = "https://api.tvmaze.com/singlesearch/shows?q=";
            String end = "&embed=episodes";

            long lastCall = System.currentTimeMillis();
            long waitTime = 500;

            while (showScan.hasNextLine()) {
                serie = showScan.nextLine();
                long timeNow = System.currentTimeMillis();
                long timeSinceLastCall = timeNow - lastCall;
                if (timeSinceLastCall < waitTime) {
                    try {
                        Thread.sleep(waitTime - timeSinceLastCall);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                JsonElement rootElem = apiCall(root+serie+end);
                JsonObject rootObj = rootElem.getAsJsonObject();
                createSerie(rootObj);
            }
            showScan.close();
        }
        catch (FileNotFoundException e) {
            System.out.println("fant ikke filen med det navnet. Prøv igjen");
            return false;
        }
        return true;
    }
    public JsonElement apiCall(String link) {

        URL url = null;
        try {
            url = new URL(link);
        } catch (MalformedURLException e) {
            System.out.println("feilskrevet tv-show " + link);
        }

        try {
            JsonParser parser = new JsonParser();
            HttpURLConnection connect = (HttpURLConnection) url.openConnection();
            Reader reader = new InputStreamReader(connect.getInputStream());
            JsonElement rootElem = parser.parse(reader);
            return(rootElem);
        }
        catch (IOException f) {
            System.out.println("error med parse");
        }
        return null;
    }
    public void createSerie(JsonObject showet){
        Integer id =  showet.get("id").getAsInt();
        String name = showet.get("name").getAsString();
        String summary = showet.get("summary").getAsString();

        Double rating = null;
        if (!showet.get("rating").getAsJsonObject().get("average").isJsonNull()){
            rating = showet.get("rating").getAsJsonObject().get("average").getAsDouble();
        }

        String imdbLink = null;
        try {
            imdbLink = "https://www.imdb.com/title/" + showet.get("externals").getAsJsonObject().get("imdb").getAsString();
        } catch(UnsupportedOperationException e){}

        String network = null;
        try{
            network = showet.get("network").getAsJsonObject().get("name").getAsString();
        } catch(UnsupportedOperationException  | IllegalStateException h){}

        JsonArray genresArrey = showet.get("genres").getAsJsonArray();
        ArrayList aGenres = new ArrayList();
        String genres = "";
        for (int i = 0; i < genresArrey.size(); i++) {
            aGenres.add(genresArrey.get(i).getAsString());
            genres += aGenres.get(0).toString() + ",";
        }

        ArrayList<JsonObject> episodes = new ArrayList();
        String bestEpisodeName = null;
        Double bestEpisodeRating = 0.0;
        String bestEpisodeNo = null;
        Integer releasedEpisodeCount = 0;

        JsonArray episodesJSon = showet.get("_embedded").getAsJsonObject().get("episodes").getAsJsonArray();
        for (int i = 0; i < episodesJSon.size(); i++) {

            JsonObject episode = episodesJSon.get(i).getAsJsonObject();
            episodes.add(episode);
            JsonElement episodeRatingE = episode.get("rating").getAsJsonObject().get("average");
            Double episodeRating = 0.0;

            try{
                episodeRating = episodeRatingE.getAsDouble();
            } catch (UnsupportedOperationException g){}

            if (episodeRating > bestEpisodeRating) {
                bestEpisodeRating = episodeRating;
                bestEpisodeName = episode.get("name").getAsString();
                Integer Iseason = 999;
                Integer Iepisode = 999;
                try{
                    Iseason = episode.get("season").getAsInt();
                } catch(NullPointerException e){}
                try{
                    Iepisode = episode.get("episode").getAsInt();
                }
                catch(NullPointerException f){
                    try{
                        Iepisode = episode.get("number").getAsInt();
                    }
                    catch(NullPointerException g) {}
                }

                if(Iseason < 10){
                    bestEpisodeNo = "S0" + Iseason;
                }
                else{
                    bestEpisodeNo = "S" + Iseason;
                }
                if(Iepisode < 10){
                    bestEpisodeNo += "E0" + Iepisode;
                }
                else{
                    bestEpisodeNo += "E" + Iepisode;
                }
            }
            if (compareDates(episode.get("airdate").getAsString())){
                releasedEpisodeCount++;
            }
        }
        Integer episodeCount = episodes.size();

/*
        database db = new database();
        db.add(id, name, rating, network, imdbLink, genres, summary, bestEpisodeName, bestEpisodeRating,
                bestEpisodeNo, episodeCount, releasedEpisodeCount);
*/

        Serie s = new Serie(id, name, rating, imdbLink, network, genres, summary, episodes, bestEpisodeName, bestEpisodeRating,
                bestEpisodeNo, episodeCount, releasedEpisodeCount);
        shows.add(s);
    }
    public void createNetwork(){
        String network;
        ArrayList networkArr = new ArrayList();

        for (int i = 0; i<shows.size();i++){
            network = shows.get(i).returnNetwork();
            if (networkArr.contains(network)){
                for(int j = 0; j < networks.size();j++){
                    if(networks.get(j).returnName() == network){
                        networks.get(j).getSerie(shows.get(i));
                    }
                }
            }
            else{
                Network n = new Network(network);
                n.getSerie(shows.get(i));
                networkArr.add(network);
                networks.add(n);
            }
        }

        for (int i = 0; i<networks.size();i++) {
            Integer numbers = 0;
            Double sum = 0.0;
            String topRated = "";
            Double topRating = 0.0;
            ArrayList<Serie> series = networks.get(i).returnSeries();

            for (int j = 0; j < series.size(); j++) {
                if(series.get(j).returnRating() != null) {

                    Double rating = series.get(j).returnRating();
                    sum += rating;
                    numbers++;

                    if (rating > topRating) {
                        topRating = rating;
                        topRated = series.get(j).returnName();
                    }
                }
            }
            networks.get(i).avgRating(sum / numbers);
            networks.get(i).topRated(topRated);
            networks.get(i).topRating(topRating);
        }
    }
    void nextWeek(){
        Calendar today = Calendar.getInstance();
        int day = today.get(Calendar.DAY_OF_WEEK);
        if (day != Calendar.MONDAY) {
            int days = (Calendar.SATURDAY - day + 2) % 7;
            today.add(Calendar.DAY_OF_YEAR, days);
        }

        Date date = today.getTime();
        String format = new SimpleDateFormat("yyyy-MM-dd").format(date);
        Integer Iday = 0;
        addShedule(format, Iday);
        for (int i = 0; i < 6; i++) {
            Iday++;
            today.add(Calendar.DAY_OF_YEAR, 1);
            date = today.getTime();
            format = new SimpleDateFormat("yyyy-MM-dd").format(date);
            addShedule(format, Iday);
        }
    }
    Boolean compareDates(String releaseDate){

        String airdate = releaseDate;
        Calendar today = Calendar.getInstance();
        Date date = today.getTime();

        String format = new SimpleDateFormat("yyyyMMdd").format(date);
        Integer todayDate = Integer.parseInt(format);
        airdate = airdate.replace("-", "");

        if (!airdate.equals("")) {
            Integer airDate = Integer.parseInt(airdate);
            if (todayDate > airDate) {
                return true;
            } else {
                return false;
            }
        }
        else {
            return false;
        }
    }
    public void addShedule(String day, Integer dayNumb){
        JsonElement dayShedule = apiCall("https://api.tvmaze.com/schedule?date="+day);
        JsonArray shedule = dayShedule.getAsJsonArray();

        for (int i = 0; i< shows.size(); i++){
            String episode = "";
            Integer serieId = shows.get(i).returnId();

            for(int j = 0; j < shedule.size(); j++){
                if (shedule.get(j).getAsJsonObject().get("show").getAsJsonObject().get("id").getAsInt() == serieId) {
                    Integer season = -1;

                    try{
                        season = shedule.get(j).getAsJsonObject().get("season").getAsInt();
                    }
                    catch(NullPointerException e){
                    }

                    Integer Iepisode = -1;
                    try{
                        Iepisode = shedule.get(j).getAsJsonObject().get("episode").getAsInt();
                    }
                    catch(NullPointerException f){
                        try{
                            Iepisode = shedule.get(j).getAsJsonObject().get("number").getAsInt();
                        }
                        catch(NullPointerException g) {
                        }
                    }

                    if (season == -1){
                        episode += "S";
                    }
                    else if(season < 10){
                        episode = "S0" + season.toString();
                    }
                    else{
                        episode = "S" + season.toString();
                    }
                    if(Iepisode == -1){
                        episode += "E";
                    }
                    else if(Iepisode < 10){
                        episode += "E0" + Iepisode.toString();
                    }
                    else{
                        episode = episode + "E" + Iepisode.toString();
                    }
                }
            }
            shows.get(i).nextWeek(episode, dayNumb);
        }
    }
    public String showNextWeek(){
        String schedule = "";
        for(int i = 0; i<shows.size();i++){
            if(!shows.get(i).returnNextWeek().equals(";;;;;;;")){
                schedule += shows.get(i).returnName() + shows.get(i).returnNextWeek();
            }
        }
        return schedule;
    }
    public void writeNextweek()throws IOException{
        File rapport = new File("Next_Week.txt");
        FileWriter fw = new FileWriter(rapport);
        PrintWriter pw = new PrintWriter(fw);
        pw.println("SHOW_NAME;MONDAY;TUESDAY;WEDENSDAY;THURSDAY;FRIDAY;SATURDAY;SUNDAY");

        for(int i = 0; i<shows.size();i++){
            String schedule = "";
            if(!shows.get(i).returnNextWeek().equals(";;;;;;;")){
                schedule += shows.get(i).returnName() + shows.get(i).returnNextWeek();
                pw.println(schedule);
            }
        }
        pw.close();
    }
    public void writeTop10() throws IOException{
        File rapport = new File("top10.txt");
        FileWriter fw = new FileWriter(rapport);
        PrintWriter pw = new PrintWriter(fw);
        pw.println("SHOW_NAME;SHOW_RATING");
        String[] showName = new String[10];
        double[] showRating = new double[10];

        for(int i = 0; i<shows.size();i++){
            for(int j = 0;j<10;j++) {
                if (shows.get(i).returnRating() != null && shows.get(i).returnRating() > showRating[j]) {
                    showName[j] = shows.get(i).returnName();
                    showRating[j] = shows.get(i).returnRating();
                    break;
                }
            }
        }
        for(int i = 0;i<10;i++) {
            pw.println(showName[i] + ";" + showRating[i]);
        }
        pw.close();
    }
    public void writeNetwork() throws IOException {
        PriorityQueue<Network> topNetwork = new PriorityQueue<>((n1, n2) -> Double.compare(n2.returnRating(), n1.returnRating()));
        topNetwork.addAll(networks);

        File rapport = new File("top_Network.txt");
        FileWriter fw = new FileWriter(rapport);
        PrintWriter pw = new PrintWriter(fw);
        pw.println("AVERAGE_RATING;NETWORK;TOP_RATED_SHOW;TOP_RATING;SHOW_COUNT");

        while (!topNetwork.isEmpty()) {
            Network n = topNetwork.poll();
            pw.println(n.returnRating() + ";" + n.returnName() + ":" + n.returnTopRated()
                    + ";" + n.returnRating() + ";" + n.returnShows());
        }
        pw.close();
    }
    public void writeSummary() throws IOException{
        File rapport = new File("Summary.txt");
        FileWriter fw = new FileWriter(rapport);
        PrintWriter pw = new PrintWriter(fw);
        pw.println("SHOW_NAME;NETWORK;GENRES;EPISODE_COUNT;RELEASED_EPISODE_COUNT");
        for (Serie s : shows) {
            pw.println(s.returnName() + ";" + s.returnNetwork() + ";" + s.returnGenres() + ";" + s.returnEC() + ";" + s.returnREC());
        }
        pw.close();
    }
    public void writeBestEpisode() throws IOException{

        PriorityQueue<Serie> topEpisodes = new PriorityQueue<>((n1, n2) -> Double.compare(n2.returnEpisodeRating(), n1.returnEpisodeRating()));
        topEpisodes.addAll(shows);
        File rapport = new File("top_Episode.txt");
        FileWriter fw = new FileWriter(rapport);
        PrintWriter pw = new PrintWriter(fw);
        pw.println("SHOW_NAME;NETWORK;GENRES;SEASON_NUMBER;EPISODE_NUMBER;EPISODE_NAME;RATING;");

        while (!topEpisodes.isEmpty()) {
            Serie epi = topEpisodes.poll();
            pw.println(epi.returnName() + ";" + epi.returnNetwork() + ";" + epi.returnGenres() + ";" + epi.returnEpisodeNo()
                    + ";" + epi.returnEpisodeName() + ";" + epi.returnEpisodeRating());
        }
        pw.close();
    }
}

