package sg.edu.nus.iss.workshop27.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sg.edu.nus.iss.workshop27.model.EditedComment;
import sg.edu.nus.iss.workshop27.model.Game;
import sg.edu.nus.iss.workshop27.model.Review;
import sg.edu.nus.iss.workshop27.repository.BoardGameRepository;

@Service
public class BoardGameService {
    
    @Autowired
    private BoardGameRepository bgRepo;

    public List<Game> getAllGames(Integer limit, Integer offset) {
        return bgRepo.getAllBoardGames(limit, offset);
    }

    public Game getBoardGameById(String gameId) {
        return bgRepo.getBoardGameById(gameId);
    }

    public List<Game> getSortedByRank(Integer limit, Integer offset) {
        return bgRepo.getSortedByRank(limit, offset);
    }

    public Review insertReview(Review r) {
        
        try {
        System.out.println("Here>>> " + r.getGid());
        Game g = this.getBoardGameById(r.getGid().toString());
        System.out.println(g);
        UUID uuid = UUID.randomUUID();
        String cid = uuid.toString().substring(0, 8);
        r.setName(g.getName());
        r.setPosted(LocalDateTime.now());
        r.setC_id(cid);
        return bgRepo.insertComment(r);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Review getReviewById(String c_id) {
        return this.bgRepo.getReviewById(c_id);
    }

    public long updateReview(EditedComment ec) {
        try {
            Review existingRecord = this.bgRepo.getReviewByIdWithEdited(ec.getC_id());
            List<EditedComment> ll = existingRecord.getEdited();
            
            System.out.println("New comment -> " + ec.getC_text());
            System.out.println("Existing comment -> " + existingRecord.getC_text());
            if (existingRecord.getEdited() == null) {
                ll = new ArrayList<EditedComment>();
                existingRecord.setEdited(ll);
            }
            
            EditedComment e = new EditedComment();
            e.setC_text(existingRecord.getC_text());
            e.setPosted(existingRecord.getPosted());
            e.setRating(existingRecord.getRating());
            existingRecord.getEdited().add(e);

            existingRecord.setC_text(ec.getC_text());
            existingRecord.setPosted(LocalDateTime.now());
            existingRecord.setRating(ec.getRating());
            return this.bgRepo.updateReview(existingRecord);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public Review getAndDisplayLatestReview(String c_id) {
        return this.bgRepo.getAndDisplayLatestReview(c_id);
    }

    public Review getAllHistoricalReviews(String c_id) {
        return this.bgRepo.getHistoricalReviewsById(c_id);
    }
}
