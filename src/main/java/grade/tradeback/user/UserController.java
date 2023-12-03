package grade.tradeback.user;

import grade.tradeback.user.dto.ChangePasswordRequest;
import grade.tradeback.user.dto.UserPublicDataRequest;
import grade.tradeback.user.dto.UserDTO;
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

    @GetMapping("/{username}")
    public ResponseEntity<UserPublicDataRequest> getPublicUserProfile(@PathVariable String username) {
        Optional<User> optionalUser = userRepository.findByUsername(username);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = optionalUser.get();
        UserPublicDataRequest userPublicDataRequest = new UserPublicDataRequest(user.getId(), user.getUsername());
        return ResponseEntity.ok(userPublicDataRequest);
    }

    @GetMapping("/settings")
    public ResponseEntity<UserDTO> getUserProfile(Principal principal) {
        User user = (User) ((UsernamePasswordAuthenticationToken) principal).getPrincipal();
        UserDTO userDto = new UserDTO(user.getId(), user.getUsername(), user.getEmail(), user.getRole(), user.getBalance(), user.isMfaEnabled());
        return ResponseEntity.ok(userDto);
    }
}
