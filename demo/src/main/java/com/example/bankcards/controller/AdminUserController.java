package com.example.bankcards.controller;

import com.example.bankcards.dto.UserCreationDTO;
import com.example.bankcards.dto.UserDTO;
import com.example.bankcards.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/user")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;
    
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll(){
        return ResponseEntity.ok(userService.getAll());
    }

    @PostMapping
    public ResponseEntity<UserDTO> addUser(@RequestBody UserCreationDTO dto){
        return ResponseEntity.ok(
                userService.createUser(dto.username(), dto.password())
        );
    }

    @DeleteMapping
    public ResponseEntity<UserDTO> deleteUser(@RequestParam String id){
        return ResponseEntity.ok(userService.deleteUser(id));
    }
}
