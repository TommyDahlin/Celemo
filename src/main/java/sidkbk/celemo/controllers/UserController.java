package sidkbk.celemo.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import sidkbk.celemo.dto.order.PreviousPurchaseFromOrderDTO;
import sidkbk.celemo.dto.user.*;
import sidkbk.celemo.exceptions.EntityNotFoundException;
import sidkbk.celemo.models.Order;
import sidkbk.celemo.models.User;
import sidkbk.celemo.services.AuctionService;
import sidkbk.celemo.services.OrderService;
import sidkbk.celemo.services.UserService;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private AuctionService auctionService;
    @Autowired
    private OrderService orderService;


// USER
//////////////////////////////////////////////////////////////////////////////////////

    // Lists all active listings by UserID
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/find/activeauction")
    public ResponseEntity<?> getActiveAuction(@Valid @RequestBody FindUserIdDTO findUserIdDTO){
        try {
            return ResponseEntity.ok(auctionService.getActiveAuction(findUserIdDTO));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }

    // List of all previousPurchases by User
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/previouspurchase")
    public ResponseEntity<?> getPreviousPurchase(@RequestBody PreviousPurchaseFromOrderDTO previousPurchaseFromOrderDTO) {
        try{
            return ResponseEntity.ok(orderService.findPreviousPurchase(previousPurchaseFromOrderDTO));
        } catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // put/update // using responseEntity<?> creates a generic wildcard that can return any type of body
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/find/finishedauction")
    public ResponseEntity<?> getFinishedAuction(@Valid @RequestBody FindUserIdDTO findUserIdDTO){
        try {
            return ResponseEntity.ok(auctionService.getFinishedAuctions(findUserIdDTO));
        } catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping("/update")
    public ResponseEntity<?> updateUser(@Valid @RequestBody UpdateUserDTO updateUserDTO){
        try{
            User updatedUser = userService.updateUser(updateUserDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping ("/favourites/all")
    public ResponseEntity<?> getUserFavouritesById(@Valid @RequestBody FindUserFavouritesDTO favouritesDTO){
        return userService.getUserFavouritesById(favouritesDTO);
    }
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping ("/favourite/add")
    public ResponseEntity<?> setUserFavouritesById(@Valid @RequestBody ModifyUserFavouritesDTO addUserFavouritesDTO){
        return userService.setUserFavouritesById(addUserFavouritesDTO);
    }
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping ("/favourite/delete")
    public ResponseEntity<?> deleteUserFavouritesById(@Valid @RequestBody ModifyUserFavouritesDTO deleteUserFavouritesDTO){
        return userService.deleteUserFavouritesById(deleteUserFavouritesDTO);
    }


// ADMIN
//////////////////////////////////////////////////////////////////////////////////////

    // find all/get all accounts
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/find/all")
    public ResponseEntity<?> getAllUsers(){
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    //get average grade, find user by id, filter out what you want to get from a user
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/findfilter")
    public ResponseEntity<?> getUserFilter(@Valid @RequestBody FindUserIdandFilterDTO findUserIdandFilterDTO){
        try {
            return ResponseEntity.ok(userService.getUserFilter(findUserIdandFilterDTO));
        }catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    // find/get using id
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping ("/find-one")
    public ResponseEntity<User> getUserById(@Valid @RequestBody FindUserIdDTO findUserIdDTO){
        Optional<User> user = userService.getUserById(findUserIdDTO);
        return user.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // delete account
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete")
    public ResponseEntity<?> deleteUser(@Valid @RequestBody DeleteUserDTO deleteUserDTO){
        try{
            return userService.deleteUser(deleteUserDTO);

        }catch (EntityNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }

    }


}
