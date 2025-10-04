package com.example.bankcards.controller;

import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {


    private final UserService userService;

    @PostMapping("/token")
    public ResponseEntity<String> getToken(@RequestBody UserCreationDTO dto){
        return ResponseEntity.ok(userService.getToken(dto.username(), dto.password()));
    }

    @GetMapping("/get-all")
    public ResponseEntity<List<UserDTO>> getAll(){
        return ResponseEntity.ok(userService.getAll());
    }

    @PostMapping("/create")
    public ResponseEntity<UserDTO> addUser(@RequestBody UserCreationDTO dto){
        return ResponseEntity.ok(
                userService.createUser(dto.username(), dto.password())
        );
    }

    @DeleteMapping("/delete")
    public ResponseEntity<UserDTO> deleteUser(@RequestParam String id){
        return ResponseEntity.ok(userService.deleteUser(id));
    }
}
