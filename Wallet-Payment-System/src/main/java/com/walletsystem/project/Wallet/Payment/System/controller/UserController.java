package com.walletsystem.project.Wallet.Payment.System.controller;

import com.walletsystem.project.Wallet.Payment.System.entity.User;
import com.walletsystem.project.Wallet.Payment.System.repository.UserRepository;
import com.walletsystem.project.Wallet.Payment.System.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserRepository userRepository;
    private final WalletService walletService; // inject WalletService

    // 🟢 Get all users
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userRepository.findAll());
    }

    // 🟢 Freeze user (wallet)
    @PutMapping("/{id}/freeze")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> freezeUser(@PathVariable Long id) {
        return userRepository.findById(id).map(user -> {
            // freeze wallet and send notification
            walletService.freezeWallet(user);

            // save active status in User
            userRepository.save(user);

            return ResponseEntity.ok("User " + user.getUsername() + " wallet has been frozen.");
        }).orElse(ResponseEntity.notFound().build());
    }

    // 🟢 Unfreeze user (wallet)
    @PutMapping("/{id}/unfreeze")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> unfreezeUser(@PathVariable Long id) {
        return userRepository.findById(id).map(user -> {
            // unfreeze wallet and send notification
            walletService.unfreezeWallet(user);

            // save active status in User
            userRepository.save(user);

            return ResponseEntity.ok("User " + user.getUsername() + " wallet has been unfrozen.");
        }).orElse(ResponseEntity.notFound().build());
    }
}
