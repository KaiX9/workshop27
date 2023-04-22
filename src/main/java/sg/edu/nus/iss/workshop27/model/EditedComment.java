package sg.edu.nus.iss.workshop27.model;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.json.Json;
import jakarta.json.JsonObjectBuilder;

public class EditedComment implements Serializable {

    private Integer rating;
    private String c_text;
    private LocalDateTime posted;
    private String c_id;

    public EditedComment() {
        
    }

    public EditedComment(Integer rating, String c_text, LocalDateTime posted) {
        this.rating = rating;
        this.c_text = c_text;
        this.posted = posted;
    }

    public Integer getRating() {
        return rating;
    }
    public void setRating(Integer rating) {
        this.rating = rating;
    }
    public String getC_text() {
        return c_text;
    }
    public void setC_text(String c_text) {
        this.c_text = c_text;
    }
    public LocalDateTime getPosted() {
        return posted;
    }
    public void setPosted(LocalDateTime posted) {
        this.posted = posted;
    }
    public String getC_id() {
        return c_id;
    }
    public void setC_id(String c_id) {
        this.c_id = c_id;
    }

    @Override
    public String toString() {
        return "{comment=" + c_text + ", rating=" + rating + ", posted=" + posted + "}";
    }

    public JsonObjectBuilder toJSON() {
        return Json.createObjectBuilder()
                    .add("c_text", getC_text())
                    .add("rating", getRating())
                    .add("posted", getPosted().toString());
    }

}
