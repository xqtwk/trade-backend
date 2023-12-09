package grade.tradeback.user;

import grade.tradeback.user.dto.ChangePasswordRequest;
import grade.tradeback.user.dto.UserPublicDataRequest;
import grade.tradeback.user.dto.UserPrivateDataRequest;
import grade.tradeback.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final UserRepository userRepository;

    @PatchMapping
    public ResponseEntity<?> changePassword(
            @RequestBody ChangePasswordRequest request,
            Principal connectedUser
    ) {
        userService.changePassword(request, connectedUser);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/get-public-data/{username}")
    public ResponseEntity<UserPublicDataRequest> getPublicUserData(@PathVariable String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = optionalUser.get();
        UserPublicDataRequest userPublicDataRequest = new UserPublicDataRequest(user.getUsername());
        return ResponseEntity.ok(userPublicDataRequest);
    }

    @GetMapping("/get-private-data")
    public ResponseEntity<UserPrivateDataRequest> getPrivateUserData(Principal connectedUser) {
        User user = (User) ((UsernamePasswordAuthenticationToken) connectedUser).getPrincipal();
        UserPrivateDataRequest userPrivateDataRequest = new UserPrivateDataRequest(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole(),
                user.getBalance(),
                user.isMfaEnabled()
        );
        return ResponseEntity.ok(userPrivateDataRequest);
    }


    @GetMapping("/settings")
    public ResponseEntity<UserPrivateDataRequest> getUserProfile(Principal principal) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        UserPrivateDataRequest userPrivateDataRequest = new UserPrivateDataRequest(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.getBalance(), user.isMfaEnabled());
        return ResponseEntity.ok(userPrivateDataRequest);
    }


}
