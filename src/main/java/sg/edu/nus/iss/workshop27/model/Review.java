package sg.edu.nus.iss.workshop27.model;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import sg.edu.nus.iss.workshop27.repository.BoardGameRepository;

public class Review implements Serializable {
    
    private String _id;
    private String c_id;
    private String user;
    private Integer rating;
    private String c_text;
    private Integer gid;
    private LocalDateTime posted;
    private String name;

    private List<EditedComment> edited;

    public List<EditedComment> getEdited() {
        return edited;
    }
    public void setEdited(List<EditedComment> edited) {
        this.edited = edited;
    }
    public String get_id() {
        return _id;
    }
    public void set_id(String _id) {
        this._id = _id;
    }
    public String getC_id() {
        return c_id;
    }
    public void setC_id(String c_id) {
        this.c_id = c_id;
    }
    public String getUser() {
        return user;
    }
    public void setUser(String user) {
        this.user = user;
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
    public Integer getGid() {
        return gid;
    }
    public void setGid(Integer gid) {
        this.gid = gid;
    }
    public LocalDateTime getPosted() {
        return posted;
    }
    public void setPosted(LocalDateTime posted) {
        this.posted = posted;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Review [_id=" + _id + ", c_id=" + c_id + ", user=" + user + ", rating=" + rating + ", c_text=" + c_text
                + ", gid=" + gid + ", posted=" + posted + ", name=" + name + ", edited=" + edited + "]";
    }

    public static Review createFromDoc(Document d) {
        Review review = new Review();
        review.set_id(d.getObjectId("_id").toString());
        review.setGid(d.getInteger("gid"));
        review.setC_text(d.getString("c_text"));
        review.setRating(d.getInteger("rating"));
        review.setUser(d.getString("user"));
        review.setPosted(LocalDateTime.now());
        review.setC_id(d.getString("c_id"));

        return review;
    }

    public static Review createFromDocIfEditedIsPresent(Document d) {
        Review review = new Review();
        review.set_id(d.getObjectId("_id").toString());
        review.setGid(d.getInteger("gid"));
        review.setC_text(d.getString("c_text"));
        review.setRating(d.getInteger("rating"));
        review.setUser(d.getString("user"));
        review.setPosted(LocalDateTime.now());
        review.setC_id(d.getString("c_id"));

        List<Document> list = d.getList("edited", Document.class);
        List<EditedComment> editedComments = new ArrayList<>();
        if (list != null) {
            for (Document doc : list) {
                String c_text = doc.getString("c_text");
                Integer rating = doc.getInteger("rating");
                LocalDateTime posted;
                System.out.println("Does posted exist? >>> " + doc.containsKey("posted"));
                if (doc.containsKey("posted")) {
                    posted = doc.getDate("posted").toInstant()
                        .atZone(ZoneId.systemDefault()).toLocalDateTime();
                } else {
                    posted = null;
                }
                EditedComment editedComment = new EditedComment(rating, c_text, posted);
                editedComments.add(editedComment);
            }
        } else {
            editedComments = null;
        }
        review.setEdited(editedComments);
        return review;
    }

    public JsonObject toJSON() {
        return Json.createObjectBuilder()
                .add("gid", getGid())
                .add("c_text", getC_text())
                .add("rating", getRating())
                .add("user", getUser())
                .add("posted", getPosted().toString())
                .add("name", getName())
                .add("c_id", getC_id())
                .build();
    }

    public JsonObject toJSONWithEdited() {

        boolean isEdited;
        if (this.getEdited() != null) {
            isEdited = true;            
        } else {
            isEdited = false;
        }

        return Json.createObjectBuilder()
                .add("user", getUser())
                .add("rating", getRating())
                .add("comment", getC_text())
                .add("ID", getGid())
                .add("posted", (getPosted() != null)
                    ? getPosted().toString() : "N/A")
                .add("name", BoardGameRepository.name)
                .add("edited", isEdited)
                .add("timestamp", LocalDateTime.now().toString())
                .build();
    }

    public JsonObject toJSONHistoricalReviews() {

        return Json.createObjectBuilder()
                .add("user", getUser())
                .add("rating", getRating())
                .add("comment", getC_text())
                .add("ID", getGid())
                .add("posted", (getPosted() != null)
                    ? getPosted().toString() : "N/A")
                .add("name", BoardGameRepository.name)
                .add("edited", (getEdited() != null)
                    ? getEdited().toString() : "N/A")
                .add("timestamp", LocalDateTime.now().toString())
                .build();
    }
    
}
