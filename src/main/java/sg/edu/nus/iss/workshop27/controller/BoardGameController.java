package sg.edu.nus.iss.workshop27.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.json.JsonObject;
import sg.edu.nus.iss.workshop27.model.EditedComment;
import sg.edu.nus.iss.workshop27.model.Game;
import sg.edu.nus.iss.workshop27.model.Games;
import sg.edu.nus.iss.workshop27.model.Review;
import sg.edu.nus.iss.workshop27.repository.BoardGameRepository;
import sg.edu.nus.iss.workshop27.service.BoardGameService;

@RestController
@RequestMapping
public class BoardGameController {
    
    @Autowired
    private BoardGameRepository bgRepo;
    @Autowired
    private BoardGameService bgSvc;

    // To fetch all board games

    @GetMapping(path="/games")
    public ResponseEntity<String> getAllBoardGames(@RequestParam(required=false) Integer limit
        , @RequestParam(required=false) Integer offset) {

        if (limit == null) {
            limit = 25;
        }

        if (offset == null) {
            offset = 0;
        }

        List<Game> gameList = bgRepo.getAllBoardGames(limit, offset);

        Games games = new Games();
        games.setGameList(gameList);
        games.setOffset(offset);
        games.setLimit(limit);
        games.setTotal(gameList.size());
        games.setTimeStamp(LocalDateTime.now());

        JsonObject result = games.toJSON();

        return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(result.toString());
    }

    @GetMapping(path="/games/rank")
    public ResponseEntity<String> getSortedByRank(@RequestParam(required=false) Integer limit
        , @RequestParam(required=false) Integer offset) {

        if (limit == null) {
            limit = 25;
        }
    
        if (offset == null) {
            offset = 0;
        }

        List<Game> gameListByRank = bgRepo.getSortedByRank(limit, offset);
        
        Games games = new Games();
        games.setGameList(gameListByRank);
        games.setOffset(offset);
        games.setLimit(limit);
        games.setTotal(gameListByRank.size());
        games.setTimeStamp(LocalDateTime.now());

        JsonObject result = games.toJSON();

        return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(result.toString());
    }

    @GetMapping(path="/games/{gameId}")
    public ResponseEntity<String> getBoardGameById(@PathVariable String gameId) {

        Game game = bgRepo.getBoardGameById(gameId);

        if (game == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body("Game with game id -> %s not found".formatted(gameId));
        }
        return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(game.toJSON().toString());
    }

    @PostMapping(path="/review", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> postReview(@ModelAttribute Review review) {

        Review insertedReview = null;
        
        insertedReview = bgSvc.insertReview(review);
        if (insertedReview == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body("Game with game id --> %s not found".formatted(review.getGid()));
        }
        return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(insertedReview.toJSON().toString());

    }

    @PutMapping(path="/review/{review_id}")
    public ResponseEntity updateReview(@RequestBody EditedComment ec
        , @PathVariable String review_id) {
        
        ec.setC_id(review_id);
        long modifiedReview = bgSvc.updateReview(ec);
        if (modifiedReview == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body("Review with comment id --> %s not found".formatted(review_id));
        }
        return ResponseEntity.ok(modifiedReview);
    }

    @GetMapping(path="/review/{review_id}")
    public ResponseEntity<String> getAndDisplayLatestReview(@PathVariable String review_id) {

        Review review = this.bgSvc.getAndDisplayLatestReview(review_id);

        if (review == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body("Review with comment id -> %s not found".formatted(review_id)); 
        }
        return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(review.toJSONWithEdited().toString());
    }

    @GetMapping(path="/review/{review_id}/history")
    public ResponseEntity<String> getAllHistoricalReviews(@PathVariable String review_id) {

        Review review = this.bgSvc.getAllHistoricalReviews(review_id);

        if (review == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .contentType(MediaType.APPLICATION_JSON)
                                .body("Review with comment id -> %s not found".formatted(review_id)); 
        }
        return ResponseEntity.status(HttpStatus.OK)
                            .contentType(MediaType.APPLICATION_JSON)
                            .body(review.toJSONHistoricalReviews().toString());
    }

}