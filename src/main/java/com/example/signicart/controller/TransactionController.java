package com.example.signicart.controller;

import com.example.signicart.model.Transaction;
import com.example.signicart.model.User;
import com.example.signicart.repository.TransactionRepository;
import com.example.signicart.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private UserRepository userRepository;  // Injecting UserRepository

    // 1. Add a new transaction (Income or Expense)
    @PostMapping("/add")
    public Transaction addTransaction(@RequestBody Transaction transaction) {
        // Optional: Check if the same transaction already exists for the user
        List<Transaction> existingTransactions = transactionRepository.findByUserId(transaction.getUser().getId());

        for (Transaction existingTransaction : existingTransactions) {
            if (existingTransaction.getAmount().equals(transaction.getAmount()) &&
                    existingTransaction.getCategory().equalsIgnoreCase(transaction.getCategory()) &&
                    existingTransaction.getDate().equals(transaction.getDate())) {
                // Don't insert the same transaction
                return existingTransaction;
            }
        }

        // If no duplicates, save the new transaction
        return transactionRepository.save(transaction);
    }


    // 2. Search transactions for a specific user by userId
    @GetMapping("/search/{userId}")
    public List<Transaction> searchTransactions(@PathVariable Long userId) {
        List<Transaction> transactions = transactionRepository.findByUserId(userId);

        // Remove duplicate transactions based on ID
        return transactions.stream()
                .distinct() // Make sure distinct works based on ID or other criteria.
                .collect(Collectors.toList());
    }


    // 3. Retrieve all expenses for a specific user
    @GetMapping("/expenses/{userId}")
    public List<Transaction> getExpenses(@PathVariable Long userId) {
        return transactionRepository.findByUserId(userId).stream()
                .filter(transaction -> "expense".equalsIgnoreCase(transaction.getType()))
                .toList();
    }

    // 4. View all transactions
    @GetMapping("/all")
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
}
