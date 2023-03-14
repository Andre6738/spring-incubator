package entelect.training.incubator.service;

import entelect.training.incubator.model.Booking;
import entelect.training.incubator.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class BookingService {
    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public Booking createBooking(Booking booking) {
        if (booking == null) {
            throw new IllegalArgumentException("Booking cannot be null");
        }
        return bookingRepository.save(booking);
    }

    public Booking getBooking(Integer id) {
        try {
            Optional<Booking> bookingOptional = bookingRepository.findById(id);
            return bookingOptional.orElseThrow(NoSuchElementException::new);
        } catch (NoSuchElementException e) {
            throw new IllegalArgumentException("Invalid booking ID: " + id, e);
        }
    }

    public List<Booking> findByReferenceNumber(String referenceNumber) {
        return bookingRepository.findByReferenceNumber(referenceNumber);
    }

    public List<Booking> findByCustomerId(Integer customerId) {
        return bookingRepository.findByCustomerId(customerId);
    }
}