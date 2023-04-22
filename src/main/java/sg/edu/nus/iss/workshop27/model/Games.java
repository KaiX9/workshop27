package sg.edu.nus.iss.workshop27.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.json.Json;
import jakarta.json.JsonObject;

public class Games {
    
    private Integer limit;
    private Integer offset;
    private Integer total;
    private LocalDateTime timeStamp;
    private List<Game> gameList = new ArrayList<Game>();

    public Games() {

    }

    public Games(Integer limit, Integer offset, Integer total, LocalDateTime timeStamp) {
        this.limit = limit;
        this.offset = offset;
        this.total = total;
        this.timeStamp = timeStamp;
    }

    public List<Game> getGameList() {
        return gameList;
    }
    public void setGameList(List<Game> gameList) {
        this.gameList = gameList;
    }
    public Integer getLimit() {
        return limit;
    }
    public void setLimit(Integer limit) {
        this.limit = limit;
    }
    public Integer getOffset() {
        return offset;
    }
    public void setOffset(Integer offset) {
        this.offset = offset;
    }
    public Integer getTotal() {
        return total;
    }
    public void setTotal(Integer total) {
        this.total = total;
    }
    public LocalDateTime getTimeStamp() {
        return timeStamp;
    }
    public void setTimeStamp(LocalDateTime timeStamp) {
        this.timeStamp = timeStamp;
    }

    @Override
    public String toString() {
        return "Games [limit=" + limit + ", offset=" + offset + ", total=" + total + ", timeStamp=" + timeStamp + "]";
    }
    
    public JsonObject toJSON() {

        return Json.createObjectBuilder()
                .add("games", getGameList().toString())
                .add("offset", getOffset())
                .add("limit", getLimit())
                .add("total", getTotal())
                .add("timestamp", getTimeStamp().toString())
                .build();
    }
}
