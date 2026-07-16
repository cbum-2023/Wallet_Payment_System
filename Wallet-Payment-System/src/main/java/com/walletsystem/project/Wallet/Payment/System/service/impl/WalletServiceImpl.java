package com.walletsystem.project.Wallet.Payment.System.service.impl;

import com.walletsystem.project.Wallet.Payment.System.dto.NotificationMessage;
import com.walletsystem.project.Wallet.Payment.System.entity.User;
import com.walletsystem.project.Wallet.Payment.System.entity.Wallet;
import com.walletsystem.project.Wallet.Payment.System.entity.WalletTransaction;
import com.walletsystem.project.Wallet.Payment.System.repository.WalletRepository;
import com.walletsystem.project.Wallet.Payment.System.repository.WalletTransactionRepository;
import com.walletsystem.project.Wallet.Payment.System.service.WalletService;
import com.walletsystem.project.Wallet.Payment.System.service.NotificationProducer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;
    private final WalletTransactionRepository transactionRepository;
    private final NotificationProducer notificationProducer;

    @Override
    @Transactional
    public Wallet topUp(User user, BigDecimal amount) {
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
        wallet.setBalance(wallet.getBalance().add(amount));
        walletRepository.save(wallet);

        transactionRepository.save(WalletTransaction.builder()
                .wallet(wallet)
                .type("TOPUP")
                .amount(amount)
                .note("Top-up")
                .build());

        // send notification
        notificationProducer.sendNotification(
                new NotificationMessage(
                        user.getEmail(),
                        "Wallet Top-up Successful",
                        "Your wallet has been credited with ₹" + amount
                )
        );

        return wallet;
    }

    @Override
    @Transactional
    public Wallet withdraw(User user, BigDecimal amount) {
        Wallet wallet = walletRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        if (wallet.getBalance().compareTo(amount) < 0) {
            notificationProducer.sendNotification(
                    new NotificationMessage(
                            user.getEmail(),
                            "Withdrawal Failed",
                            "Your withdrawal of ₹" + amount + " failed due to insufficient balance."
                    )
            );
            throw new RuntimeException("Insufficient balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(amount));
        walletRepository.save(wallet);

        transactionRepository.save(WalletTransaction.builder()
                .wallet(wallet)
                .type("WITHDRAW")
                .amount(amount)
                .note("Withdraw")
                .build());

        // send notification
        notificationProducer.sendNotification(
                new NotificationMessage(
                        user.getEmail(),
                        "Withdrawal Successful",
                        "You have successfully withdrawn ₹" + amount
                )
        );

        return wallet;
    }

    @Override
    @Transactional
    public void transfer(User fromUser, User toUser, BigDecimal amount) {
        Wallet fromWallet = walletRepository.findByUserId(fromUser.getId())
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));
        Wallet toWallet = walletRepository.findByUserId(toUser.getId())
                .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

        if (fromWallet.getBalance().compareTo(amount) < 0) {
            notificationProducer.sendNotification(
                    new NotificationMessage(
                            fromUser.getEmail(),
                            "Transfer Failed",
                            "Your transfer of ₹" + amount + " to userId " + toUser.getId() + " failed due to insufficient balance."
                    )
            );
            throw new RuntimeException("Insufficient balance");
        }

        fromWallet.setBalance(fromWallet.getBalance().subtract(amount));
        walletRepository.save(fromWallet);

        toWallet.setBalance(toWallet.getBalance().add(amount));
        walletRepository.save(toWallet);

        // log transactions
        transactionRepository.save(WalletTransaction.builder()
                .wallet(fromWallet)
                .type("TRANSFER")
                .amount(amount)
                .note("Transferred to userId " + toUser.getId())
                .build());

        transactionRepository.save(WalletTransaction.builder()
                .wallet(toWallet)
                .type("TRANSFER")
                .amount(amount)
                .note("Received from userId " + fromUser.getId())
                .build());

        // send notifications
        notificationProducer.sendNotification(
                new NotificationMessage(
                        fromUser.getEmail(),
                        "Transfer Successful",
                        "You have successfully transferred ₹" + amount + " to userId " + toUser.getId()
                )
        );

        notificationProducer.sendNotification(
                new NotificationMessage(
                        toUser.getEmail(),
                        "Wallet Credited",
                        "You have received ₹" + amount + " from userId " + fromUser.getId()
                )
        );
    }

    @Override
    public Wallet getWalletByUserId(Long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));
    }

    // freeze/unfreeze using User.active
    @Transactional
    public void freezeWallet(User user) {
        user.setActive(false); // freeze wallet
        notificationProducer.sendNotification(
                new NotificationMessage(
                        user.getEmail(),
                        "Wallet Frozen",
                        "Your wallet has been frozen due to suspicious activity."
                )
        );
    }

    @Transactional
    public void unfreezeWallet(User user) {
        user.setActive(true); // unfreeze wallet
        notificationProducer.sendNotification(
                new NotificationMessage(
                        user.getEmail(),
                        "Wallet Unfrozen",
                        "Your wallet is now active and available."
                )
        );
    }
}