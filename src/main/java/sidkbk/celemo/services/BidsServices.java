package sidkbk.celemo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sidkbk.celemo.dto.Bids.BidsDTO;
import sidkbk.celemo.dto.Bids.FindBidIdDTO;
import sidkbk.celemo.models.User;
import sidkbk.celemo.models.Auction;
import sidkbk.celemo.models.Bids;
import sidkbk.celemo.repositories.UserRepository;
import sidkbk.celemo.repositories.AuctionRepository;
import sidkbk.celemo.repositories.BidsRepository;

import java.util.List;

@Service
public class BidsServices {

    @Autowired
    BidsRepository bidsRepository;

    @Autowired
    AuctionRepository auctionRepository;

    @Autowired
    UserRepository userRepository;



// Find all bids
    public List<Bids>findAllBids(){
        return bidsRepository.findAll();
    }




    // Create a bids using price, userId and listingId
    public ResponseEntity<?> createBids(BidsDTO bidsDTO){
        // gets DTO, checks user from user-repo
        User foundUser = userRepository.findById(bidsDTO.getUserId())
                .orElseThrow(()-> new RuntimeException("User does not exist!"));
        // gets DTO, checks auction id from auction-repo
        Auction foundAuction = auctionRepository.findById(bidsDTO.getAuctionId())
                .orElseThrow(()-> new RuntimeException("Auction does not exist!"));

        // makes new bid object
        Bids newBid = new Bids();
        // sets user found from DTO ID
        newBid.setUser(foundUser);
        // sets auction from found bid on auction which might try to find the bid from the auction and the auction has the bid
        //newBid.setAuction(foundAuction);

        newBid.setStartPrice(bidsDTO.getStartPrice());

        newBid.setMaxPrice(bidsDTO.getMaxPrice());

        // Checks if users balance is valid
        if (bidsDTO.getMaxPrice() > foundUser.getBalance()){
            throw new RuntimeException("Your max bid can not be higher than " + foundUser.getBalance() + " , your current balance." + bidsDTO.getMaxPrice());
        }
        if (foundUser.getBalance() < newBid.getStartPrice()){
            throw new RuntimeException("Your bid cannot be higher than your balance. Your current balance is " + foundUser.getBalance() + "Your current bid is " + bidsDTO.getStartPrice() + ".");
        }

        // user loses
        if (foundAuction.isHasBids()){
            if (foundAuction.getBid().getId().equals(newBid.getUser().getId())) {
                Bids auctionCurrentBid = bidsRepository.findById(foundAuction.getBid().getId()).get();

                User currentBidUser = userRepository.findById(auctionCurrentBid.getId()).get();

                // checks if new bid is less than the current
                if (newBid.getMaxPrice() < auctionCurrentBid.getMaxPrice()) {
                    // Raises by 10 if possible
                    if (newBid.getMaxPrice() + 10 <= auctionCurrentBid.getMaxPrice()) {
                        auctionCurrentBid.setCurrentPrice(newBid.getMaxPrice() + 10);
                    } else {
                        auctionCurrentBid.setCurrentPrice(auctionCurrentBid.getMaxPrice());
                    }
                    bidsRepository.save(auctionCurrentBid);
                    foundAuction.setCurrentPrice(auctionCurrentBid.getCurrentPrice());
                    auctionRepository.save(foundAuction);
                    return ResponseEntity.ok(newBid.getMaxPrice() + " is less than auctions current bids max price.");
                }
                // if the bids are equal sets the current bid as winner.
                if (newBid.getMaxPrice() == auctionCurrentBid.getMaxPrice()) {
                    auctionCurrentBid.setCurrentPrice(auctionCurrentBid.getMaxPrice());
                    foundAuction.setCurrentPrice(auctionCurrentBid.getMaxPrice());
                    bidsRepository.save(auctionCurrentBid);
                    auctionRepository.save(foundAuction);
                    return ResponseEntity.ok(newBid.getMaxPrice() + " is as much than auctions current bids max price. Make a new bid if you want to continue. New price is previous bids max");
                }
                // user wins
                // Checks if you can raise the current price by ten if not still wins
                if (newBid.getMaxPrice() > auctionCurrentBid.getMaxPrice()) {
                    if (auctionCurrentBid.getMaxPrice() + 10 < newBid.getMaxPrice()) {
                        newBid.setCurrentPrice(auctionCurrentBid.getCurrentPrice() + 10);
                        bidsRepository.save(newBid);
                        foundAuction.setCurrentPrice(newBid.getCurrentPrice());
                        currentBidUser.setBalance(auctionCurrentBid.getMaxPrice());
                        auctionRepository.save(foundAuction);
                        userRepository.save(currentBidUser);
                        foundUser.setBalance(newBid.getCurrentPrice());
                        userRepository.save(foundUser);
                        return ResponseEntity.ok(newBid.getCurrentPrice() + " you have the current bid.");
                    }else {
                        newBid.setCurrentPrice(auctionCurrentBid.getCurrentPrice());
                        bidsRepository.save(newBid);
                        foundAuction.setCurrentPrice(newBid.getCurrentPrice());
                        auctionRepository.save(foundAuction);
                        return ResponseEntity.ok(newBid.getCurrentPrice() + " you have the current bid.");
                    }
                }
            }

            //send back balance of lost bids
        }
        newBid.setCurrentPrice(newBid.getStartPrice());
        userRepository.save(foundUser);
        bidsRepository.save(newBid);
        foundAuction.setBid(newBid);
        foundAuction.setCurrentPrice(newBid.getCurrentPrice());
        foundAuction.setHasBids(true);
        auctionRepository.save(foundAuction);
        return ResponseEntity.ok(newBid + " Has been created, current price is" + newBid.getCurrentPrice());

     }





//Find a bids by id
    public Bids findOne(FindBidIdDTO findBidIdDTO){
        return bidsRepository.findById(findBidIdDTO.getbidId())
                .orElseThrow(()-> new RuntimeException("bidId not found"));
    }

// delete a bids
    public String deleteBids(FindBidIdDTO findBidIdDTO){
        bidsRepository.findById(findBidIdDTO.getbidId()).orElseThrow(()-> new RuntimeException("bidId not found"));
        bidsRepository.deleteById(findBidIdDTO.getbidId());
        return "Deleted successfully!";
    }

// update a bids
    public Bids updateBids(BidsDTO bidsDTO) {
        User foundUser = userRepository.findById(bidsDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("User does not exist!"));
        Auction foundAuction = auctionRepository.findById(bidsDTO.getAuctionId())
                .orElseThrow(() -> new RuntimeException("Auction does not exist!"));
        Bids newUpdate = new Bids();
        newUpdate.setAuctionId(foundAuction.getId());
        newUpdate.setUser(foundUser);
        return bidsRepository.save(newUpdate);
    }
}
