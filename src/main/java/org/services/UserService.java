package org.services;

import org.models.User;
import org.models.enums.Rol;
import org.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Called by Spring Security on every login attempt
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword())
                .authorities(new SimpleGrantedAuthority("ROLE_" + user.getRol().name()))
                .build();
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Transactional
    public User save(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already registered: " + user.getEmail());
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    @Transactional
    public User update(Long id, User data) {
        User existing = findById(id);
        existing.setName(data.getName());
        existing.setEmail(data.getEmail());
        existing.setRol(data.getRol());
        existing.setActive(data.isActive());

        // Only re-encode password if a new one was provided
        if (data.getPassword() != null && !data.getPassword().isBlank()) {
            existing.setPassword(passwordEncoder.encode(data.getPassword()));
        }
        return userRepository.save(existing);
    }

    @Transactional
    public void delete(Long id) {
        userRepository.delete(findById(id));
    }

    @Transactional
    public boolean changePassword(String email, String currentRaw, String newRaw) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
        if (!passwordEncoder.matches(currentRaw, user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newRaw));
        userRepository.save(user);
        return true;
    }

    @Transactional(readOnly = true)
    public List<User> findByRol(Rol rol) {
        return userRepository.findAll().stream()
                .filter(u -> u.getRol() == rol)
                .toList();
    }
}