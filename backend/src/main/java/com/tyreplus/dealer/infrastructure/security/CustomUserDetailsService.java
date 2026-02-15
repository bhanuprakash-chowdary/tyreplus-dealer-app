package com.tyreplus.dealer.infrastructure.security;

import com.tyreplus.dealer.domain.repository.DealerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final DealerRepository dealerRepository;

    @Override
    public UserDetails loadUserByUsername(String mobile) throws UsernameNotFoundException {
        return dealerRepository.findByMobile(mobile)
                .map(DealerDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("Dealer not found with mobile: " + mobile));
    }
}