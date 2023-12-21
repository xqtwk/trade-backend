package grade.tradeback.user;

import grade.tradeback.user.dto.ChangePasswordRequest;
import grade.tradeback.user.dto.UserPublicDataRequest;
import grade.tradeback.user.dto.UserPrivateDataResponse;
import grade.tradeback.user.entity.User;
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
        UserPublicDataRequest userPublicDataRequest = new UserPublicDataRequest(
                user.getId(),
                user.getUsername());
        return ResponseEntity.ok(userPublicDataRequest);
    }

    @GetMapping("/get-private-data")
    public ResponseEntity<UserPrivateDataResponse> getPrivateUserData(Principal connectedUser) {
        UserPrivateDataResponse userPrivateDataResponse = userService.getPrivateUserData(connectedUser);
        return ResponseEntity.ok(userPrivateDataResponse);
    }


    @GetMapping("/settings")
    public ResponseEntity<UserPrivateDataResponse> getUserProfile(Principal principal) {
        UserPrivateDataResponse userPrivateDataResponse = userService.getPrivateUserData(principal);
        return ResponseEntity.ok(userPrivateDataResponse);
    }


}
