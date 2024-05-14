package sidkbk.celemo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import sidkbk.celemo.dto.reports.*;
import sidkbk.celemo.models.Auction;
import sidkbk.celemo.models.Reports;
import sidkbk.celemo.models.User;
import sidkbk.celemo.repositories.AuctionRepository;
import sidkbk.celemo.repositories.ReportsRepository;
import sidkbk.celemo.repositories.UserRepository;

import java.util.List;

@Service
public class ReportsServices {

    @Autowired
    ReportsRepository reportsRepository;
    @Autowired
    AuctionRepository auctionRepository;
    @Autowired
    UserRepository userRepository;


    //post , create report for user
    public ResponseEntity<Reports> createReportUser(ReportsUserDTO reportsDTO) {
        User foundReportedUser = userRepository.findById(reportsDTO.getReportedUserId()) // this could be a problem
                .orElseThrow(() -> new RuntimeException("User does not exist!"));
        User foundReportingUser = userRepository.findById(reportsDTO.getReportingUserId()) // this could be a problem
                .orElseThrow(() -> new RuntimeException("User does not exist!"));
        //reportsDTO.setReportedUserId(reportsDTO.getReportingUserId());
        //reportsDTO.setReportingUserId(reportsDTO.getReportingUserId());
        Reports newReport = new Reports();
        newReport.setReportedUserId(foundReportedUser);
        newReport.setReportingUserId(foundReportingUser);
        newReport.setContent(reportsDTO.getContent());

        return ResponseEntity.ok(reportsRepository.save(newReport));
    }


    //post , create report for auction
    public ResponseEntity<?> createReportAuction(ReportsAuctionDTO reportsAuctionDTO) {
        User foundReportingAuction = userRepository.findById(reportsAuctionDTO.getReportingUserId()) // this could be a problem
                .orElseThrow(() -> new RuntimeException("Auction does not exist!"));
        Auction foundAuction = auctionRepository.findById(reportsAuctionDTO.getAuctionId()) // this could be a problem
                .orElseThrow(() -> new RuntimeException("Auction does not exist!"));
        //reportsAuctionDTO.setReportingUserId(reportsAuctionDTO.getReportingUserId());
        //reportsAuctionDTO.setAuction(reportsAuctionDTO.getAuction());

        Reports newReport = new Reports();
        newReport.setReportingUserId(foundReportingAuction);
        newReport.setAuction(foundAuction); // this could be a problem
        newReport.setContent(reportsAuctionDTO.getContent());

        return ResponseEntity.ok(reportsRepository.save(newReport));
    }

    //get , find all reports
    public List<Reports> findAllReports() {
        return reportsRepository.findAll();
    }

    //get , find one report
    public ResponseEntity<?> findOne(String reportsId) {
        Reports foundReport = reportsRepository.findById(reportsId)
                .orElseThrow(() -> new RuntimeException("Order was not found"));
        return ResponseEntity.ok(foundReport);
    }

    //put, update report
    public ResponseEntity<?> updateReport(ReportsPutDTO reportsPutDTO) {
        return reportsRepository.findById(reportsPutDTO.getReportsId())
                .map(existingOrder -> {
                    if (reportsPutDTO.getContent() != null) {
                        existingOrder.setContent(reportsPutDTO.getContent());
                    }
                    return ResponseEntity.ok(reportsRepository.save(existingOrder));
                }).orElseThrow(() -> new RuntimeException("Order was not found"));
    }

    // DELETE ( Delete report using reportsId(body) )
    public ResponseEntity<?> deleteReport(ReportsDeleteDTO reportsDeleteDTO) {
            reportsRepository.findById(reportsDeleteDTO.reportsId) // Check if report exist
                    .orElseThrow(() -> new RuntimeException("Report does not exist!"));
            reportsRepository.deleteById(reportsDeleteDTO.getReportsId());
            return ResponseEntity.ok(reportsDeleteDTO.getReportsId() + " Was deleted!");

    }
}