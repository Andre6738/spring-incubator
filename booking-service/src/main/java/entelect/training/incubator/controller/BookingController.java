package entelect.training.incubator.controller;

import entelect.training.incubator.model.Booking;
import entelect.training.incubator.service.BookingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("bookings")
public class BookingController {

    private final Logger LOGGER = LoggerFactory.getLogger(BookingController.class);

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<?> createBooking(@RequestBody Booking booking) {
        LOGGER.info("Processing customer creation request for booking={}", booking);

        final Booking savedBooking = bookingService.createBooking(booking);

        LOGGER.trace("Customer created");
        return new ResponseEntity<>(savedBooking, HttpStatus.CREATED);
    }

    @GetMapping("{id}")
    public ResponseEntity<?> getBooking(@PathVariable Integer id) {
        LOGGER.info("Processing booking search request for booking id={}", id);
        Booking booking = this.bookingService.getBooking(id);

        if (booking != null) {
            LOGGER.trace("Found booking");
            return new ResponseEntity<>(booking, HttpStatus.OK);
        }

        LOGGER.trace("Booking not found");
        return ResponseEntity.notFound().build();
    }

    @PostMapping("/search")
    public ResponseEntity<?> search(@RequestBody Map<String, Object> searchRequest) {
        LOGGER.info("Processing search request for request {}", searchRequest);

        List<Booking> bookings;
        Integer customerId = (Integer) searchRequest.get("customerId");
        if (customerId != null) {
            bookings = bookingService.findByCustomerId(customerId);
        } else {
            String referenceNumber = (String) searchRequest.get("referenceNumber");
            if (referenceNumber != null) {
                bookings = bookingService.findByReferenceNumber(referenceNumber);
            } else {
                return ResponseEntity.badRequest().body("Invalid search request");
            }
        }

        if (!bookings.isEmpty()) {
            return ResponseEntity.ok(bookings);
        }

        LOGGER.trace("Bookings not found");
        return ResponseEntity.notFound().build();
    }
}