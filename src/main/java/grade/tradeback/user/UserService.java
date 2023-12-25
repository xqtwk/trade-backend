package grade.tradeback.user;

import grade.tradeback.payments.transaction.Transaction;
import grade.tradeback.payments.transaction.TransactionDto;
import grade.tradeback.user.dto.ChangePasswordRequest;
import grade.tradeback.user.dto.UserPrivateDataResponse;
import grade.tradeback.user.entity.User;
import jakarta.persistence.EntityNotFoundException;

import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public void changePassword(ChangePasswordRequest request, Principal connectedUser) {
        var user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalStateException("Wrong password");
        }
        if (!request.getNewPassword().equals(request.getConfirmationPassword())) {
            throw new IllegalStateException("Password confirmation failed");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));

        userRepository.save(user);
    }
    public String getUsernameById(Long id) {
        return userRepository.findById(id)
                .map(User::getUsername)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Transactional
    public void addTransactionToUser(String username, Transaction newTransaction) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User not found"));
        newTransaction.setUser(user);
        user.getTransactions().add(newTransaction);
        //         userRepository.save(user);
    }


    public List<TransactionDto> getTransactionDTOsForUser(User user) {
        return user.getTransactions().stream()
                .map(transaction -> new TransactionDto(
                        transaction.getId(),
                        transaction.getOperationId(),
                        transaction.getType(),
                        transaction.getCheckoutId(),
                        transaction.getAmount(),
                        transaction.getStatus()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public UserPrivateDataResponse getPrivateUserData(Principal connectedUser) {
        User principalUser = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        User user = userRepository.findByUsername(principalUser.getUsername())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Hibernate.initialize(user.getTransactions());

        List<TransactionDto> transactionDTOs = getTransactionDTOsForUser(user);

        return new UserPrivateDataResponse(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getBalance(),
                user.isMfaEnabled(),
                transactionDTOs
        );
    }

    public void addBalance(String username, double amount) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            BigDecimal currentBalance = BigDecimal.valueOf(user.getBalance());
            BigDecimal amountToAdd = BigDecimal.valueOf(amount);
            BigDecimal newBalance = currentBalance.add(amountToAdd).setScale(2, RoundingMode.HALF_DOWN);

            user.setBalance(newBalance.doubleValue());
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    public void removeBalance(String username, double amount) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            BigDecimal currentBalance = BigDecimal.valueOf(user.getBalance());
            BigDecimal amountToRemove = BigDecimal.valueOf(amount);

            if (currentBalance.compareTo(amountToRemove) >= 0) {
                BigDecimal newBalance = currentBalance.subtract(amountToRemove).setScale(2, RoundingMode.HALF_DOWN);

               /* String formatted = String.format("%.2f", user.getBalance() - amount);
                user.setBalance(Double.parseDouble(formatted));
                userRepository.save(user);*/
                user.setBalance(newBalance.doubleValue());
                userRepository.save(user);
            }
            else {
                throw new IllegalArgumentException("Insufficient balance");
            }
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }
}
