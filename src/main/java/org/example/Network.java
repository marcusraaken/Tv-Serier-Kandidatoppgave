package org.example;

import java.util.ArrayList;
public class Network {
    String name;
    ArrayList<Serie> series = new ArrayList<>();
    Double avgRating;
    String topRated;
    Double topRating;
    Integer shows;
    public Network(String name){

        this.name = name;
    }
    public String returnName(){
        return name;
    }
    public void getSerie(Serie s){
        series.add(s);
    }
    public ArrayList returnSeries(){

        return series;
    }
    public void avgRating(Double avg){

        avgRating = avg;
    }
    public Double returnRating(){
        return avgRating;
    }
    public void topRating(Double top){

        topRating = top;
    }
    public Double returnTopRating(){

        return topRating;
    }
    public void topRated(String top){

        topRated = top;
    }
    public String returnTopRated(){

        return topRated;
    }
    public Integer returnShows(){
        shows = series.size();
        return shows;
    }

}
