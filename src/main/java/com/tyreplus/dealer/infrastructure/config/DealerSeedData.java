package com.tyreplus.dealer.infrastructure.config;

import com.tyreplus.dealer.domain.entity.Dealer;
import com.tyreplus.dealer.domain.entity.Wallet;
import com.tyreplus.dealer.domain.repository.DealerRepository;
import com.tyreplus.dealer.domain.repository.WalletRepository;
import com.tyreplus.dealer.domain.valueobject.Address;
import com.tyreplus.dealer.domain.valueobject.BusinessHours;
import com.tyreplus.dealer.domain.valueobject.ContactDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
@Component
@RequiredArgsConstructor
@Profile("local")
@Order(1)
public class DealerSeedData implements CommandLineRunner {

    private final DealerRepository dealerRepository;
    private final WalletRepository walletRepository;

    // Formatter to convert "09:00 AM" string to a Java LocalTime object
    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("hh:mm a", java.util.Locale.ENGLISH);

    @Override
    @Transactional
    public void run(String... args) {
        String testMobile = "9876543210";

        if (!dealerRepository.existsByMobile(testMobile)) {

            Address address = new Address(
                    "123, MG Road", "Bangalore", "Karnataka", "560001", "India"
            );

            ContactDetails contact = new ContactDetails(
                    "dealer@supertyres.com", testMobile, testMobile
            );

            // FIX: Parse the Strings into LocalTime objects
            BusinessHours hours = new BusinessHours(
                    LocalTime.parse("09:00 AM", TIME_FORMATTER),
                    LocalTime.parse("09:00 PM", TIME_FORMATTER),
                    false
            );

            Dealer dealer = Dealer.builder()
                    .businessName("Super Tyres Ltd.")
                    .ownerName("Rajesh Kumar")
                    .contactDetails(contact)
                    .address(address)
                    .businessHours(hours)
                    .isVerified(true)
                    .build();

            Dealer savedDealer = dealerRepository.save(dealer);
            walletRepository.save(new Wallet(savedDealer.getId(), 5000));

            System.out.println("✅ Seeded Dealer: " + savedDealer.getBusinessName());
        }
    }
}