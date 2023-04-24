package org.example;
import java.util.ArrayList;

public class Serie {
    private Integer id;
    private String name;
    private Double rating;
    private String network;
    private String summary;
    private String imdbLink;
    private String bestEpisodeName;
    private Double bestEpisodeRating;
    private ArrayList episodes;
    private String bestEpisodeNo;
    private Integer episodeCount;
    private Integer releasedEpisodeCount;
    private String shedule;
    private String genres;


    public Serie(Integer id, String name, Double rating, String imdbLink, String network, String genres, String summary, ArrayList episodes,
                 String bestEpisodeName, Double bestEpisodeRating, String bestEpisodeNo, Integer episodeCount, Integer releasedEpisodeCount){
        this.id = id;
        this.name = name;
        this.rating = rating;
        this.imdbLink = imdbLink;
        this.network = network;
        this.summary = summary;
        this.episodes = episodes;
        this.bestEpisodeName = bestEpisodeName;
        this.bestEpisodeRating = bestEpisodeRating;
        this.bestEpisodeNo = bestEpisodeNo;
        this.episodeCount = episodeCount;
        this.releasedEpisodeCount = releasedEpisodeCount;
        this.shedule = "";
        this.genres = genres;

    }
    public void nextWeek(String episode, Integer day){
        shedule += ";" + episode;
    }
    public String returnNextWeek(){
        return shedule;
    }
    public Integer returnId(){

        return id;
    }
    public String returnName(){
        return name;
    }
    public Double returnRating(){
        return rating;
    }
    public String returnNetwork(){
        return network;
    }
    public String returnSummary(){ return summary;  }
    public String returnImdb(){ return imdbLink;  }
    public Integer returnEC(){
        return episodeCount;
    }
    public Integer returnREC(){
        return releasedEpisodeCount;
    }
    public String returnGenres(){
        return genres;
    }
    public String returnEpisodeName(){
        return bestEpisodeName;
    }
    public Double returnEpisodeRating(){
        return bestEpisodeRating;
    }
    public String returnEpisodeNo(){
        return bestEpisodeNo;
    }
}
