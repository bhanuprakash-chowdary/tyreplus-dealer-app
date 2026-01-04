package com.tyreplus.dealer.infrastructure.config;

import com.tyreplus.dealer.domain.entity.Lead;
import com.tyreplus.dealer.domain.entity.LeadStatus;
import com.tyreplus.dealer.domain.repository.LeadRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Seeds sample lead data into the database on startup if no leads exist.
 */
@Component
@RequiredArgsConstructor
@Slf4j
@Profile("local")
@Order(2)
public class LeadSeedData implements CommandLineRunner {

    private final LeadRepository leadRepository;

    @Override
    @Transactional
    public void run(String... args) {
        // Only check count; it's more efficient than loading all leads
        if (leadRepository.count() == 0) {
            log.info("Database empty. Seeding sample leads for TyrePlus...");

            List<Lead> sampleLeads = List.of(
                    createLead("Rajesh Kumar", "9876543210", "Maruti Suzuki Swift", "2022", 50),
                    createLead("Anjali Sharma", "9988776655", "Honda City", "2021", 75),
                    createLead("Amit Patel", "9123456789", "Hyundai Creta", "2023", 100),
                    createLead("Suresh Raina", "9000000001", "Toyota Fortuner", "2020", 150),
                    createLead("Priya Das", "9888877777", "Mahindra XUV700", "2023", 120),
                    createLead("Vikram Singh", "9777766666", "Tata Nexon EV", "2022", 80),
                    createLead("Neha Gupta", "9666655555", "BMW 3 Series", "2019", 250),
                    createLead("Mohit Verma", "9555544444", "Kia Seltos", "2021", 90)
            );

            leadRepository.saveAll(sampleLeads);
            leadRepository.flush();
            log.info("Successfully seeded {} leads.", sampleLeads.size());
        } else {
            log.info("Leads already exist. Skipping seed.");
        }
    }

    private Lead createLead(String name, String phone, String model, String year, int cost) {
        return Lead.builder()
//                .id(java.util.UUID.randomUUID())
                .customerName(name)
                .customerPhone(phone)
                .customerEmail(name.toLowerCase().replace(" ", ".") + "@example.com")
                .vehicleModel(model)
                .vehicleYear(year)
                .status(LeadStatus.NEW)
                .leadCost(cost)
                .createdAt(LocalDateTime.now().minusHours((long) (Math.random() * 48))) // Random time in last 2 days
                .build();
    }
}
