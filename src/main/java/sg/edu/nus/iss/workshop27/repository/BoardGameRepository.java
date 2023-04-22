package sg.edu.nus.iss.workshop27.repository;

import java.util.List;

import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

import com.mongodb.client.result.UpdateResult;

import sg.edu.nus.iss.workshop27.model.Game;
import sg.edu.nus.iss.workshop27.model.Review;

@Repository
public class BoardGameRepository {
    
    @Autowired
    MongoTemplate mongoTemplate;

    public static String name;

    public List<Game> getAllBoardGames(Integer limit, Integer offset) {
        
        Query query = new Query();
        Pageable pageable = PageRequest.of(offset, limit);

        query.with(pageable);

        return mongoTemplate.find(query, Document.class, "games")
                            .stream()
                            .map(d -> Game.createFromDoc(d))
                            .toList();
    }

    public List<Game> getSortedByRank(Integer limit, Integer offset) {
        
        Query query = new Query();
        query.addCriteria(Criteria.where("ranking").exists(true));
        query.with(Sort.by(Sort.Direction.ASC, "ranking"));
        query.limit(limit).skip(offset);

        return mongoTemplate.find(query, Document.class, "games")
                            .stream()
                            .map(d -> Game.createFromDoc(d))
                            .toList();
    }

    public Game getBoardGameById(String gameId) {

        try {
        Query query = new Query();
        System.out.println("GameId>>> " + gameId);
        if (ObjectId.isValid(gameId)) {
            query.addCriteria(Criteria.where("_id").is(gameId));
        } else {
            System.out.println("Searching for gid... " + gameId);
            query.addCriteria(Criteria.where("gid").is(Integer.parseInt(gameId)));
        }

        return mongoTemplate.find(query, Document.class, "games")
                            .stream()
                            .map(d -> Game.createFromDoc(d))
                            .findFirst()
                            .get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Review insertComment(Review r) {

        return mongoTemplate.insert(r, "reviews");

    }

    public Review getReviewById(String c_id) {
        try {
        Query query = new Query();
        query.addCriteria(Criteria.where("c_id").is(c_id));

        return mongoTemplate.find(query, Document.class, "reviews")
                            .stream()
                            .map(d -> Review.createFromDoc(d))
                            .findFirst()
                            .get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Review getReviewByIdWithEdited(String c_id) {
        try {
        Query query = new Query();
        query.addCriteria(Criteria.where("c_id").is(c_id));

        return mongoTemplate.find(query, Document.class, "reviews")
                            .stream()
                            .map(d -> Review.createFromDocIfEditedIsPresent(d))
                            .findFirst()
                            .get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public long updateReview(Review r) {
        Query query = new Query();
        query.addCriteria(Criteria.where("c_id").is(r.getC_id()));

        Update updateOps = new Update()
                        .set("rating", r.getRating())
                        .set("c_text", r.getC_text())
                        .set("posted", r.getPosted())
                        .set("edited", r.getEdited());
        
        UpdateResult updResult = mongoTemplate.updateMulti(query, updateOps
            , Review.class, "reviews");

        return updResult.getModifiedCount();
    }

    public Review getAndDisplayLatestReview(String c_id) {

        try {
            getGameName(c_id);        
            Query query = new Query();
            query.addCriteria(Criteria.where("c_id").is(c_id));

            return mongoTemplate.findOne(query, Review.class, "reviews");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Review getHistoricalReviewsById(String c_id) {
        try {
            getGameName(c_id);
            Query query = new Query();
            query.addCriteria(Criteria.where("c_id").is(c_id));

            return mongoTemplate.find(query, Document.class, "reviews")
                                .stream()
                                .map(d -> Review.createFromDocIfEditedIsPresent(d))
                                .findFirst()
                                .get();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getGameName(String c_id) {

        Query q1 = new Query();
        q1.addCriteria(Criteria.where("c_id").is(c_id));
        q1.fields().include("gid");
        List<Review> r = mongoTemplate.find(q1, Review.class, "reviews");
        Integer gid = r.get(0).getGid();

        Query q2 = new Query();
        q2.addCriteria(Criteria.where("gid").is(gid));
        q2.fields().include("name");
        List<Game> g = mongoTemplate.find(q2, Game.class, "games");
        name = g.get(0).getName();
        return name;
    }
}