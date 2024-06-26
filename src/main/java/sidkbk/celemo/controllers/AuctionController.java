package sidkbk.celemo.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sidkbk.celemo.dto.auctions.AuctionCreationDTO;
import sidkbk.celemo.dto.auctions.AuctionIdDTO;
import sidkbk.celemo.dto.auctions.AuctionUpdateDTO;
import sidkbk.celemo.exceptions.EntityNotFoundException;
import sidkbk.celemo.models.Auction;
import sidkbk.celemo.services.AuctionService;

import java.util.List;


@RestController
@RequestMapping(value = "/api/auction")
public class AuctionController {

    @Autowired
    AuctionService auctionService;

// PUBLIC
//////////////////////////////////////////////////////////////////////////////////////


    // Get all auctions
    @GetMapping("/find/all")
    public ResponseEntity<?> getAllAuctions() {
        try {
            return ResponseEntity.ok(auctionService.getAllAuctions());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
    // Get all auctions from user

    @GetMapping("/find/all/user/{userId}")
    public ResponseEntity<?> getAllAuctionsFromUser(@PathVariable("userId") String userId) {

        List<Auction> foundAuctions = auctionService.getAllAuctionsFromUser(userId);
        if (foundAuctions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not have any auctions...");
        } else {
            return ResponseEntity.ok(foundAuctions);
        }

    }


// USER
//////////////////////////////////////////////////////////////////////////////////////

    // POST create new auction
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<?> createAuction(@Valid @RequestBody AuctionCreationDTO auctionCreationDTO) {
        try {
            return ResponseEntity.ok(auctionService.createAuction(auctionCreationDTO));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }



    // PUT update an auction
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/update")
    public ResponseEntity<?> updateAuction(@Valid @RequestBody AuctionUpdateDTO auctionUpdateDTO) {
        try {
            return ResponseEntity.ok(auctionService.updateAuction(auctionUpdateDTO));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // DELETE an auction
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteAuction(@Valid @RequestBody AuctionIdDTO auctionIdDTO) {
        return auctionService.deleteAuction(auctionIdDTO);
    }

    // GET one auction
    @GetMapping("/find-one/{auctionId}")
    public ResponseEntity<?> getAuction(@PathVariable("auctionId") String auctionId) {
        try {
            return ResponseEntity.ok(auctionService.getOneAuction(auctionId));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }




    // GET one auction with time left
    @GetMapping("/find-one/timeleft/{auctionId}")
    public ResponseEntity<?> getOneAuctionTimeleft(@PathVariable String auctionId) {
        try {
            return ResponseEntity.ok(auctionService.getOneAuctionTimeleft(auctionId));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
// ADMIN
//////////////////////////////////////////////////////////////////////////////////////


    //REMOVE BEFORE PRODUCTION
    @DeleteMapping("/dev/deleteAll")
    public void deleteAllAuctions() {
        auctionService.deleteAllAuctions();
    }
}
